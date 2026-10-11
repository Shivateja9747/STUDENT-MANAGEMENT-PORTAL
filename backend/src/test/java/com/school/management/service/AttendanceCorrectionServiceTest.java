package com.school.management.service;

import com.school.management.entity.*;
import com.school.management.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttendanceCorrectionServiceTest {

    @Mock
    private AttendanceCorrectionRepository correctionRepository;
    @Mock
    private TeacherRepository teacherRepository;
    @Mock
    private AttendanceRepository attendanceRepository;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private AttendanceCorrectionService correctionService;

    private Teacher teacher;
    private Teacher otherTeacher;
    private Subject subject;
    private Attendance attendance;
    private AttendanceCorrection correction;

    @BeforeEach
    void setUp() {
        User teacherUser = User.builder().id(10L).username("teacher1").role(Role.TEACHER).build();
        teacher = Teacher.builder().id(1L).user(teacherUser).name("Teacher One").department("CS").build();

        User otherUser = User.builder().id(11L).username("teacher2").role(Role.TEACHER).build();
        otherTeacher = Teacher.builder().id(2L).user(otherUser).name("Teacher Two").department("CS").build();

        subject = Subject.builder().id(100L).code("CS101").name("DSA").teacher(teacher).build();
        attendance = Attendance.builder().id(500L).subject(subject).date(LocalDate.now()).status(AttendanceStatus.ABSENT).build();
        correction = AttendanceCorrection.builder()
                .id(1L)
                .attendance(attendance)
                .requestedStatus(AttendanceStatus.PRESENT)
                .status(AttendanceCorrection.CorrectionStatus.PENDING)
                .build();
    }

    @Test
    void testTeacherCannotReviewOtherTeacherSubject() {
        when(correctionRepository.findById(1L)).thenReturn(Optional.of(correction));

        assertThrows(AccessDeniedException.class, () ->
                correctionService.reviewCorrection(1L, "teacher2", true, "Ok")
        );
    }

    @Test
    void testCannotReviewTwice() {
        correction.setStatus(AttendanceCorrection.CorrectionStatus.APPROVED);
        when(correctionRepository.findById(1L)).thenReturn(Optional.of(correction));

        assertThrows(RuntimeException.class, () ->
                correctionService.reviewCorrection(1L, "teacher1", true, "Ok again")
        );
    }
}
