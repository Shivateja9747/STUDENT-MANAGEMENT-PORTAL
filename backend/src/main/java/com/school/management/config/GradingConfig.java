package com.school.management.config;

/**
 * Single source of truth for Grading Scale & SGPA mappings.
 * 
 * Scale:
 * O  : >= 90% (Grade Point: 10.0) - Outstanding
 * A+ : 80 - 89% (Grade Point: 9.0) - Excellent
 * A  : 70 - 79% (Grade Point: 8.0) - Very Good
 * B  : 60 - 69% (Grade Point: 7.0) - Good
 * C  : 50 - 59% (Grade Point: 6.0) - Above Average
 * D  : 40 - 49% (Grade Point: 5.0) - Pass
 * F  : < 40%  (Grade Point: 0.0) - Fail
 */
public class GradingConfig {
    public static final double PASS_MARK_PERCENTAGE = 40.0;

    public static String calculateGrade(double percentage) {
        if (percentage >= 90.0) return "O";
        if (percentage >= 80.0) return "A+";
        if (percentage >= 70.0) return "A";
        if (percentage >= 60.0) return "B";
        if (percentage >= 50.0) return "C";
        if (percentage >= 40.0) return "D";
        return "F";
    }

    public static double getGradePoint(String grade) {
        switch (grade) {
            case "O": return 10.0;
            case "A+": return 9.0;
            case "A": return 8.0;
            case "B": return 7.0;
            case "C": return 6.0;
            case "D": return 5.0;
            default: return 0.0;
        }
    }

    public static boolean isPass(double percentage) {
        return percentage >= PASS_MARK_PERCENTAGE;
    }
}
