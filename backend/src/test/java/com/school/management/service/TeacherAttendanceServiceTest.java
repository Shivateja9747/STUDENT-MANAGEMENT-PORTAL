package com.school.management.service;

import com.school.management.dto.AttendanceRecordRequest;
import com.school.management.dto.BulkAttendanceRequest;
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
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeacherAttendanceServiceTest {

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private SubjectRepository subjectRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private AcademicCalendarService academicCalendarService;

    @InjectMocks
    private TeacherAttendanceService teacherAttendanceService;

    private Teacher teacher;
    private Subject subject;
    private Student student;

    @BeforeEach
    void setUp() {
        User teacherUser = User.builder().id(3L).username("johndoe").role(Role.TEACHER).build();
        teacher = Teacher.builder().id(5L).user(teacherUser).name("John Doe").department("CS").build();

        subject = Subject.builder().id(10L).code("CS101-DS").name("Data Structures").semester(1).teacher(teacher).build();

        User studentUser = User.builder().id(4L).username("CS101").role(Role.STUDENT).build();
        student = Student.builder().id(1L).user(studentUser).rollNo("CS101").name("Alice").semester(1).build();
    }

    @Test
    void testSaveBulkAttendanceUpsertSuccess() {
        BulkAttendanceRequest request = BulkAttendanceRequest.builder()
                .subjectId(10L)
                .date(LocalDate.now())
                .period(1)
                .records(Collections.singletonList(new AttendanceRecordRequest(1L, AttendanceStatus.PRESENT, "On time")))
                .build();

        when(teacherRepository.findByUserUsername("johndoe")).thenReturn(Optional.of(teacher));
        when(subjectRepository.findById(10L)).thenReturn(Optional.of(subject));
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(academicCalendarService.isHoliday(any())).thenReturn(false);
        when(attendanceRepository.findByStudentIdAndSubjectIdAndDateAndPeriod(1L, 10L, LocalDate.now(), 1)).thenReturn(Optional.empty());

        teacherAttendanceService.saveBulkAttendance(request, "johndoe");

        verify(attendanceRepository, times(1)).save(any(Attendance.class));
    }

    @Test
    void testSaveBulkAttendanceFutureDateThrowsException() {
        BulkAttendanceRequest request = BulkAttendanceRequest.builder()
                .subjectId(10L)
                .date(LocalDate.now().plusDays(1))
                .period(1)
                .records(Collections.singletonList(new AttendanceRecordRequest(1L, AttendanceStatus.PRESENT, null)))
                .build();

        assertThrows(RuntimeException.class, () -> teacherAttendanceService.saveBulkAttendance(request, "johndoe"));
    }

    @Test
    void testAccessDeniedWhenSubjectNotAssignedToTeacher() {
        Teacher otherTeacher = Teacher.builder().id(99L).build();
        Subject unassignedSubject = Subject.builder().id(20L).teacher(otherTeacher).build();

        BulkAttendanceRequest request = BulkAttendanceRequest.builder()
                .subjectId(20L)
                .date(LocalDate.now())
                .period(1)
                .records(Collections.singletonList(new AttendanceRecordRequest(1L, AttendanceStatus.PRESENT, null)))
                .build();

        when(teacherRepository.findByUserUsername("johndoe")).thenReturn(Optional.of(teacher));
        when(subjectRepository.findById(20L)).thenReturn(Optional.of(unassignedSubject));
        when(academicCalendarService.isHoliday(any())).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> teacherAttendanceService.saveBulkAttendance(request, "johndoe"));
    }
}
