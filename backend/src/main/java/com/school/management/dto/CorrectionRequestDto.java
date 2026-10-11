package com.school.management.dto;

import com.school.management.entity.AttendanceCorrection;
import com.school.management.entity.AttendanceStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CorrectionRequestDto {

    private Long id;

    @NotNull(message = "Attendance ID is required")
    private Long attendanceId;

    private Long studentId;
    private String studentName;
    private String studentRollNo;
    private String subjectName;
    private LocalDate attendanceDate;
    private AttendanceStatus currentStatus;

    @NotNull(message = "Requested status is required")
    private AttendanceStatus requestedStatus;

    @NotBlank(message = "Reason is required")
    private String reason;

    private AttendanceCorrection.CorrectionStatus status;
    private String reviewerComment;
    private LocalDateTime createdAt;

    public CorrectionRequestDto() {
    }

    public CorrectionRequestDto(Long id, Long attendanceId, Long studentId, String studentName, String studentRollNo, String subjectName, LocalDate attendanceDate, AttendanceStatus currentStatus, AttendanceStatus requestedStatus, String reason, AttendanceCorrection.CorrectionStatus status, String reviewerComment, LocalDateTime createdAt) {
        this.id = id;
        this.attendanceId = attendanceId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentRollNo = studentRollNo;
        this.subjectName = subjectName;
        this.attendanceDate = attendanceDate;
        this.currentStatus = currentStatus;
        this.requestedStatus = requestedStatus;
        this.reason = reason;
        this.status = status;
        this.reviewerComment = reviewerComment;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(Long attendanceId) {
        this.attendanceId = attendanceId;
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

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public LocalDate getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(LocalDate attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public AttendanceStatus getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(AttendanceStatus currentStatus) {
        this.currentStatus = currentStatus;
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

    public AttendanceCorrection.CorrectionStatus getStatus() {
        return status;
    }

    public void setStatus(AttendanceCorrection.CorrectionStatus status) {
        this.status = status;
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

    public static CorrectionRequestDtoBuilder builder() {
        return new CorrectionRequestDtoBuilder();
    }

    public static class CorrectionRequestDtoBuilder {
        private Long id;
        private Long attendanceId;
        private Long studentId;
        private String studentName;
        private String studentRollNo;
        private String subjectName;
        private LocalDate attendanceDate;
        private AttendanceStatus currentStatus;
        private AttendanceStatus requestedStatus;
        private String reason;
        private AttendanceCorrection.CorrectionStatus status;
        private String reviewerComment;
        private LocalDateTime createdAt;

        public CorrectionRequestDtoBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public CorrectionRequestDtoBuilder attendanceId(Long attendanceId) {
            this.attendanceId = attendanceId;
            return this;
        }

        public CorrectionRequestDtoBuilder studentId(Long studentId) {
            this.studentId = studentId;
            return this;
        }

        public CorrectionRequestDtoBuilder studentName(String studentName) {
            this.studentName = studentName;
            return this;
        }

        public CorrectionRequestDtoBuilder studentRollNo(String studentRollNo) {
            this.studentRollNo = studentRollNo;
            return this;
        }

        public CorrectionRequestDtoBuilder subjectName(String subjectName) {
            this.subjectName = subjectName;
            return this;
        }

        public CorrectionRequestDtoBuilder attendanceDate(LocalDate attendanceDate) {
            this.attendanceDate = attendanceDate;
            return this;
        }

        public CorrectionRequestDtoBuilder currentStatus(AttendanceStatus currentStatus) {
            this.currentStatus = currentStatus;
            return this;
        }

        public CorrectionRequestDtoBuilder requestedStatus(AttendanceStatus requestedStatus) {
            this.requestedStatus = requestedStatus;
            return this;
        }

        public CorrectionRequestDtoBuilder reason(String reason) {
            this.reason = reason;
            return this;
        }

        public CorrectionRequestDtoBuilder status(AttendanceCorrection.CorrectionStatus status) {
            this.status = status;
            return this;
        }

        public CorrectionRequestDtoBuilder reviewerComment(String reviewerComment) {
            this.reviewerComment = reviewerComment;
            return this;
        }

        public CorrectionRequestDtoBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public CorrectionRequestDto build() {
            return new CorrectionRequestDto(id, attendanceId, studentId, studentName, studentRollNo, subjectName, attendanceDate, currentStatus, requestedStatus, reason, status, reviewerComment, createdAt);
        }
    }
}
