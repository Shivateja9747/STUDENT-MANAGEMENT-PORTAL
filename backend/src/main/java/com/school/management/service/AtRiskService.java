package com.school.management.service;

import com.school.management.config.GradingConfig;
import com.school.management.dto.AtRiskScoreDto;
import com.school.management.entity.*;
import com.school.management.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AtRiskService {

    private final StudentRepository studentRepository;
    private final AttendanceRepository attendanceRepository;
    private final MarksRepository marksRepository;

    public AtRiskService(StudentRepository studentRepository, AttendanceRepository attendanceRepository, MarksRepository marksRepository) {
        this.studentRepository = studentRepository;
        this.attendanceRepository = attendanceRepository;
        this.marksRepository = marksRepository;
    }

    public AtRiskScoreDto calculateStudentRisk(Student student) {
        List<Attendance> attendances = attendanceRepository.findByStudentId(student.getId());
        AttendanceCalculator.CalculationResult attCalc = AttendanceCalculator.calculate(attendances);

        List<Marks> publishedMarks = marksRepository.findByStudentId(student.getId()).stream()
                .filter(m -> Boolean.TRUE.equals(m.getPublished()))
                .collect(Collectors.toList());

        int score = 0;
        List<String> reasons = new ArrayList<>();

        // 1. Attendance score factor
        double attPct = attCalc.getPercentage();
        if (attPct < 65.0) {
            score += 60;
            reasons.add("Critically low attendance (" + attPct + "% < 65%)");
        } else if (attPct < 75.0) {
            score += 40;
            reasons.add("Low attendance warning (" + attPct + "% < 75%)");
        }

        // 2. 4-week trend factor
        LocalDate today = LocalDate.now();
        LocalDate twoWeeksAgo = today.minusWeeks(2);
        LocalDate fourWeeksAgo = today.minusWeeks(4);

        long recentTotal = attendances.stream().filter(a -> !a.getDate().isBefore(twoWeeksAgo)).count();
        long recentPresent = attendances.stream().filter(a -> !a.getDate().isBefore(twoWeeksAgo) && (a.getStatus() == AttendanceStatus.PRESENT || a.getStatus() == AttendanceStatus.LATE)).count();
        double recentPct = recentTotal > 0 ? ((double) recentPresent / recentTotal) * 100.0 : attPct;

        long priorTotal = attendances.stream().filter(a -> !a.getDate().isBefore(fourWeeksAgo) && a.getDate().isBefore(twoWeeksAgo)).count();
        long priorPresent = attendances.stream().filter(a -> !a.getDate().isBefore(fourWeeksAgo) && a.getDate().isBefore(twoWeeksAgo) && (a.getStatus() == AttendanceStatus.PRESENT || a.getStatus() == AttendanceStatus.LATE)).count();
        double priorPct = priorTotal > 0 ? ((double) priorPresent / priorTotal) * 100.0 : attPct;

        String trendMsg = "Stable attendance trend";
        if (priorTotal > 0 && recentTotal > 0 && recentPct < priorPct - 5.0) {
            score += 15;
            trendMsg = "Declining attendance trend over past 4 weeks (" + Math.round(priorPct) + "% -> " + Math.round(recentPct) + "%)";
            reasons.add(trendMsg);
        }

        // 3. Marks factor
        double totalObtained = publishedMarks.stream().filter(m -> !Boolean.TRUE.equals(m.getIsAbsent())).mapToDouble(Marks::getMarksObtained).sum();
        double totalMax = publishedMarks.stream().filter(m -> !Boolean.TRUE.equals(m.getIsAbsent())).mapToDouble(Marks::getMaxMarks).sum();
        double marksPct = totalMax > 0 ? (totalObtained / totalMax) * 100.0 : 100.0;

        if (totalMax > 0 && marksPct < 40.0) {
            score += 30;
            reasons.add("Critically low academic performance (" + Math.round(marksPct) + "% < 40%)");
        } else if (totalMax > 0 && marksPct < 50.0) {
            score += 20;
            reasons.add("Below average academic marks (" + Math.round(marksPct) + "% < 50%)");
        }

        // 4. Failed or absent exams
        int failedExams = 0;
        for (Marks m : publishedMarks) {
            if (Boolean.TRUE.equals(m.getIsAbsent())) {
                failedExams++;
            } else if (m.getMaxMarks() > 0) {
                double pct = (m.getMarksObtained() / m.getMaxMarks()) * 100.0;
                if (!GradingConfig.isPass(pct)) {
                    failedExams++;
                }
            }
        }

        if (failedExams > 0) {
            int failPts = Math.min(30, failedExams * 10);
            score += failPts;
            reasons.add("Failed or absent in " + failedExams + " examination(s)");
        }

        score = Math.min(100, Math.max(0, score));

        String level = "Low";
        if (score >= 61) level = "High";
        else if (score >= 31) level = "Medium";

        if (reasons.isEmpty()) {
            reasons.add("Consistent attendance and good academic standing.");
        }

        return AtRiskScoreDto.builder()
                .studentId(student.getId())
                .studentName(student.getName())
                .rollNo(student.getRollNo())
                .department(student.getDepartment())
                .semester(student.getSemester())
                .section(student.getSection())
                .riskScore(score)
                .riskLevel(level)
                .attendancePercentage(attPct)
                .marksPercentage(Math.round(marksPct * 10.0) / 10.0)
                .failedExamsCount(failedExams)
                .trendMessage(trendMsg)
                .reasons(reasons)
                .build();
    }

    public AtRiskScoreDto getStudentRiskByUsername(String studentUsername) {
        Student student = studentRepository.findByUserUsername(studentUsername)
                .orElseThrow(() -> new RuntimeException("Student profile not found for username: " + studentUsername));
        return calculateStudentRisk(student);
    }

    public List<AtRiskScoreDto> getDepartmentRiskPanel(String department) {
        List<Student> students = studentRepository.findAll();
        if (department != null && !department.trim().isEmpty()) {
            students = students.stream().filter(s -> department.equalsIgnoreCase(s.getDepartment())).collect(Collectors.toList());
        }
        return students.stream()
                .map(this::calculateStudentRisk)
                .sorted(Comparator.comparingInt(AtRiskScoreDto::getRiskScore).reversed())
                .collect(Collectors.toList());
    }
}
