package com.school.management.repository;

import com.school.management.entity.LeaveApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveApplicationRepository extends JpaRepository<LeaveApplication, Long> {

    List<LeaveApplication> findByStudentUserUsernameOrderByCreatedAtDesc(String username);

    List<LeaveApplication> findByStudentDepartmentOrderByCreatedAtDesc(String department);

    List<LeaveApplication> findByStatusOrderByCreatedAtDesc(LeaveApplication.LeaveStatus status);

    Page<LeaveApplication> findByStudentNameContainingIgnoreCaseOrReasonContainingIgnoreCase(String name, String reason, Pageable pageable);
}
