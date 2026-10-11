package com.school.management.repository;

import com.school.management.entity.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Optional<Teacher> findByUserIdAndDeletedAtIsNull(Long userId);
    default Optional<Teacher> findByUserId(Long userId) { return findByUserIdAndDeletedAtIsNull(userId); }

    Optional<Teacher> findByUserUsernameAndDeletedAtIsNull(String username);
    default Optional<Teacher> findByUserUsername(String username) { return findByUserUsernameAndDeletedAtIsNull(username); }

    Page<Teacher> findByNameContainingIgnoreCaseOrDepartmentContainingIgnoreCase(String name, String department, Pageable pageable);
}
