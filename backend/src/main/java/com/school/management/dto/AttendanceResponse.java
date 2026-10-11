package com.school.management.dto;

import com.school.management.entity.AttendanceStatus;

import java.time.LocalDate;

public class AttendanceResponse {

    private Long id;
    private Long studentId;
    private String studentName;
    private String studentRollNo;
    private Long subjectId;
    private String subjectName;
    private LocalDate date;
    private Integer period;
    private AttendanceStatus status;
    private String remark;

    public AttendanceResponse() {
    }

    public AttendanceResponse(Long id, Long studentId, String studentName, String studentRollNo, Long subjectId, String subjectName, LocalDate date, Integer period, AttendanceStatus status, String remark) {
        this.id = id;
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentRollNo = studentRollNo;
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.date = date;
        this.period = period;
        this.status = status;
        this.remark = remark;
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

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Integer getPeriod() {
        return period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
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

    public static AttendanceResponseBuilder builder() {
        return new AttendanceResponseBuilder();
    }

    public static class AttendanceResponseBuilder {
        private Long id;
        private Long studentId;
        private String studentName;
        private String studentRollNo;
        private Long subjectId;
        private String subjectName;
        private LocalDate date;
        private Integer period;
        private AttendanceStatus status;
        private String remark;

        public AttendanceResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public AttendanceResponseBuilder studentId(Long studentId) {
            this.studentId = studentId;
            return this;
        }

        public AttendanceResponseBuilder studentName(String studentName) {
            this.studentName = studentName;
            return this;
        }

        public AttendanceResponseBuilder studentRollNo(String studentRollNo) {
            this.studentRollNo = studentRollNo;
            return this;
        }

        public AttendanceResponseBuilder subjectId(Long subjectId) {
            this.subjectId = subjectId;
            return this;
        }

        public AttendanceResponseBuilder subjectName(String subjectName) {
            this.subjectName = subjectName;
            return this;
        }

        public AttendanceResponseBuilder date(LocalDate date) {
            this.date = date;
            return this;
        }

        public AttendanceResponseBuilder period(Integer period) {
            this.period = period;
            return this;
        }

        public AttendanceResponseBuilder status(AttendanceStatus status) {
            this.status = status;
            return this;
        }

        public AttendanceResponseBuilder remark(String remark) {
            this.remark = remark;
            return this;
        }

        public AttendanceResponse build() {
            return new AttendanceResponse(id, studentId, studentName, studentRollNo, subjectId, subjectName, date, period, status, remark);
        }
    }
}
