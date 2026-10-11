package com.school.management.integration;

import com.school.management.entity.Attendance;
import com.school.management.entity.AttendanceStatus;
import com.school.management.entity.Role;
import com.school.management.entity.Student;
import com.school.management.entity.Subject;
import com.school.management.entity.User;
import com.school.management.repository.AttendanceRepository;
import com.school.management.repository.StudentRepository;
import com.school.management.repository.SubjectRepository;
import com.school.management.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class AttendanceQueryOptimizationTest {

    @Autowired private UserRepository userRepository;
    @Autowired private StudentRepository studentRepository;
    @Autowired private SubjectRepository subjectRepository;
    @Autowired private AttendanceRepository attendanceRepository;
    @Autowired private EntityManager entityManager;
    @Autowired private EntityManagerFactory entityManagerFactory;

    @Test
    @DisplayName("Assert single query execution (no N+1) for fetching attendance of 30 students by subject")
    void testAttendanceBySubjectQueryCountFor30Students() {
        // Create 1 Subject
        Subject subject = Subject.builder()
                .code("CS301_OPT")
                .name("Optimization Test Subject")
                .semester(3)
                .build();
        Subject savedSubject = subjectRepository.save(subject);

        // Create 30 Students with Attendance records
        List<Attendance> attendanceList = new ArrayList<>();
        for (int i = 1; i <= 30; i++) {
            User user = User.builder()
                    .username("opt_student_" + i)
                    .password("password123")
                    .email("opt_student_" + i + "@school.com")
                    .role(Role.STUDENT)
                    .isActive(true)
                    .build();
            User savedUser = userRepository.save(user);

            Student student = Student.builder()
                    .user(savedUser)
                    .rollNo("OPT_ROLL_" + i)
                    .name("Opt Student " + i)
                    .department("CS")
                    .semester(3)
                    .section("A")
                    .build();
            Student savedStudent = studentRepository.save(student);

            Attendance attendance = Attendance.builder()
                    .student(savedStudent)
                    .subject(savedSubject)
                    .date(LocalDate.now())
                    .period(1)
                    .status(AttendanceStatus.PRESENT)
                    .build();
            attendanceList.add(attendance);
        }
        attendanceRepository.saveAll(attendanceList);

        // Flush and clear L1 Cache so query goes to DB
        entityManager.flush();
        entityManager.clear();

        SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
        Statistics statistics = sessionFactory.getStatistics();
        statistics.setStatisticsEnabled(true);
        statistics.clear();

        // Perform fetch for 30 students
        List<Attendance> fetchedRecords = attendanceRepository.findBySubjectId(savedSubject.getId());

        assertEquals(30, fetchedRecords.size());

        // Access attributes to ensure lazy loading is satisfied by entity graph without extra queries
        for (Attendance record : fetchedRecords) {
            record.getStudent().getName();
            record.getStudent().getUser().getUsername();
            record.getSubject().getName();
        }

        long statementCount = statistics.getPrepareStatementCount();
        System.out.println("REAL HIBERNATE PREPARED STATEMENT COUNT FOR 30 STUDENTS ATTENDANCE FETCH: " + statementCount);

        // Assert exactly 1 query executed (N+1 query problem prevented via @EntityGraph)
        assertEquals(1, statementCount, "Expected exactly 1 SQL query prepared via @EntityGraph fetch, avoiding N+1 queries");
    }
}
