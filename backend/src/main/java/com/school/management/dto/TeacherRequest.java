package com.school.management.dto;

import jakarta.validation.constraints.NotBlank;

public class TeacherRequest {

    private String username;
    private String password;
    private String email;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Department is required")
    private String department;

    public TeacherRequest() {
    }

    public TeacherRequest(String username, String password, String email, String name, String department) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.name = name;
        this.department = department;
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

    public static TeacherRequestBuilder builder() {
        return new TeacherRequestBuilder();
    }

    public static class TeacherRequestBuilder {
        private String username;
        private String password;
        private String email;
        private String name;
        private String department;

        public TeacherRequestBuilder username(String username) {
            this.username = username;
            return this;
        }

        public TeacherRequestBuilder password(String password) {
            this.password = password;
            return this;
        }

        public TeacherRequestBuilder email(String email) {
            this.email = email;
            return this;
        }

        public TeacherRequestBuilder name(String name) {
            this.name = name;
            return this;
        }

        public TeacherRequestBuilder department(String department) {
            this.department = department;
            return this;
        }

        public TeacherRequest build() {
            return new TeacherRequest(username, password, email, name, department);
        }
    }
}
