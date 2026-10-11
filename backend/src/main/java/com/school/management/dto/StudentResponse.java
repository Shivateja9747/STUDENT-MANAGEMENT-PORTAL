package com.school.management.dto;

public class StudentResponse {

    private Long id;
    private Long userId;
    private String username;
    private String rollNo;
    private String name;
    private String department;
    private Integer semester;

    public StudentResponse() {
    }

    public StudentResponse(Long id, Long userId, String username, String rollNo, String name, String department, Integer semester) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.rollNo = rollNo;
        this.name = name;
        this.department = department;
        this.semester = semester;
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

    public static StudentResponseBuilder builder() {
        return new StudentResponseBuilder();
    }

    public static class StudentResponseBuilder {
        private Long id;
        private Long userId;
        private String username;
        private String rollNo;
        private String name;
        private String department;
        private Integer semester;

        public StudentResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public StudentResponseBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public StudentResponseBuilder username(String username) {
            this.username = username;
            return this;
        }

        public StudentResponseBuilder rollNo(String rollNo) {
            this.rollNo = rollNo;
            return this;
        }

        public StudentResponseBuilder name(String name) {
            this.name = name;
            return this;
        }

        public StudentResponseBuilder department(String department) {
            this.department = department;
            return this;
        }

        public StudentResponseBuilder semester(Integer semester) {
            this.semester = semester;
            return this;
        }

        public StudentResponse build() {
            return new StudentResponse(id, userId, username, rollNo, name, department, semester);
        }
    }
}
