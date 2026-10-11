package com.school.management.repository;

import com.school.management.entity.NotificationPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationPreferenceRepository extends JpaRepository<NotificationPreference, Long> {
    Optional<NotificationPreference> findByUserUsername(String username);
    Optional<NotificationPreference> findByUserId(Long userId);
}
