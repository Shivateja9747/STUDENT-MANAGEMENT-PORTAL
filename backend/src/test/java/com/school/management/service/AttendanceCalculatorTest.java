package com.school.management.service;

import com.school.management.entity.Attendance;
import com.school.management.entity.AttendanceStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AttendanceCalculatorTest {

    @Test
    void testCalculateWorkingDaysPercentage_ExcludesHolidaysAndLeave() {
        LocalDate d1 = LocalDate.of(2026, 10, 5);
        LocalDate d2 = LocalDate.of(2026, 10, 7);

        List<Attendance> list = Arrays.asList(
                Attendance.builder().date(d1).status(AttendanceStatus.PRESENT).build(),
                Attendance.builder().date(d2).status(AttendanceStatus.LEAVE).build()
        );

        AttendanceCalculator.CalculationResult res = AttendanceCalculator.calculate(list);
        // LEAVE is excluded from denominator when EXCLUDE_LEAVE_FROM_DENOMINATOR is true
        assertEquals(2, res.getTotalClasses());
        assertEquals(1, res.getAttendedClasses());
        assertEquals(100.0, res.getPercentage());
    }
}
