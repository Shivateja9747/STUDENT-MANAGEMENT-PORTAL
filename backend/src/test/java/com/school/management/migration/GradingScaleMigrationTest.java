package com.school.management.migration;

import com.school.management.config.GradingConfig;
import com.school.management.entity.GradingScale;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GradingScaleMigrationTest {

    @Test
    @DisplayName("Verify grading scale ranges are non-overlapping and cover 0-100")
    void testGradingScaleCoverageAndNoOverlap() {
        List<GradingScale> scaleList = Arrays.asList(
            new GradingScale(1L, "F", 0.0, 39.99, 0.0, "Fail"),
            new GradingScale(2L, "D", 40.0, 49.99, 5.0, "Pass"),
            new GradingScale(3L, "C", 50.0, 59.99, 6.0, "Above Average"),
            new GradingScale(4L, "B", 60.0, 69.99, 7.0, "Good"),
            new GradingScale(5L, "A", 70.0, 79.99, 8.0, "Very Good"),
            new GradingScale(6L, "A+", 80.0, 89.99, 9.0, "Excellent"),
            new GradingScale(7L, "O", 90.0, 100.0, 10.0, "Outstanding")
        );

        scaleList.sort(Comparator.comparingDouble(GradingScale::getMinPercentage));

        // Check 0-100 bounds
        assertEquals(0.0, scaleList.get(0).getMinPercentage(), 0.001);
        assertEquals(100.0, scaleList.get(scaleList.size() - 1).getMaxPercentage(), 0.001);

        // Check continuity and non-overlap
        for (int i = 0; i < scaleList.size() - 1; i++) {
            GradingScale current = scaleList.get(i);
            GradingScale next = scaleList.get(i + 1);

            assertTrue(current.getMaxPercentage() < next.getMinPercentage(),
                "Range overlap between " + current.getGrade() + " and " + next.getGrade());

            // Max gap between adjacent ranges should be negligible (0.01)
            double gap = next.getMinPercentage() - current.getMaxPercentage();
            assertTrue(gap <= 0.011, "Gap between " + current.getGrade() + " and " + next.getGrade() + " is too large: " + gap);
        }

        // Test GradingConfig alignment
        assertEquals("O", GradingConfig.calculateGrade(95.0));
        assertEquals("A+", GradingConfig.calculateGrade(85.0));
        assertEquals("A", GradingConfig.calculateGrade(75.0));
        assertEquals("B", GradingConfig.calculateGrade(65.0));
        assertEquals("C", GradingConfig.calculateGrade(55.0));
        assertEquals("D", GradingConfig.calculateGrade(45.0));
        assertEquals("F", GradingConfig.calculateGrade(30.0));
    }
}
