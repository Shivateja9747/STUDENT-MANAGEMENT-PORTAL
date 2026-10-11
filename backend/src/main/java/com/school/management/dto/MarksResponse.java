package com.school.management.dto;

import com.school.management.entity.ExamType;

public class MarksResponse {

    private Long id;
    private Long studentId;
    private String studentName;
    private String studentRollNo;
    private Long subjectId;
    private String subjectName;
    private ExamType examType;
    private Double marksObtained;
    private Double maxMarks;
    private Double percentage;
    private String grade;
    private Boolean isPassed;
    private Boolean isAbsent;
    private Integer rank;

    public MarksResponse() {
    }

    public MarksResponse(Long id, Long studentId, String studentName, String studentRollNo, Long subjectId, String subjectName, ExamType examType, Double marksObtained, Double maxMarks, Double percentage, String grade, Boolean isPassed, Boolean isAbsent, Integer rank) {
        this.id = id;
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentRollNo = studentRollNo;
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.examType = examType;
        this.marksObtained = marksObtained;
        this.maxMarks = maxMarks;
        this.percentage = percentage;
        this.grade = grade;
        this.isPassed = isPassed;
        this.isAbsent = isAbsent;
        this.rank = rank;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentRollNo() {
        return studentRollNo;
    }

    public void setStudentRollNo(String studentRollNo) {
        this.studentRollNo = studentRollNo;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
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

    public Double getMarksObtained() {
        return marksObtained;
    }

    public void setMarksObtained(Double marksObtained) {
        this.marksObtained = marksObtained;
    }

    public Double getMaxMarks() {
        return maxMarks;
    }

    public void setMaxMarks(Double maxMarks) {
        this.maxMarks = maxMarks;
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public Boolean getIsPassed() {
        return isPassed;
    }

    public void setIsPassed(Boolean isPassed) {
        this.isPassed = isPassed;
    }

    public Boolean getIsAbsent() {
        return isAbsent;
    }

    public void setIsAbsent(Boolean isAbsent) {
        this.isAbsent = isAbsent;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public static MarksResponseBuilder builder() {
        return new MarksResponseBuilder();
    }

    public static class MarksResponseBuilder {
        private Long id;
        private Long studentId;
        private String studentName;
        private String studentRollNo;
        private Long subjectId;
        private String subjectName;
        private ExamType examType;
        private Double marksObtained;
        private Double maxMarks;
        private Double percentage;
        private String grade;
        private Boolean isPassed;
        private Boolean isAbsent;
        private Integer rank;

        public MarksResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public MarksResponseBuilder studentId(Long studentId) {
            this.studentId = studentId;
            return this;
        }

        public MarksResponseBuilder studentName(String studentName) {
            this.studentName = studentName;
            return this;
        }

        public MarksResponseBuilder studentRollNo(String studentRollNo) {
            this.studentRollNo = studentRollNo;
            return this;
        }

        public MarksResponseBuilder subjectId(Long subjectId) {
            this.subjectId = subjectId;
            return this;
        }

        public MarksResponseBuilder subjectName(String subjectName) {
            this.subjectName = subjectName;
            return this;
        }

        public MarksResponseBuilder examType(ExamType examType) {
            this.examType = examType;
            return this;
        }

        public MarksResponseBuilder marksObtained(Double marksObtained) {
            this.marksObtained = marksObtained;
            return this;
        }

        public MarksResponseBuilder maxMarks(Double maxMarks) {
            this.maxMarks = maxMarks;
            return this;
        }

        public MarksResponseBuilder percentage(Double percentage) {
            this.percentage = percentage;
            return this;
        }

        public MarksResponseBuilder grade(String grade) {
            this.grade = grade;
            return this;
        }

        public MarksResponseBuilder isPassed(Boolean isPassed) {
            this.isPassed = isPassed;
            return this;
        }

        public MarksResponseBuilder isAbsent(Boolean isAbsent) {
            this.isAbsent = isAbsent;
            return this;
        }

        public MarksResponseBuilder rank(Integer rank) {
            this.rank = rank;
            return this;
        }

        public MarksResponse build() {
            return new MarksResponse(id, studentId, studentName, studentRollNo, subjectId, subjectName, examType, marksObtained, maxMarks, percentage, grade, isPassed, isAbsent, rank);
        }
    }
}
