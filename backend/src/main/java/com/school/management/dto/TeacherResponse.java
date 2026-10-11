package com.school.management.dto;

public class TeacherResponse {

    private Long id;
    private Long userId;
    private String username;
    private String name;
    private String department;

    public TeacherResponse() {
    }

    public TeacherResponse(Long id, Long userId, String username, String name, String department) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.name = name;
        this.department = department;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public static TeacherResponseBuilder builder() {
        return new TeacherResponseBuilder();
    }

    public static class TeacherResponseBuilder {
        private Long id;
        private Long userId;
        private String username;
        private String name;
        private String department;

        public TeacherResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public TeacherResponseBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public TeacherResponseBuilder username(String username) {
            this.username = username;
            return this;
        }

        public TeacherResponseBuilder name(String name) {
            this.name = name;
            return this;
        }

        public TeacherResponseBuilder department(String department) {
            this.department = department;
            return this;
        }

        public TeacherResponse build() {
            return new TeacherResponse(id, userId, username, name, department);
        }
    }
}
