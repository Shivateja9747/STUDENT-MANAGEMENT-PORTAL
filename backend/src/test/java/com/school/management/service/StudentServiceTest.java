package com.school.management.service;

import com.school.management.dto.StudentSubjectAttendanceDto;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private MarksRepository marksRepository;

    @Mock
    private SubjectRepository subjectRepository;

    @InjectMocks
    private StudentService studentService;

    private Student student;
    private Subject subject;

    @BeforeEach
    void setUp() {
        User user = User.builder().id(2L).username("CS101").role(Role.STUDENT).build();
        student = Student.builder().id(1L).user(user).rollNo("CS101").name("Alice").department("CS").semester(1).build();
        subject = Subject.builder().id(10L).code("CS101-DS").name("Data Structures").semester(1).build();
    }

    @Test
    void testGetStudentAttendancePercentageAndLowAttendanceFlag() {
        Attendance a1 = Attendance.builder().id(100L).student(student).subject(subject).date(LocalDate.now().minusDays(2)).status(AttendanceStatus.PRESENT).build();
        Attendance a2 = Attendance.builder().id(101L).student(student).subject(subject).date(LocalDate.now().minusDays(1)).status(AttendanceStatus.ABSENT).build();
        Attendance a3 = Attendance.builder().id(102L).student(student).subject(subject).date(LocalDate.now()).status(AttendanceStatus.ABSENT).build();

        when(studentRepository.findByUserUsername("CS101")).thenReturn(Optional.of(student));
        when(attendanceRepository.findByStudentId(1L)).thenReturn(Arrays.asList(a1, a2, a3));

        List<StudentSubjectAttendanceDto> attendance = studentService.getStudentAttendance("CS101");

        assertEquals(1, attendance.size());
        StudentSubjectAttendanceDto dto = attendance.get(0);
        assertEquals(3, dto.getTotalClasses());
        assertEquals(1, dto.getPresentClasses());
        assertEquals(33.33, dto.getPercentage());
        assertTrue(dto.isLowAttendance()); // 33.33% < 75% -> true
    }

    @Test
    void testStudentDataIsolation_UserNotFoundThrowsException() {
        when(studentRepository.findByUserUsername("otherstudent")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> studentService.getStudentAttendance("otherstudent"));
    }
}
