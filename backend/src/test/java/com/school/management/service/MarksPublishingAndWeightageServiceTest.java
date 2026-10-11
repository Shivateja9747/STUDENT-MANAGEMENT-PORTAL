package com.school.management.service;

import com.school.management.entity.ExamType;
import com.school.management.entity.Marks;
import com.school.management.repository.ExamWeightageRepository;
import com.school.management.repository.MarksRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MarksPublishingAndWeightageServiceTest {

    @Mock
    private MarksRepository marksRepository;
    @Mock
    private ExamWeightageRepository weightageRepository;
    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private MarksPublishingAndWeightageService service;

    @BeforeEach
    void setUp() {
        when(weightageRepository.findAll()).thenReturn(Collections.emptyList());
    }

    @Test
    void testCalculateWeightedScore_WithAllExams() {
        Marks m1 = Marks.builder().examType(ExamType.MID1).marksObtained(80.0).maxMarks(100.0).published(true).build(); // 20% weight
        Marks m2 = Marks.builder().examType(ExamType.MID2).marksObtained(90.0).maxMarks(100.0).published(true).build(); // 20% weight
        Marks m3 = Marks.builder().examType(ExamType.ASSIGNMENT).marksObtained(100.0).maxMarks(100.0).published(true).build(); // 10% weight
        Marks m4 = Marks.builder().examType(ExamType.FINAL).marksObtained(70.0).maxMarks(100.0).published(true).build(); // 50% weight

        List<Marks> list = Arrays.asList(m1, m2, m3, m4);
        Double score = service.calculateWeightedScore(list);

        // Expected: (80*0.2 + 90*0.2 + 100*0.1 + 70*0.5) = 16 + 18 + 10 + 35 = 79.0
        assertEquals(79.0, score, 0.01);
    }

    @Test
    void testCalculateWeightedScore_HandlesMissingExamsAndUnpublished() {
        Marks m1 = Marks.builder().examType(ExamType.MID1).marksObtained(80.0).maxMarks(100.0).published(true).build(); // 20%
        Marks m2 = Marks.builder().examType(ExamType.MID2).marksObtained(90.0).maxMarks(100.0).published(false).build(); // Unpublished!

        List<Marks> list = Arrays.asList(m1, m2);
        Double score = service.calculateWeightedScore(list);

        // Only m1 is published. Score pct = 80%, applied weight = 20%. Total score = (80 * 0.2) / 0.2 = 80.0%
        assertEquals(80.0, score, 0.01);
    }
}
