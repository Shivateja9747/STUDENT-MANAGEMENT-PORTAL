package com.school.management.service;

import com.school.management.entity.ExamType;
import com.school.management.entity.ExamWeightage;
import com.school.management.entity.Marks;
import com.school.management.entity.Role;
import com.school.management.repository.ExamWeightageRepository;
import com.school.management.repository.MarksRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MarksPublishingAndWeightageService {

    private final MarksRepository marksRepository;
    private final ExamWeightageRepository weightageRepository;
    private final AuditLogService auditLogService;

    public MarksPublishingAndWeightageService(MarksRepository marksRepository, ExamWeightageRepository weightageRepository, AuditLogService auditLogService) {
        this.marksRepository = marksRepository;
        this.weightageRepository = weightageRepository;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public void publishMarks(Long subjectId, ExamType examType, boolean publish, String teacherUsername) {
        List<Marks> marksList = marksRepository.findBySubjectIdAndExamType(subjectId, examType);
        for (Marks m : marksList) {
            m.setPublished(publish);
            marksRepository.save(m);
        }
        auditLogService.log(teacherUsername, Role.TEACHER, "MARKS_PUBLISH",
                (publish ? "Published" : "Unpublished") + " marks for Subject ID " + subjectId + " Exam " + examType,
                null, String.valueOf(publish), null);
    }

    @Transactional
    public void setWeightage(ExamType examType, Double percentage, String adminUsername) {
        ExamWeightage weightage = weightageRepository.findByExamType(examType)
                .orElse(ExamWeightage.builder().examType(examType).build());
        weightage.setWeightagePercentage(percentage);
        weightageRepository.save(weightage);

        auditLogService.log(adminUsername, Role.ADMIN, "WEIGHTAGE_UPDATE",
                "Set weightage for " + examType + " to " + percentage + "%", null, percentage + "%", null);
    }

    public Map<ExamType, Double> getAllWeightages() {
        List<ExamWeightage> list = weightageRepository.findAll();
        Map<ExamType, Double> map = new HashMap<>();

        // Defaults if not set
        map.put(ExamType.MID1, 20.0);
        map.put(ExamType.MID2, 20.0);
        map.put(ExamType.ASSIGNMENT, 10.0);
        map.put(ExamType.FINAL, 50.0);

        for (ExamWeightage w : list) {
            map.put(w.getExamType(), w.getWeightagePercentage());
        }
        return map;
    }

    public Double calculateWeightedScore(List<Marks> studentMarksForSubject) {
        Map<ExamType, Double> weightages = getAllWeightages();
        double totalWeightedScore = 0.0;
        double totalWeightApplied = 0.0;

        for (Marks m : studentMarksForSubject) {
            if (Boolean.TRUE.equals(m.getPublished()) || m.getPublished() == null) {
                double weight = weightages.getOrDefault(m.getExamType(), 25.0);
                double scorePct = (m.getMarksObtained() / m.getMaxMarks()) * 100.0;
                totalWeightedScore += (scorePct * (weight / 100.0));
                totalWeightApplied += weight;
            }
        }

        if (totalWeightApplied == 0.0) return 0.0;
        return (totalWeightedScore / totalWeightApplied) * 100.0;
    }
}
