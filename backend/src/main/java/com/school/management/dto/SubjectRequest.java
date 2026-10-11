package com.school.management.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SubjectRequest {

    @NotBlank(message = "Subject code is required")
    private String code;

    @NotBlank(message = "Subject name is required")
    private String name;

    @NotNull(message = "Semester is required")
    @Min(value = 1, message = "Semester must be at least 1")
    private Integer semester;

    private Long teacherId;

    public SubjectRequest() {
    }

    public SubjectRequest(String code, String name, Integer semester, Long teacherId) {
        this.code = code;
        this.name = name;
        this.semester = semester;
        this.teacherId = teacherId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSemester() {
        return semester;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public static SubjectRequestBuilder builder() {
        return new SubjectRequestBuilder();
    }

    public static class SubjectRequestBuilder {
        private String code;
        private String name;
        private Integer semester;
        private Long teacherId;

        public SubjectRequestBuilder code(String code) {
            this.code = code;
            return this;
        }

        public SubjectRequestBuilder name(String name) {
            this.name = name;
            return this;
        }

        public SubjectRequestBuilder semester(Integer semester) {
            this.semester = semester;
            return this;
        }

        public SubjectRequestBuilder teacherId(Long teacherId) {
            this.teacherId = teacherId;
            return this;
        }

        public SubjectRequest build() {
            return new SubjectRequest(code, name, semester, teacherId);
        }
    }
}
