package com.school.management.service;

import com.school.management.config.GradingConfig;
import com.school.management.dto.MarksEntryRequest;
import com.school.management.dto.MarksRecordRequest;
import com.school.management.dto.MarksResponse;
import com.school.management.entity.ExamType;
import com.school.management.entity.Marks;
import com.school.management.entity.Student;
import com.school.management.entity.Subject;
import com.school.management.entity.Teacher;
import com.school.management.repository.MarksRepository;
import com.school.management.repository.StudentRepository;
import com.school.management.repository.SubjectRepository;
import com.school.management.repository.TeacherRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TeacherMarksService {

    private final TeacherRepository teacherRepository;
    private final SubjectRepository subjectRepository;
    private final StudentRepository studentRepository;
    private final MarksRepository marksRepository;

    public TeacherMarksService(TeacherRepository teacherRepository, SubjectRepository subjectRepository, StudentRepository studentRepository, MarksRepository marksRepository) {
        this.teacherRepository = teacherRepository;
        this.subjectRepository = subjectRepository;
        this.studentRepository = studentRepository;
        this.marksRepository = marksRepository;
    }

    public Teacher getTeacherByUsername(String username) {
        return teacherRepository.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Teacher profile not found for user: " + username));
    }

    @Transactional
    public List<MarksResponse> saveOrUpdateMarks(MarksEntryRequest request, String username) {
        Teacher teacher = getTeacherByUsername(username);
        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Subject not found with id: " + request.getSubjectId()));

        if (subject.getTeacher() == null || !subject.getTeacher().getId().equals(teacher.getId())) {
            throw new AccessDeniedException("Subject is not assigned to you.");
        }

        for (MarksRecordRequest rec : request.getRecords()) {
            boolean isAbs = Boolean.TRUE.equals(rec.getIsAbsent());
            double obtained = isAbs ? 0.0 : (rec.getMarksObtained() != null ? rec.getMarksObtained() : 0.0);

            if (!isAbs && (obtained < 0 || obtained > rec.getMaxMarks())) {
                throw new RuntimeException("Marks obtained (" + obtained + 
                        ") must be between 0 and max marks (" + rec.getMaxMarks() + ").");
            }

            Student student = studentRepository.findById(rec.getStudentId())
                    .orElseThrow(() -> new RuntimeException("Student not found with id: " + rec.getStudentId()));

            Optional<Marks> existingOpt = marksRepository.findByStudentIdAndSubjectIdAndExamType(
                    student.getId(), subject.getId(), request.getExamType());

            Marks marks;
            if (existingOpt.isPresent()) {
                marks = existingOpt.get();
                marks.setMarksObtained(obtained);
                marks.setMaxMarks(rec.getMaxMarks());
                marks.setIsAbsent(isAbs);
            } else {
                marks = Marks.builder()
                        .student(student)
                        .subject(subject)
                        .examType(request.getExamType())
                        .marksObtained(obtained)
                        .maxMarks(rec.getMaxMarks())
                        .isAbsent(isAbs)
                        .build();
            }
            marksRepository.save(marks);
        }

        return getMarksBySubjectAndExamType(subject.getId(), request.getExamType(), username);
    }

    public List<MarksResponse> getMarksBySubjectAndExamType(Long subjectId, ExamType examType, String username) {
        Teacher teacher = getTeacherByUsername(username);
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found with id: " + subjectId));

        if (subject.getTeacher() == null || !subject.getTeacher().getId().equals(teacher.getId())) {
            throw new AccessDeniedException("Subject is not assigned to you.");
        }

        List<Marks> list = marksRepository.findBySubjectIdAndExamType(subjectId, examType);

        // Sort descending to assign ranks among present students
        List<Marks> presentList = list.stream()
                .filter(m -> !Boolean.TRUE.equals(m.getIsAbsent()))
                .sorted((a, b) -> Double.compare(
                        b.getMaxMarks() > 0 ? (b.getMarksObtained() / b.getMaxMarks()) : 0,
                        a.getMaxMarks() > 0 ? (a.getMarksObtained() / a.getMaxMarks()) : 0
                ))
                .collect(Collectors.toList());

        Map<Long, Integer> rankMap = new HashMap<>();
        for (int i = 0; i < presentList.size(); i++) {
            rankMap.put(presentList.get(i).getId(), i + 1);
        }

        return list.stream()
                .map(m -> {
                    boolean isAbs = Boolean.TRUE.equals(m.getIsAbsent());
                    double pct = (!isAbs && m.getMaxMarks() > 0) ? round((m.getMarksObtained() / m.getMaxMarks()) * 100.0) : 0.0;
                    String grade = isAbs ? "ABSENT" : GradingConfig.calculateGrade(pct);
                    boolean isPass = !isAbs && GradingConfig.isPass(pct);
                    Integer rank = rankMap.get(m.getId());

                    return MarksResponse.builder()
                            .id(m.getId())
                            .studentId(m.getStudent().getId())
                            .studentName(m.getStudent().getName())
                            .studentRollNo(m.getStudent().getRollNo())
                            .subjectId(m.getSubject().getId())
                            .subjectName(m.getSubject().getName())
                            .examType(m.getExamType())
                            .marksObtained(m.getMarksObtained())
                            .maxMarks(m.getMaxMarks())
                            .percentage(pct)
                            .grade(grade)
                            .isPassed(isPass)
                            .isAbsent(isAbs)
                            .rank(rank)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteMarks(Long marksId, String username) {
        Teacher teacher = getTeacherByUsername(username);
        Marks marks = marksRepository.findById(marksId)
                .orElseThrow(() -> new RuntimeException("Marks record not found with id: " + marksId));

        if (marks.getSubject().getTeacher() == null || !marks.getSubject().getTeacher().getId().equals(teacher.getId())) {
            throw new AccessDeniedException("Subject is not assigned to you.");
        }

        marksRepository.delete(marks);
    }

    // Statistics for an exam: Class average, highest, lowest, pass percentage
    public Map<String, Object> getMarksStats(Long subjectId, ExamType examType, String username) {
        List<MarksResponse> responses = getMarksBySubjectAndExamType(subjectId, examType, username);
        List<MarksResponse> presentResponses = responses.stream()
                .filter(r -> !Boolean.TRUE.equals(r.getIsAbsent()))
                .collect(Collectors.toList());

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalStudents", responses.size());
        stats.put("presentCount", presentResponses.size());
        stats.put("absentCount", responses.size() - presentResponses.size());

        if (presentResponses.isEmpty()) {
            stats.put("classAverage", 0.0);
            stats.put("highestScore", 0.0);
            stats.put("lowestScore", 0.0);
            stats.put("passPercentage", 0.0);
            return stats;
        }

        double avgPct = round(presentResponses.stream().mapToDouble(MarksResponse::getPercentage).average().orElse(0.0));
        double maxPct = round(presentResponses.stream().mapToDouble(MarksResponse::getPercentage).max().orElse(0.0));
        double minPct = round(presentResponses.stream().mapToDouble(MarksResponse::getPercentage).min().orElse(0.0));
        long passCount = presentResponses.stream().filter(MarksResponse::getIsPassed).count();
        double passPct = round(((double) passCount / presentResponses.size()) * 100.0);

        stats.put("classAverage", avgPct);
        stats.put("highestScore", maxPct);
        stats.put("lowestScore", minPct);
        stats.put("passPercentage", passPct);

        return stats;
    }

    private double round(double val) {
        BigDecimal bd = BigDecimal.valueOf(val);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
