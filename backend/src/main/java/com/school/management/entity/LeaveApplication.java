package com.school.management.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "leave_applications")
public class LeaveApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false, length = 1000)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeaveStatus status = LeaveStatus.PENDING;

    private String reviewerComment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private User reviewedBy;

    private LocalDateTime reviewedAt;

    private LocalDateTime createdAt;

    public LeaveApplication() {
    }

    public LeaveApplication(Long id, Student student, LocalDate startDate, LocalDate endDate, String reason, LeaveStatus status, String reviewerComment, User reviewedBy, LocalDateTime reviewedAt, LocalDateTime createdAt) {
        this.id = id;
        this.student = student;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
        this.status = status != null ? status : LeaveStatus.PENDING;
        this.reviewerComment = reviewerComment;
        this.reviewedBy = reviewedBy;
        this.reviewedAt = reviewedAt;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    @PrePersist
    public void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = LeaveStatus.PENDING;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LeaveStatus getStatus() {
        return status;
    }

    public void setStatus(LeaveStatus status) {
        this.status = status;
    }

    public String getReviewerComment() {
        return reviewerComment;
    }

    public void setReviewerComment(String reviewerComment) {
        this.reviewerComment = reviewerComment;
    }

    public User getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(User reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public enum LeaveStatus {
        PENDING,
        APPROVED,
        REJECTED
    }

    public static LeaveApplicationBuilder builder() {
        return new LeaveApplicationBuilder();
    }

    public static class LeaveApplicationBuilder {
        private Long id;
        private Student student;
        private LocalDate startDate;
        private LocalDate endDate;
        private String reason;
        private LeaveStatus status = LeaveStatus.PENDING;
        private String reviewerComment;
        private User reviewedBy;
        private LocalDateTime reviewedAt;
        private LocalDateTime createdAt;

        public LeaveApplicationBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public LeaveApplicationBuilder student(Student student) {
            this.student = student;
            return this;
        }

        public LeaveApplicationBuilder startDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        public LeaveApplicationBuilder endDate(LocalDate endDate) {
            this.endDate = endDate;
            return this;
        }

        public LeaveApplicationBuilder reason(String reason) {
            this.reason = reason;
            return this;
        }

        public LeaveApplicationBuilder status(LeaveStatus status) {
            this.status = status;
            return this;
        }

        public LeaveApplicationBuilder reviewerComment(String reviewerComment) {
            this.reviewerComment = reviewerComment;
            return this;
        }

        public LeaveApplicationBuilder reviewedBy(User reviewedBy) {
            this.reviewedBy = reviewedBy;
            return this;
        }

        public LeaveApplicationBuilder reviewedAt(LocalDateTime reviewedAt) {
            this.reviewedAt = reviewedAt;
            return this;
        }

        public LeaveApplicationBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public LeaveApplication build() {
            return new LeaveApplication(id, student, startDate, endDate, reason, status, reviewerComment, reviewedBy, reviewedAt, createdAt);
        }
    }
}
