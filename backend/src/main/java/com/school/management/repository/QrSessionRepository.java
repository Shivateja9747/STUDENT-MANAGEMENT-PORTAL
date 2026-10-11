package com.school.management.repository;

import com.school.management.entity.QrSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface QrSessionRepository extends JpaRepository<QrSession, Long> {
    @Query("SELECT s FROM QrSession s WHERE s.teacher.user.username = :teacherUsername AND s.isActive = true")
    List<QrSession> findByTeacherUserUsernameAndOpenTrue(@Param("teacherUsername") String teacherUsername);

    @Query("SELECT s FROM QrSession s WHERE s.subject.id = :subjectId AND s.isActive = true AND FUNCTION('DATE', s.generatedAt) = :date")
    Optional<QrSession> findBySubjectIdAndDateAndOpenTrue(@Param("subjectId") Long subjectId, @Param("date") LocalDate date);
}

