package com.school.management.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class StudentRequest {

    private String username;
    private String password;
    private String email;

    @NotBlank(message = "Roll number is required")
    private String rollNo;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Department is required")
    private String department;

    @NotNull(message = "Semester is required")
    @Min(value = 1, message = "Semester must be at least 1")
    private Integer semester;

    public StudentRequest() {
    }

    public StudentRequest(String username, String password, String email, String rollNo, String name, String department, Integer semester) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.rollNo = rollNo;
        this.name = name;
        this.department = department;
        this.semester = semester;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRollNo() {
        return rollNo;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Integer getSemester() {
        return semester;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
    }

    public static StudentRequestBuilder builder() {
        return new StudentRequestBuilder();
    }

    public static class StudentRequestBuilder {
        private String username;
        private String password;
        private String email;
        private String rollNo;
        private String name;
        private String department;
        private Integer semester;

        public StudentRequestBuilder username(String username) {
            this.username = username;
            return this;
        }

        public StudentRequestBuilder password(String password) {
            this.password = password;
            return this;
        }

        public StudentRequestBuilder email(String email) {
            this.email = email;
            return this;
        }

        public StudentRequestBuilder rollNo(String rollNo) {
            this.rollNo = rollNo;
            return this;
        }

        public StudentRequestBuilder name(String name) {
            this.name = name;
            return this;
        }

        public StudentRequestBuilder department(String department) {
            this.department = department;
            return this;
        }

        public StudentRequestBuilder semester(Integer semester) {
            this.semester = semester;
            return this;
        }

        public StudentRequest build() {
            return new StudentRequest(username, password, email, rollNo, name, department, semester);
        }
    }
}
