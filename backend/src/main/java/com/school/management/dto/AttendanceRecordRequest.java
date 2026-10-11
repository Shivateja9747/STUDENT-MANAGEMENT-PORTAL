package com.school.management.dto;

import com.school.management.entity.AttendanceStatus;
import jakarta.validation.constraints.NotNull;

public class AttendanceRecordRequest {

    @NotNull(message = "Student ID is required")
    private Long studentId;

    @NotNull(message = "Attendance status is required")
    private AttendanceStatus status;

    private String remark;

    public AttendanceRecordRequest() {
    }

    public AttendanceRecordRequest(Long studentId, AttendanceStatus status, String remark) {
        this.studentId = studentId;
        this.status = status;
        this.remark = remark;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    public void setStatus(AttendanceStatus status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public static AttendanceRecordRequestBuilder builder() {
        return new AttendanceRecordRequestBuilder();
    }

    public static class AttendanceRecordRequestBuilder {
        private Long studentId;
        private AttendanceStatus status;
        private String remark;

        public AttendanceRecordRequestBuilder studentId(Long studentId) {
            this.studentId = studentId;
            return this;
        }

        public AttendanceRecordRequestBuilder status(AttendanceStatus status) {
            this.status = status;
            return this;
        }

        public AttendanceRecordRequestBuilder remark(String remark) {
            this.remark = remark;
            return this;
        }

        public AttendanceRecordRequest build() {
            return new AttendanceRecordRequest(studentId, status, remark);
        }
    }
}
