package com.school.management.repository;

import com.school.management.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByActorUsername(String actorUsername);

    List<AuditLog> findByAction(String action);

    Page<AuditLog> findByActorUsernameContainingIgnoreCaseOrActionContainingIgnoreCaseOrDetailsContainingIgnoreCase(String username, String action, String details, Pageable pageable);
}
