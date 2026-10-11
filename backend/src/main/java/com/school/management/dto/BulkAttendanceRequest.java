package com.school.management.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;
import java.util.List;

public class BulkAttendanceRequest {

    @NotNull(message = "Subject ID is required")
    private Long subjectId;

    @NotNull(message = "Date is required")
    @PastOrPresent(message = "Attendance date cannot be in the future")
    private LocalDate date;

    private Integer period = 1;

    @NotNull(message = "Attendance records list cannot be null")
    @Valid
    private List<AttendanceRecordRequest> records;

    public BulkAttendanceRequest() {
    }

    public BulkAttendanceRequest(Long subjectId, LocalDate date, Integer period, List<AttendanceRecordRequest> records) {
        this.subjectId = subjectId;
        this.date = date;
        this.period = period != null ? period : 1;
        this.records = records;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
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
        this.period = period != null ? period : 1;
    }

    public List<AttendanceRecordRequest> getRecords() {
        return records;
    }

    public void setRecords(List<AttendanceRecordRequest> records) {
        this.records = records;
    }

    public static BulkAttendanceRequestBuilder builder() {
        return new BulkAttendanceRequestBuilder();
    }

    public static class BulkAttendanceRequestBuilder {
        private Long subjectId;
        private LocalDate date;
        private Integer period = 1;
        private List<AttendanceRecordRequest> records;

        public BulkAttendanceRequestBuilder subjectId(Long subjectId) {
            this.subjectId = subjectId;
            return this;
        }

        public BulkAttendanceRequestBuilder date(LocalDate date) {
            this.date = date;
            return this;
        }

        public BulkAttendanceRequestBuilder period(Integer period) {
            this.period = period != null ? period : 1;
            return this;
        }

        public BulkAttendanceRequestBuilder records(List<AttendanceRecordRequest> records) {
            this.records = records;
            return this;
        }

        public BulkAttendanceRequest build() {
            return new BulkAttendanceRequest(subjectId, date, period, records);
        }
    }
}
