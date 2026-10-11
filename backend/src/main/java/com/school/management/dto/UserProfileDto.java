package com.school.management.dto;

public class UserProfileDto {

    private Long userId;
    private String username;
    private String role;
    private String name;
    private String email;
    private String phone;
    private String department;
    private String rollNo;
    private Integer semester;
    private boolean mustChangePassword;

    public UserProfileDto() {
    }

    public UserProfileDto(Long userId, String username, String role, String name, String email, String phone, String department, String rollNo, Integer semester, boolean mustChangePassword) {
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.department = department;
        this.rollNo = rollNo;
        this.semester = semester;
        this.mustChangePassword = mustChangePassword;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getRollNo() {
        return rollNo;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
    }

    public Integer getSemester() {
        return semester;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
    }

    public boolean isMustChangePassword() {
        return mustChangePassword;
    }

    public void setMustChangePassword(boolean mustChangePassword) {
        this.mustChangePassword = mustChangePassword;
    }

    public static UserProfileDtoBuilder builder() {
        return new UserProfileDtoBuilder();
    }

    public static class UserProfileDtoBuilder {
        private Long userId;
        private String username;
        private String role;
        private String name;
        private String email;
        private String phone;
        private String department;
        private String rollNo;
        private Integer semester;
        private boolean mustChangePassword;

        public UserProfileDtoBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public UserProfileDtoBuilder username(String username) {
            this.username = username;
            return this;
        }

        public UserProfileDtoBuilder role(String role) {
            this.role = role;
            return this;
        }

        public UserProfileDtoBuilder name(String name) {
            this.name = name;
            return this;
        }

        public UserProfileDtoBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserProfileDtoBuilder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public UserProfileDtoBuilder department(String department) {
            this.department = department;
            return this;
        }

        public UserProfileDtoBuilder rollNo(String rollNo) {
            this.rollNo = rollNo;
            return this;
        }

        public UserProfileDtoBuilder semester(Integer semester) {
            this.semester = semester;
            return this;
        }

        public UserProfileDtoBuilder mustChangePassword(boolean mustChangePassword) {
            this.mustChangePassword = mustChangePassword;
            return this;
        }

        public UserProfileDto build() {
            return new UserProfileDto(userId, username, role, name, email, phone, department, rollNo, semester, mustChangePassword);
        }
    }
}
