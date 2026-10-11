package com.school.management.config;

/**
 * Single source of truth for Attendance Calculation Rules and Constants.
 * 
 * Rules:
 * 1. PRESENT & LATE count as attended (+1 attended, +1 total).
 * 2. ABSENT counts as unexcused (+0 attended, +1 total).
 * 3. LEAVE counts as excused (+0 attended, +0 denominator).
 * 
 * Formula: Attendance % = ((PRESENT + LATE) / (TOTAL - LEAVE)) * 100
 */
public class AttendanceConfig {
    public static final boolean EXCLUDE_LEAVE_FROM_DENOMINATOR = true;
    public static final boolean COUNT_LATE_AS_PRESENT = true;
    public static final int TEACHER_EDIT_WINDOW_DAYS = 7;
    public static final double DEFAULT_ATTENDANCE_THRESHOLD = 75.0;
}
