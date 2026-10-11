package com.school.management.dto;

public class SubjectResponse {

    private Long id;
    private String code;
    private String name;
    private Integer semester;
    private Long teacherId;
    private String teacherName;

    public SubjectResponse() {
    }

    public SubjectResponse(Long id, String code, String name, Integer semester, Long teacherId, String teacherName) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.semester = semester;
        this.teacherId = teacherId;
        this.teacherName = teacherName;
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

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public static SubjectResponseBuilder builder() {
        return new SubjectResponseBuilder();
    }

    public static class SubjectResponseBuilder {
        private Long id;
        private String code;
        private String name;
        private Integer semester;
        private Long teacherId;
        private String teacherName;

        public SubjectResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public SubjectResponseBuilder code(String code) {
            this.code = code;
            return this;
        }

        public SubjectResponseBuilder name(String name) {
            this.name = name;
            return this;
        }

        public SubjectResponseBuilder semester(Integer semester) {
            this.semester = semester;
            return this;
        }

        public SubjectResponseBuilder teacherId(Long teacherId) {
            this.teacherId = teacherId;
            return this;
        }

        public SubjectResponseBuilder teacherName(String teacherName) {
            this.teacherName = teacherName;
            return this;
        }

        public SubjectResponse build() {
            return new SubjectResponse(id, code, name, semester, teacherId, teacherName);
        }
    }
}
