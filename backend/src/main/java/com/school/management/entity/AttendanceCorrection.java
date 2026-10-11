package com.school.management.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendance_corrections")
public class AttendanceCorrection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "attendance_record_id", nullable = false)
    private Attendance attendance;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "requested_by", nullable = false)
    private User requestedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttendanceStatus requestedStatus;

    @Column(nullable = false, length = 1000)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CorrectionStatus status = CorrectionStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private User reviewedBy;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    private String reviewerComment;

    private LocalDateTime createdAt;

    public AttendanceCorrection() {
    }

    public AttendanceCorrection(Long id, Attendance attendance, User requestedBy, AttendanceStatus requestedStatus, String reason, CorrectionStatus status, User reviewedBy, LocalDateTime reviewedAt, String reviewerComment, LocalDateTime createdAt) {
        this.id = id;
        this.attendance = attendance;
        this.requestedBy = requestedBy;
        this.requestedStatus = requestedStatus;
        this.reason = reason;
        this.status = status != null ? status : CorrectionStatus.PENDING;
        this.reviewedBy = reviewedBy;
        this.reviewedAt = reviewedAt;
        this.reviewerComment = reviewerComment;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    @PrePersist
    public void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = CorrectionStatus.PENDING;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Attendance getAttendance() {
        return attendance;
    }

    public void setAttendance(Attendance attendance) {
        this.attendance = attendance;
    }

    public Attendance getAttendanceRecord() {
        return attendance;
    }

    public void setAttendanceRecord(Attendance attendance) {
        this.attendance = attendance;
    }

    public User getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(User requestedBy) {
        this.requestedBy = requestedBy;
    }

    public Student getStudent() {
        return attendance != null ? attendance.getStudent() : null;
    }

    public AttendanceStatus getRequestedStatus() {
        return requestedStatus;
    }

    public void setRequestedStatus(AttendanceStatus requestedStatus) {
        this.requestedStatus = requestedStatus;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public CorrectionStatus getStatus() {
        return status;
    }

    public void setStatus(CorrectionStatus status) {
        this.status = status;
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

    public String getReviewerComment() {
        return reviewerComment;
    }

    public void setReviewerComment(String reviewerComment) {
        this.reviewerComment = reviewerComment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public enum CorrectionStatus {
        PENDING,
        APPROVED,
        REJECTED
    }

    public static AttendanceCorrectionBuilder builder() {
        return new AttendanceCorrectionBuilder();
    }

    public static class AttendanceCorrectionBuilder {
        private Long id;
        private Attendance attendance;
        private User requestedBy;
        private AttendanceStatus requestedStatus;
        private String reason;
        private CorrectionStatus status = CorrectionStatus.PENDING;
        private User reviewedBy;
        private LocalDateTime reviewedAt;
        private String reviewerComment;
        private LocalDateTime createdAt;

        public AttendanceCorrectionBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public AttendanceCorrectionBuilder attendance(Attendance attendance) {
            this.attendance = attendance;
            return this;
        }

        public AttendanceCorrectionBuilder attendanceRecord(Attendance attendance) {
            this.attendance = attendance;
            return this;
        }

        public AttendanceCorrectionBuilder requestedBy(User requestedBy) {
            this.requestedBy = requestedBy;
            return this;
        }

        public AttendanceCorrectionBuilder requestedStatus(AttendanceStatus requestedStatus) {
            this.requestedStatus = requestedStatus;
            return this;
        }

        public AttendanceCorrectionBuilder reason(String reason) {
            this.reason = reason;
            return this;
        }

        public AttendanceCorrectionBuilder status(CorrectionStatus status) {
            this.status = status;
            return this;
        }

        public AttendanceCorrectionBuilder reviewedBy(User reviewedBy) {
            this.reviewedBy = reviewedBy;
            return this;
        }

        public AttendanceCorrectionBuilder reviewedAt(LocalDateTime reviewedAt) {
            this.reviewedAt = reviewedAt;
            return this;
        }

        public AttendanceCorrectionBuilder reviewerComment(String reviewerComment) {
            this.reviewerComment = reviewerComment;
            return this;
        }

        public AttendanceCorrectionBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public AttendanceCorrection build() {
            return new AttendanceCorrection(id, attendance, requestedBy, requestedStatus, reason, status, reviewedBy, reviewedAt, reviewerComment, createdAt);
        }
    }
}
