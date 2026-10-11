package com.school.management.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @Column(nullable = false, unique = true)
    private String rollNo;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String department;

    @Column(nullable = false)
    private Integer semester;

    @Column(nullable = false)
    private String section = "A";

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public Student() {
    }

    public Student(Long id, User user, String rollNo, String name, String department, Integer semester, String section) {
        this.id = id;
        this.user = user;
        this.rollNo = rollNo;
        this.name = name;
        this.department = department;
        this.semester = semester;
        this.section = section != null ? section : "A";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section != null ? section : "A";
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public static StudentBuilder builder() {
        return new StudentBuilder();
    }

    public static class StudentBuilder {
        private Long id;
        private User user;
        private String rollNo;
        private String name;
        private String department;
        private Integer semester;
        private String section = "A";

        public StudentBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public StudentBuilder user(User user) {
            this.user = user;
            return this;
        }

        public StudentBuilder rollNo(String rollNo) {
            this.rollNo = rollNo;
            return this;
        }

        public StudentBuilder name(String name) {
            this.name = name;
            return this;
        }

        public StudentBuilder department(String department) {
            this.department = department;
            return this;
        }

        public StudentBuilder semester(Integer semester) {
            this.semester = semester;
            return this;
        }

        public StudentBuilder section(String section) {
            this.section = section != null ? section : "A";
            return this;
        }

        public Student build() {
            return new Student(id, user, rollNo, name, department, semester, section);
        }
    }
}
