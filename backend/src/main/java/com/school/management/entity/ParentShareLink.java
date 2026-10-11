package com.school.management.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "parent_share_links")
public class ParentShareLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private Boolean revoked = false;

    private LocalDateTime createdAt;

    public ParentShareLink() {
    }

    public ParentShareLink(Long id, Student student, String token, LocalDateTime expiresAt, Boolean revoked, LocalDateTime createdAt) {
        this.id = id;
        this.student = student;
        this.token = token;
        this.expiresAt = expiresAt;
        this.revoked = revoked != null ? revoked : false;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    @PrePersist
    public void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (revoked == null) revoked = false;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public Boolean getRevoked() { return revoked; }
    public void setRevoked(Boolean revoked) { this.revoked = revoked != null ? revoked : false; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static ParentShareLinkBuilder builder() { return new ParentShareLinkBuilder(); }

    public static class ParentShareLinkBuilder {
        private Long id;
        private Student student;
        private String token;
        private LocalDateTime expiresAt;
        private Boolean revoked = false;
        private LocalDateTime createdAt;

        public ParentShareLinkBuilder id(Long id) { this.id = id; return this; }
        public ParentShareLinkBuilder student(Student student) { this.student = student; return this; }
        public ParentShareLinkBuilder token(String token) { this.token = token; return this; }
        public ParentShareLinkBuilder expiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; return this; }
        public ParentShareLinkBuilder revoked(Boolean revoked) { this.revoked = revoked; return this; }
        public ParentShareLinkBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public ParentShareLink build() {
            return new ParentShareLink(id, student, token, expiresAt, revoked, createdAt);
        }
    }
}
