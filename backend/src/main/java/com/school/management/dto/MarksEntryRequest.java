package com.school.management.dto;

import com.school.management.entity.ExamType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class MarksEntryRequest {

    @NotNull(message = "Subject ID is required")
    private Long subjectId;

    @NotNull(message = "Exam type is required")
    private ExamType examType;

    @NotNull(message = "Records list cannot be null")
    @Valid
    private List<MarksRecordRequest> records;

    public MarksEntryRequest() {
    }

    public MarksEntryRequest(Long subjectId, ExamType examType, List<MarksRecordRequest> records) {
        this.subjectId = subjectId;
        this.examType = examType;
        this.records = records;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public ExamType getExamType() {
        return examType;
    }

    public void setExamType(ExamType examType) {
        this.examType = examType;
    }

    public List<MarksRecordRequest> getRecords() {
        return records;
    }

    public void setRecords(List<MarksRecordRequest> records) {
        this.records = records;
    }

    public static MarksEntryRequestBuilder builder() {
        return new MarksEntryRequestBuilder();
    }

    public static class MarksEntryRequestBuilder {
        private Long subjectId;
        private ExamType examType;
        private List<MarksRecordRequest> records;

        public MarksEntryRequestBuilder subjectId(Long subjectId) {
            this.subjectId = subjectId;
            return this;
        }

        public MarksEntryRequestBuilder examType(ExamType examType) {
            this.examType = examType;
            return this;
        }

        public MarksEntryRequestBuilder records(List<MarksRecordRequest> records) {
            this.records = records;
            return this;
        }

        public MarksEntryRequest build() {
            return new MarksEntryRequest(subjectId, examType, records);
        }
    }
}
