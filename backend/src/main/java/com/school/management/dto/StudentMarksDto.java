package com.school.management.dto;

import com.school.management.entity.ExamType;

public class StudentMarksDto {

    private Long id;
    private Long subjectId;
    private String subjectCode;
    private String subjectName;
    private ExamType examType;
    private double marksObtained;
    private double maxMarks;
    private double percentage;
    private String grade;
    private boolean isPassed;
    private boolean isAbsent;
    private double gradePoint;

    public StudentMarksDto() {
    }

    public StudentMarksDto(Long id, Long subjectId, String subjectCode, String subjectName, ExamType examType, double marksObtained, double maxMarks, double percentage, String grade, boolean isPassed, boolean isAbsent, double gradePoint) {
        this.id = id;
        this.subjectId = subjectId;
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.examType = examType;
        this.marksObtained = marksObtained;
        this.maxMarks = maxMarks;
        this.percentage = percentage;
        this.grade = grade;
        this.isPassed = isPassed;
        this.isAbsent = isAbsent;
        this.gradePoint = gradePoint;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public ExamType getExamType() {
        return examType;
    }

    public void setExamType(ExamType examType) {
        this.examType = examType;
    }

    public double getMarksObtained() {
        return marksObtained;
    }

    public void setMarksObtained(double marksObtained) {
        this.marksObtained = marksObtained;
    }

    public double getMaxMarks() {
        return maxMarks;
    }

    public void setMaxMarks(double maxMarks) {
        this.maxMarks = maxMarks;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public boolean isPassed() {
        return isPassed;
    }

    public void setPassed(boolean isPassed) {
        this.isPassed = isPassed;
    }

    public boolean isAbsent() {
        return isAbsent;
    }

    public void setAbsent(boolean isAbsent) {
        this.isAbsent = isAbsent;
    }

    public double getGradePoint() {
        return gradePoint;
    }

    public void setGradePoint(double gradePoint) {
        this.gradePoint = gradePoint;
    }

    public static StudentMarksDtoBuilder builder() {
        return new StudentMarksDtoBuilder();
    }

    public static class StudentMarksDtoBuilder {
        private Long id;
        private Long subjectId;
        private String subjectCode;
        private String subjectName;
        private ExamType examType;
        private double marksObtained;
        private double maxMarks;
        private double percentage;
        private String grade;
        private boolean isPassed;
        private boolean isAbsent;
        private double gradePoint;

        public StudentMarksDtoBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public StudentMarksDtoBuilder subjectId(Long subjectId) {
            this.subjectId = subjectId;
            return this;
        }

        public StudentMarksDtoBuilder subjectCode(String subjectCode) {
            this.subjectCode = subjectCode;
            return this;
        }

        public StudentMarksDtoBuilder subjectName(String subjectName) {
            this.subjectName = subjectName;
            return this;
        }

        public StudentMarksDtoBuilder examType(ExamType examType) {
            this.examType = examType;
            return this;
        }

        public StudentMarksDtoBuilder marksObtained(double marksObtained) {
            this.marksObtained = marksObtained;
            return this;
        }

        public StudentMarksDtoBuilder maxMarks(double maxMarks) {
            this.maxMarks = maxMarks;
            return this;
        }

        public StudentMarksDtoBuilder percentage(double percentage) {
            this.percentage = percentage;
            return this;
        }

        public StudentMarksDtoBuilder grade(String grade) {
            this.grade = grade;
            return this;
        }

        public StudentMarksDtoBuilder isPassed(boolean isPassed) {
            this.isPassed = isPassed;
            return this;
        }

        public StudentMarksDtoBuilder isAbsent(boolean isAbsent) {
            this.isAbsent = isAbsent;
            return this;
        }

        public StudentMarksDtoBuilder gradePoint(double gradePoint) {
            this.gradePoint = gradePoint;
            return this;
        }

        public StudentMarksDto build() {
            return new StudentMarksDto(id, subjectId, subjectCode, subjectName, examType, marksObtained, maxMarks, percentage, grade, isPassed, isAbsent, gradePoint);
        }
    }
}
