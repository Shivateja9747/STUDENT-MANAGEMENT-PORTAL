package com.school.management.service;

import com.school.management.config.AttendanceConfig;
import com.school.management.entity.Attendance;
import com.school.management.entity.AttendanceStatus;

import java.util.List;

public class AttendanceCalculator {

    public static class CalculationResult {
        private final int totalClasses;
        private final int presentClasses;
        private final int lateClasses;
        private final int leaveClasses;
        private final int absentClasses;
        private final int attendedClasses;
        private final int workingDaysDenominator;
        private final double percentage;

        public CalculationResult(int totalClasses, int presentClasses, int lateClasses, int leaveClasses, int absentClasses, int attendedClasses, int workingDaysDenominator, double percentage) {
            this.totalClasses = totalClasses;
            this.presentClasses = presentClasses;
            this.lateClasses = lateClasses;
            this.leaveClasses = leaveClasses;
            this.absentClasses = absentClasses;
            this.attendedClasses = attendedClasses;
            this.workingDaysDenominator = workingDaysDenominator;
            this.percentage = percentage;
        }

        public int getTotalClasses() { return totalClasses; }
        public int getPresentClasses() { return presentClasses; }
        public int getLateClasses() { return lateClasses; }
        public int getLeaveClasses() { return leaveClasses; }
        public int getAbsentClasses() { return absentClasses; }
        public int getAttendedClasses() { return attendedClasses; }
        public int getWorkingDaysDenominator() { return workingDaysDenominator; }
        public double getPercentage() { return percentage; }
    }

    public static CalculationResult calculate(List<Attendance> records) {
        if (records == null || records.isEmpty()) {
            return new CalculationResult(0, 0, 0, 0, 0, 0, 0, 100.0);
        }

        int present = 0;
        int late = 0;
        int leave = 0;
        int absent = 0;

        for (Attendance a : records) {
            if (a.getStatus() == AttendanceStatus.PRESENT) present++;
            else if (a.getStatus() == AttendanceStatus.LATE) late++;
            else if (a.getStatus() == AttendanceStatus.LEAVE) leave++;
            else if (a.getStatus() == AttendanceStatus.ABSENT) absent++;
        }

        int total = records.size();
        int attended = present + (AttendanceConfig.COUNT_LATE_AS_PRESENT ? late : 0);
        int denominator = total - (AttendanceConfig.EXCLUDE_LEAVE_FROM_DENOMINATOR ? leave : 0);

        double pct = denominator > 0 ? ((double) attended / denominator) * 100.0 : 100.0;
        double roundedPct = Math.round(pct * 10.0) / 10.0;

        return new CalculationResult(total, present, late, leave, absent, attended, denominator, roundedPct);
    }
}
