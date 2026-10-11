package com.school.management.dto;

import com.school.management.entity.LeaveApplication;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class LeaveApplicationDto {

    private Long id;
    private Long studentId;
    private String studentName;
    private String studentRollNo;
    private String studentDepartment;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @NotBlank(message = "Reason is required")
    private String reason;

    private String attachmentUrl;
    private LeaveApplication.LeaveStatus status;
    private String reviewerComment;
    private String reviewedBy;
    private LocalDateTime createdAt;

    public LeaveApplicationDto() {
    }

    public LeaveApplicationDto(Long id, Long studentId, String studentName, String studentRollNo, String studentDepartment, LocalDate startDate, LocalDate endDate, String reason, String attachmentUrl, LeaveApplication.LeaveStatus status, String reviewerComment, String reviewedBy, LocalDateTime createdAt) {
        this.id = id;
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentRollNo = studentRollNo;
        this.studentDepartment = studentDepartment;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
        this.attachmentUrl = attachmentUrl;
        this.status = status;
        this.reviewerComment = reviewerComment;
        this.reviewedBy = reviewedBy;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentRollNo() {
        return studentRollNo;
    }

    public void setStudentRollNo(String studentRollNo) {
        this.studentRollNo = studentRollNo;
    }

    public String getStudentDepartment() {
        return studentDepartment;
    }

    public void setStudentDepartment(String studentDepartment) {
        this.studentDepartment = studentDepartment;
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

    public String getAttachmentUrl() {
        return attachmentUrl;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }

    public LeaveApplication.LeaveStatus getStatus() {
        return status;
    }

    public void setStatus(LeaveApplication.LeaveStatus status) {
        this.status = status;
    }

    public String getReviewerComment() {
        return reviewerComment;
    }

    public void setReviewerComment(String reviewerComment) {
        this.reviewerComment = reviewerComment;
    }

    public String getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(String reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static LeaveApplicationDtoBuilder builder() {
        return new LeaveApplicationDtoBuilder();
    }

    public static class LeaveApplicationDtoBuilder {
        private Long id;
        private Long studentId;
        private String studentName;
        private String studentRollNo;
        private String studentDepartment;
        private LocalDate startDate;
        private LocalDate endDate;
        private String reason;
        private String attachmentUrl;
        private LeaveApplication.LeaveStatus status;
        private String reviewerComment;
        private String reviewedBy;
        private LocalDateTime createdAt;

        public LeaveApplicationDtoBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public LeaveApplicationDtoBuilder studentId(Long studentId) {
            this.studentId = studentId;
            return this;
        }

        public LeaveApplicationDtoBuilder studentName(String studentName) {
            this.studentName = studentName;
            return this;
        }

        public LeaveApplicationDtoBuilder studentRollNo(String studentRollNo) {
            this.studentRollNo = studentRollNo;
            return this;
        }

        public LeaveApplicationDtoBuilder studentDepartment(String studentDepartment) {
            this.studentDepartment = studentDepartment;
            return this;
        }

        public LeaveApplicationDtoBuilder startDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        public LeaveApplicationDtoBuilder endDate(LocalDate endDate) {
            this.endDate = endDate;
            return this;
        }

        public LeaveApplicationDtoBuilder reason(String reason) {
            this.reason = reason;
            return this;
        }

        public LeaveApplicationDtoBuilder attachmentUrl(String attachmentUrl) {
            this.attachmentUrl = attachmentUrl;
            return this;
        }

        public LeaveApplicationDtoBuilder status(LeaveApplication.LeaveStatus status) {
            this.status = status;
            return this;
        }

        public LeaveApplicationDtoBuilder reviewerComment(String reviewerComment) {
            this.reviewerComment = reviewerComment;
            return this;
        }

        public LeaveApplicationDtoBuilder reviewedBy(String reviewedBy) {
            this.reviewedBy = reviewedBy;
            return this;
        }

        public LeaveApplicationDtoBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public LeaveApplicationDto build() {
            return new LeaveApplicationDto(id, studentId, studentName, studentRollNo, studentDepartment, startDate, endDate, reason, attachmentUrl, status, reviewerComment, reviewedBy, createdAt);
        }
    }
}
