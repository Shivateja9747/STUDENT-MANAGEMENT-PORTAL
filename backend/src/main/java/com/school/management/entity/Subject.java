package com.school.management.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "subjects")
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer semester;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    public Subject() {
    }

    public Subject(Long id, String code, String name, Integer semester, Teacher teacher) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.semester = semester;
        this.teacher = teacher;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    @Column(name = "deleted_at")
    private java.time.LocalDateTime deletedAt;

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public java.time.LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(java.time.LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public static SubjectBuilder builder() {
        return new SubjectBuilder();
    }

    public static class SubjectBuilder {
        private Long id;
        private String code;
        private String name;
        private Integer semester;
        private Teacher teacher;

        public SubjectBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public SubjectBuilder code(String code) {
            this.code = code;
            return this;
        }

        public SubjectBuilder name(String name) {
            this.name = name;
            return this;
        }

        public SubjectBuilder semester(Integer semester) {
            this.semester = semester;
            return this;
        }

        public SubjectBuilder teacher(Teacher teacher) {
            this.teacher = teacher;
            return this;
        }

        public Subject build() {
            return new Subject(id, code, name, semester, teacher);
        }
    }
}
