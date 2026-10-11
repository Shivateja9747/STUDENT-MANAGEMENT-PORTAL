package com.school.management.repository;

import com.school.management.entity.AttendanceCorrection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttendanceCorrectionRepository extends JpaRepository<AttendanceCorrection, Long> {

    @Query("SELECT ac FROM AttendanceCorrection ac WHERE ac.requestedBy.username = :username ORDER BY ac.createdAt DESC")
    List<AttendanceCorrection> findByStudentUserUsernameOrderByCreatedAtDesc(@Param("username") String username);

    @Query("SELECT ac FROM AttendanceCorrection ac WHERE ac.attendance.subject.teacher.user.username = :teacherUsername ORDER BY ac.createdAt DESC")
    List<AttendanceCorrection> findByAttendanceSubjectTeacherUserUsernameOrderByCreatedAtDesc(@Param("teacherUsername") String teacherUsername);

    List<AttendanceCorrection> findByStatusOrderByCreatedAtDesc(AttendanceCorrection.CorrectionStatus status);

    Page<AttendanceCorrection> findByReasonContainingIgnoreCase(String reason, Pageable pageable);
}
