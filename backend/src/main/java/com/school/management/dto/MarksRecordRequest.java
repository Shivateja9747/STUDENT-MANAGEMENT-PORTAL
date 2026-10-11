package com.school.management.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class MarksRecordRequest {

    @NotNull(message = "Student ID is required")
    private Long studentId;

    @Min(value = 0, message = "Marks obtained cannot be negative")
    private Double marksObtained = 0.0;

    @NotNull(message = "Max marks is required")
    @Min(value = 1, message = "Max marks must be greater than 0")
    private Double maxMarks = 100.0;

    private Boolean isAbsent = false;

    public MarksRecordRequest() {
    }

    public MarksRecordRequest(Long studentId, Double marksObtained, Double maxMarks, Boolean isAbsent) {
        this.studentId = studentId;
        this.marksObtained = marksObtained != null ? marksObtained : 0.0;
        this.maxMarks = maxMarks != null ? maxMarks : 100.0;
        this.isAbsent = isAbsent != null ? isAbsent : false;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Double getMarksObtained() {
        return marksObtained;
    }

    public void setMarksObtained(Double marksObtained) {
        this.marksObtained = marksObtained != null ? marksObtained : 0.0;
    }

    public Double getMaxMarks() {
        return maxMarks;
    }

    public void setMaxMarks(Double maxMarks) {
        this.maxMarks = maxMarks != null ? maxMarks : 100.0;
    }

    public Boolean getIsAbsent() {
        return isAbsent;
    }

    public void setIsAbsent(Boolean isAbsent) {
        this.isAbsent = isAbsent != null ? isAbsent : false;
    }

    public static MarksRecordRequestBuilder builder() {
        return new MarksRecordRequestBuilder();
    }

    public static class MarksRecordRequestBuilder {
        private Long studentId;
        private Double marksObtained = 0.0;
        private Double maxMarks = 100.0;
        private Boolean isAbsent = false;

        public MarksRecordRequestBuilder studentId(Long studentId) {
            this.studentId = studentId;
            return this;
        }

        public MarksRecordRequestBuilder marksObtained(Double marksObtained) {
            this.marksObtained = marksObtained != null ? marksObtained : 0.0;
            return this;
        }

        public MarksRecordRequestBuilder maxMarks(Double maxMarks) {
            this.maxMarks = maxMarks != null ? maxMarks : 100.0;
            return this;
        }

        public MarksRecordRequestBuilder isAbsent(Boolean isAbsent) {
            this.isAbsent = isAbsent != null ? isAbsent : false;
            return this;
        }

        public MarksRecordRequest build() {
            return new MarksRecordRequest(studentId, marksObtained, maxMarks, isAbsent);
        }
    }
}
