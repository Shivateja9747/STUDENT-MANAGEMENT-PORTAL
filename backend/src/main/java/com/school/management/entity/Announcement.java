package com.school.management.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "announcements")
public class Announcement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(name = "target_audience", nullable = false)
    private String audience = "ALL"; // ALL, TEACHERS, STUDENTS

    @Column(name = "created_by")
    private Long createdBy;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Announcement() {
    }

    public Announcement(Long id, String title, String content, String audience, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.audience = audience != null ? audience : "ALL";
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    public Announcement(Long id, String title, String content, String audience, Long createdBy, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.audience = audience != null ? audience : "ALL";
        this.createdBy = createdBy;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAudience() {
        return audience;
    }

    public void setAudience(String audience) {
        if (audience != null && !audience.isBlank()) {
            this.audience = audience;
        }
    }

    public String getTargetRole() {
        return audience;
    }

    public void setTargetRole(String targetRole) {
        if (targetRole != null && !targetRole.isBlank()) {
            this.audience = targetRole;
        }
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static AnnouncementBuilder builder() {
        return new AnnouncementBuilder();
    }

    public static class AnnouncementBuilder {
        private Long id;
        private String title;
        private String content;
        private String audience = "ALL";
        private LocalDateTime createdAt = LocalDateTime.now();

        public AnnouncementBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public AnnouncementBuilder title(String title) {
            this.title = title;
            return this;
        }

        public AnnouncementBuilder content(String content) {
            this.content = content;
            return this;
        }

        public AnnouncementBuilder audience(String audience) {
            this.audience = audience;
            return this;
        }

        public AnnouncementBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Announcement build() {
            return new Announcement(id, title, content, audience, createdAt);
        }
    }
}
