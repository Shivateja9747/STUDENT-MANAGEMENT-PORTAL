package com.school.management.controller;

import com.school.management.entity.Announcement;
import com.school.management.entity.User;
import com.school.management.repository.AnnouncementRepository;
import com.school.management.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class AnnouncementController {

    private final AnnouncementRepository announcementRepository;
    private final UserRepository userRepository;

    public AnnouncementController(AnnouncementRepository announcementRepository, UserRepository userRepository) {
        this.announcementRepository = announcementRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/announcements")
    public ResponseEntity<List<Announcement>> getAnnouncements() {
        return ResponseEntity.ok(announcementRepository.findAllByOrderByCreatedAtDesc());
    }

    @PostMapping("/admin/announcements")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Announcement> createAnnouncement(@RequestBody Announcement announcement, Authentication authentication) {
        announcement.setCreatedAt(LocalDateTime.now());
        if (announcement.getAudience() == null || announcement.getAudience().isBlank()) {
            announcement.setAudience("ALL");
        }
        if (announcement.getCreatedBy() == null && authentication != null) {
            Optional<User> userOpt = userRepository.findByUsername(authentication.getName());
            userOpt.ifPresent(user -> announcement.setCreatedBy(user.getId()));
        }
        if (announcement.getCreatedBy() == null) {
            userRepository.findAll().stream().findFirst().ifPresent(u -> announcement.setCreatedBy(u.getId()));
        }
        return ResponseEntity.ok(announcementRepository.save(announcement));
    }

    @DeleteMapping("/admin/announcements/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAnnouncement(@PathVariable Long id) {
        announcementRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
