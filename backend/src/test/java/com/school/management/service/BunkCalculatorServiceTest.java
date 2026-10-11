package com.school.management.service;

import com.school.management.dto.BunkCalculatorDto;
import com.school.management.entity.*;
import com.school.management.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BunkCalculatorServiceTest {

    @Mock
    private StudentRepository studentRepository;
    @Mock
    private SubjectRepository subjectRepository;
    @Mock
    private AttendanceRepository attendanceRepository;

    @InjectMocks
    private BunkCalculatorService bunkCalculatorService;

    private Student student;
    private Subject subject;

    @BeforeEach
    void setUp() {
        User user = User.builder().id(10L).username("student1").role(Role.STUDENT).build();
        student = Student.builder().id(1L).user(user).rollNo("CS101").name("Student").semester(1).build();
        subject = Subject.builder().id(100L).code("CS101").name("DSA").semester(1).build();

        when(studentRepository.findByUserUsername("student1")).thenReturn(Optional.of(student));
        when(subjectRepository.findBySemester(1)).thenReturn(Collections.singletonList(subject));
    }

    @Test
    void testZeroClassesHeld_EdgeCase() {
        when(attendanceRepository.findByStudentIdAndSubjectId(1L, 100L)).thenReturn(Collections.emptyList());

        List<BunkCalculatorDto> res = bunkCalculatorService.calculateBunkMarginForStudent("student1", 75.0);
        assertEquals(1, res.size());
        BunkCalculatorDto dto = res.get(0);
        assertEquals(0, dto.getTotalClasses());
        assertEquals(100.0, dto.getCurrentPercentage());
        assertEquals("No classes conducted yet.", dto.getStatusMessage());
    }

    @Test
    void testAlreadyBelowTarget_EdgeCase() {
        Attendance a1 = Attendance.builder().id(1L).status(AttendanceStatus.ABSENT).build();
        Attendance a2 = Attendance.builder().id(2L).status(AttendanceStatus.ABSENT).build();
        Attendance a3 = Attendance.builder().id(3L).status(AttendanceStatus.PRESENT).build();

        when(attendanceRepository.findByStudentIdAndSubjectId(1L, 100L)).thenReturn(Arrays.asList(a1, a2, a3));

        List<BunkCalculatorDto> res = bunkCalculatorService.calculateBunkMarginForStudent("student1", 75.0);
        assertEquals(1, res.size());
        BunkCalculatorDto dto = res.get(0);
        assertEquals(3, dto.getTotalClasses());
        assertEquals(33.3, dto.getCurrentPercentage());
        assertTrue(dto.getClassesToAttend() > 0);
        assertTrue(dto.getStatusMessage().contains("must attend"));
    }

    @Test
    void testLeaveExcludedFromCalculation_EdgeCase() {
        Attendance a1 = Attendance.builder().id(1L).status(AttendanceStatus.PRESENT).build();
        Attendance a2 = Attendance.builder().id(2L).status(AttendanceStatus.LEAVE).build();

        when(attendanceRepository.findByStudentIdAndSubjectId(1L, 100L)).thenReturn(Arrays.asList(a1, a2));

        List<BunkCalculatorDto> res = bunkCalculatorService.calculateBunkMarginForStudent("student1", 75.0);
        assertEquals(1, res.size());
        BunkCalculatorDto dto = res.get(0);
        assertEquals(1, dto.getTotalClasses()); // LEAVE is excluded from denominator
        assertEquals(1, dto.getAttendedClasses());
        assertEquals(100.0, dto.getCurrentPercentage());
    }
}
