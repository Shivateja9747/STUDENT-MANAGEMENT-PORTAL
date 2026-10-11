package com.school.management.service;

import com.school.management.config.AttendanceConfig;
import com.school.management.config.GradingConfig;
import com.school.management.dto.*;
import com.school.management.entity.*;
import com.school.management.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final AttendanceRepository attendanceRepository;
    private final MarksRepository marksRepository;
    private final SubjectRepository subjectRepository;

    public StudentService(StudentRepository studentRepository, AttendanceRepository attendanceRepository, MarksRepository marksRepository, SubjectRepository subjectRepository) {
        this.studentRepository = studentRepository;
        this.attendanceRepository = attendanceRepository;
        this.marksRepository = marksRepository;
        this.subjectRepository = subjectRepository;
    }

    public Student getStudentByUsername(String username) {
        return studentRepository.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Student profile not found for user: " + username));
    }

    public List<StudentSubjectAttendanceDto> getStudentAttendance(String username) {
        Student student = getStudentByUsername(username);
        List<Attendance> attendances = attendanceRepository.findByStudentId(student.getId());

        Map<Subject, List<Attendance>> subjectGroup = attendances.stream()
                .collect(Collectors.groupingBy(Attendance::getSubject));

        List<StudentSubjectAttendanceDto> result = new ArrayList<>();

        for (Map.Entry<Subject, List<Attendance>> entry : subjectGroup.entrySet()) {
            Subject subject = entry.getKey();
            List<Attendance> list = entry.getValue();

            int total = list.size();
            int present = (int) list.stream().filter(a -> a.getStatus() == AttendanceStatus.PRESENT).count();
            int late = (int) list.stream().filter(a -> a.getStatus() == AttendanceStatus.LATE).count();
            int leave = (int) list.stream().filter(a -> a.getStatus() == AttendanceStatus.LEAVE).count();
            int absent = (int) list.stream().filter(a -> a.getStatus() == AttendanceStatus.ABSENT).count();

            int attendedCount = present + (AttendanceConfig.COUNT_LATE_AS_PRESENT ? late : 0);
            int denominator = total - (AttendanceConfig.EXCLUDE_LEAVE_FROM_DENOMINATOR ? leave : 0);

            double pct = denominator > 0 ? round(((double) attendedCount / denominator) * 100.0) : 100.0;
            boolean isLow = pct < AttendanceConfig.DEFAULT_ATTENDANCE_THRESHOLD;

            result.add(StudentSubjectAttendanceDto.builder()
                    .subjectId(subject.getId())
                    .subjectCode(subject.getCode())
                    .subjectName(subject.getName())
                    .totalClasses(total)
                    .presentClasses(attendedCount)
                    .absentClasses(absent)
                    .percentage(pct)
                    .lowAttendance(isLow)
                    .build());
        }

        return result;
    }

    public List<StudentMarksDto> getStudentMarks(String username) {
        Student student = getStudentByUsername(username);
        List<Marks> marksList = marksRepository.findByStudentId(student.getId()).stream()
                .filter(m -> Boolean.TRUE.equals(m.getPublished()))
                .collect(Collectors.toList());

        return marksList.stream()
                .map(m -> {
                    boolean isAbs = Boolean.TRUE.equals(m.getIsAbsent());
                    double pct = (!isAbs && m.getMaxMarks() > 0) ? round((m.getMarksObtained() / m.getMaxMarks()) * 100.0) : 0.0;
                    String grade = isAbs ? "ABSENT" : GradingConfig.calculateGrade(pct);
                    boolean isPass = !isAbs && GradingConfig.isPass(pct);
                    double gradePoint = isAbs ? 0.0 : GradingConfig.getGradePoint(grade);

                    return StudentMarksDto.builder()
                            .id(m.getId())
                            .subjectId(m.getSubject().getId())
                            .subjectCode(m.getSubject().getCode())
                            .subjectName(m.getSubject().getName())
                            .examType(m.getExamType())
                            .marksObtained(m.getMarksObtained())
                            .maxMarks(m.getMaxMarks())
                            .percentage(pct)
                            .grade(grade)
                            .isPassed(isPass)
                            .isAbsent(isAbs)
                            .gradePoint(gradePoint)
                            .build();
                })
                .collect(Collectors.toList());
    }

    public StudentPerformanceSummaryDto getStudentPerformance(String username) {
        Student student = getStudentByUsername(username);
        List<StudentSubjectAttendanceDto> attendanceList = getStudentAttendance(username);
        List<StudentMarksDto> marksList = getStudentMarks(username);

        int totalClassesAll = attendanceList.stream().mapToInt(StudentSubjectAttendanceDto::getTotalClasses).sum();
        int totalPresentAll = attendanceList.stream().mapToInt(StudentSubjectAttendanceDto::getPresentClasses).sum();
        double overallAttendance = totalClassesAll > 0 ? round((double) totalPresentAll / totalClassesAll * 100.0) : 100.0;

        boolean lowAttendanceWarning = attendanceList.stream().anyMatch(StudentSubjectAttendanceDto::isLowAttendance) || (totalClassesAll > 0 && overallAttendance < AttendanceConfig.DEFAULT_ATTENDANCE_THRESHOLD);

        List<StudentMarksDto> validMarks = marksList.stream().filter(m -> !m.isAbsent()).collect(Collectors.toList());
        double totalObtained = validMarks.stream().mapToDouble(StudentMarksDto::getMarksObtained).sum();
        double totalMax = validMarks.stream().mapToDouble(StudentMarksDto::getMaxMarks).sum();
        double overallMarksPct = totalMax > 0 ? round((totalObtained / totalMax) * 100.0) : 0.0;

        double sgpa = validMarks.size() > 0 ? round(validMarks.stream().mapToDouble(StudentMarksDto::getGradePoint).average().orElse(0.0)) : 0.0;
        String gpaGrade = GradingConfig.calculateGrade(overallMarksPct);

        int totalSubjectsCount = subjectRepository.findAll().stream()
                .filter(sub -> sub.getSemester().equals(student.getSemester()))
                .collect(Collectors.toList()).size();

        return StudentPerformanceSummaryDto.builder()
                .studentId(student.getId())
                .studentName(student.getName())
                .rollNo(student.getRollNo())
                .department(student.getDepartment())
                .semester(student.getSemester())
                .overallAttendancePercentage(overallAttendance)
                .hasLowAttendanceWarning(lowAttendanceWarning)
                .overallMarksPercentage(overallMarksPct)
                .sgpa(sgpa)
                .gpaGrade(gpaGrade)
                .totalSubjects(totalSubjectsCount)
                .subjectAttendances(attendanceList)
                .marksList(marksList)
                .build();
    }

    // PDF Report Card Generator
    public byte[] generateReportCardPdf(String username) {
        StudentPerformanceSummaryDto summary = getStudentPerformance(username);

        StringBuilder sb = new StringBuilder();
        sb.append("===============================================================\n");
        sb.append("         OFFICIAL ACADEMIC PERFORMANCE REPORT CARD             \n");
        sb.append("===============================================================\n\n");
        sb.append("Student Name : ").append(summary.getStudentName()).append("\n");
        sb.append("Roll Number  : ").append(summary.getRollNo()).append("\n");
        sb.append("Department   : ").append(summary.getDepartment()).append("\n");
        sb.append("Semester     : ").append(summary.getSemester()).append("\n");
        sb.append("Date Issued  : ").append(new java.util.Date()).append("\n\n");
        sb.append("---------------------------------------------------------------\n");
        sb.append("SUMMARY STATISTICS\n");
        sb.append("---------------------------------------------------------------\n");
        sb.append("Overall Attendance : ").append(summary.getOverallAttendancePercentage()).append("%\n");
        sb.append("Overall Marks      : ").append(summary.getOverallMarksPercentage()).append("%\n");
        sb.append("SGPA Estimate      : ").append(summary.getSgpa()).append(" / 10.0 (Grade ").append(summary.getGpaGrade()).append(")\n\n");
        sb.append("---------------------------------------------------------------\n");
        sb.append("EXAMINATION MARKS TRANSCRIPT\n");
        sb.append("---------------------------------------------------------------\n");
        sb.append(String.format("%-12s %-20s %-10s %-10s %-8s %-6s\n", "CODE", "SUBJECT", "EXAM", "SCORE", "PCT%", "GRADE"));
        sb.append("---------------------------------------------------------------\n");

        for (StudentMarksDto m : summary.getMarksList()) {
            sb.append(String.format("%-12s %-20s %-10s %-10s %-8.1f %-6s\n",
                    m.getSubjectCode(),
                    m.getSubjectName().length() > 20 ? m.getSubjectName().substring(0, 17) + "..." : m.getSubjectName(),
                    m.getExamType(),
                    m.isAbsent() ? "ABSENT" : (m.getMarksObtained() + "/" + m.getMaxMarks()),
                    m.getPercentage(),
                    m.getGrade()));
        }

        sb.append("---------------------------------------------------------------\n");
        sb.append("Status: ").append(summary.isHasLowAttendanceWarning() ? "ATTENDANCE WARNING (<75%)" : "GOOD ACADEMIC STANDING").append("\n");
        sb.append("===============================================================\n");

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    private double round(double val) {
        BigDecimal bd = BigDecimal.valueOf(val);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
