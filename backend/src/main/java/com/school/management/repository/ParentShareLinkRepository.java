package com.school.management.repository;

import com.school.management.entity.ParentShareLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParentShareLinkRepository extends JpaRepository<ParentShareLink, Long> {
    Optional<ParentShareLink> findByTokenAndRevokedFalse(String token);
    List<ParentShareLink> findByStudentUserUsernameOrderByCreatedAtDesc(String studentUsername);
}
