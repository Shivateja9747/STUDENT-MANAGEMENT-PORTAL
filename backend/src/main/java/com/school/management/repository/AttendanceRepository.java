package com.school.management.repository;

import com.school.management.entity.Attendance;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    @org.springframework.data.jpa.repository.Query("SELECT a FROM Attendance a WHERE a.student.id = :studentId AND a.subject.id = :subjectId AND a.attendanceDate = :date AND a.period = :period")
    Optional<Attendance> findByStudentIdAndSubjectIdAndDateAndPeriod(@org.springframework.data.repository.query.Param("studentId") Long studentId, @org.springframework.data.repository.query.Param("subjectId") Long subjectId, @org.springframework.data.repository.query.Param("date") LocalDate date, @org.springframework.data.repository.query.Param("period") Integer period);

    @org.springframework.data.jpa.repository.Query("SELECT a FROM Attendance a WHERE a.student.id = :studentId AND a.subject.id = :subjectId AND a.attendanceDate = :date")
    Optional<Attendance> findByStudentIdAndSubjectIdAndDate(@org.springframework.data.repository.query.Param("studentId") Long studentId, @org.springframework.data.repository.query.Param("subjectId") Long subjectId, @org.springframework.data.repository.query.Param("date") LocalDate date);

    @EntityGraph(attributePaths = {"student", "student.user", "subject"})
    @org.springframework.data.jpa.repository.Query("SELECT a FROM Attendance a WHERE a.subject.id = :subjectId AND a.attendanceDate = :date AND a.period = :period")
    List<Attendance> findBySubjectIdAndDateAndPeriod(@org.springframework.data.repository.query.Param("subjectId") Long subjectId, @org.springframework.data.repository.query.Param("date") LocalDate date, @org.springframework.data.repository.query.Param("period") Integer period);

    @EntityGraph(attributePaths = {"student", "student.user", "subject"})
    @org.springframework.data.jpa.repository.Query("SELECT a FROM Attendance a WHERE a.subject.id = :subjectId AND a.attendanceDate = :date")
    List<Attendance> findBySubjectIdAndDate(@org.springframework.data.repository.query.Param("subjectId") Long subjectId, @org.springframework.data.repository.query.Param("date") LocalDate date);

    @EntityGraph(attributePaths = {"student", "student.user", "subject"})
    @org.springframework.data.jpa.repository.Query("SELECT a FROM Attendance a WHERE a.subject.id = :subjectId AND a.attendanceDate BETWEEN :startDate AND :endDate")
    List<Attendance> findBySubjectIdAndDateBetween(@org.springframework.data.repository.query.Param("subjectId") Long subjectId, @org.springframework.data.repository.query.Param("startDate") LocalDate startDate, @org.springframework.data.repository.query.Param("endDate") LocalDate endDate);

    @EntityGraph(attributePaths = {"student", "student.user", "subject"})
    List<Attendance> findBySubjectId(Long subjectId);

    @EntityGraph(attributePaths = {"student", "student.user", "subject"})
    List<Attendance> findByStudentId(Long studentId);

    @EntityGraph(attributePaths = {"student", "student.user", "subject"})
    List<Attendance> findByStudentIdAndSubjectId(Long studentId, Long subjectId);

    boolean existsBySubjectId(Long subjectId);
}

