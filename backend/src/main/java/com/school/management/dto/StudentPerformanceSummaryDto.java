package com.school.management.dto;

import java.util.List;

public class StudentPerformanceSummaryDto {

    private Long studentId;
    private String studentName;
    private String rollNo;
    private String department;
    private Integer semester;
    private double overallAttendancePercentage;
    private boolean hasLowAttendanceWarning;
    private double overallMarksPercentage;
    private double sgpa;
    private String gpaGrade;
    private int totalSubjects;
    private List<StudentSubjectAttendanceDto> subjectAttendances;
    private List<StudentMarksDto> marksList;

    public StudentPerformanceSummaryDto() {
    }

    public StudentPerformanceSummaryDto(Long studentId, String studentName, String rollNo, String department, Integer semester, double overallAttendancePercentage, boolean hasLowAttendanceWarning, double overallMarksPercentage, double sgpa, String gpaGrade, int totalSubjects, List<StudentSubjectAttendanceDto> subjectAttendances, List<StudentMarksDto> marksList) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.rollNo = rollNo;
        this.department = department;
        this.semester = semester;
        this.overallAttendancePercentage = overallAttendancePercentage;
        this.hasLowAttendanceWarning = hasLowAttendanceWarning;
        this.overallMarksPercentage = overallMarksPercentage;
        this.sgpa = sgpa;
        this.gpaGrade = gpaGrade;
        this.totalSubjects = totalSubjects;
        this.subjectAttendances = subjectAttendances;
        this.marksList = marksList;
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

    public String getRollNo() {
        return rollNo;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
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

    public double getOverallAttendancePercentage() {
        return overallAttendancePercentage;
    }

    public void setOverallAttendancePercentage(double overallAttendancePercentage) {
        this.overallAttendancePercentage = overallAttendancePercentage;
    }

    public boolean isHasLowAttendanceWarning() {
        return hasLowAttendanceWarning;
    }

    public void setHasLowAttendanceWarning(boolean hasLowAttendanceWarning) {
        this.hasLowAttendanceWarning = hasLowAttendanceWarning;
    }

    public double getOverallMarksPercentage() {
        return overallMarksPercentage;
    }

    public void setOverallMarksPercentage(double overallMarksPercentage) {
        this.overallMarksPercentage = overallMarksPercentage;
    }

    public double getSgpa() {
        return sgpa;
    }

    public void setSgpa(double sgpa) {
        this.sgpa = sgpa;
    }

    public String getGpaGrade() {
        return gpaGrade;
    }

    public void setGpaGrade(String gpaGrade) {
        this.gpaGrade = gpaGrade;
    }

    public int getTotalSubjects() {
        return totalSubjects;
    }

    public void setTotalSubjects(int totalSubjects) {
        this.totalSubjects = totalSubjects;
    }

    public List<StudentSubjectAttendanceDto> getSubjectAttendances() {
        return subjectAttendances;
    }

    public void setSubjectAttendances(List<StudentSubjectAttendanceDto> subjectAttendances) {
        this.subjectAttendances = subjectAttendances;
    }

    public List<StudentMarksDto> getMarksList() {
        return marksList;
    }

    public void setMarksList(List<StudentMarksDto> marksList) {
        this.marksList = marksList;
    }

    public static StudentPerformanceSummaryDtoBuilder builder() {
        return new StudentPerformanceSummaryDtoBuilder();
    }

    public static class StudentPerformanceSummaryDtoBuilder {
        private Long studentId;
        private String studentName;
        private String rollNo;
        private String department;
        private Integer semester;
        private double overallAttendancePercentage;
        private boolean hasLowAttendanceWarning;
        private double overallMarksPercentage;
        private double sgpa;
        private String gpaGrade;
        private int totalSubjects;
        private List<StudentSubjectAttendanceDto> subjectAttendances;
        private List<StudentMarksDto> marksList;

        public StudentPerformanceSummaryDtoBuilder studentId(Long studentId) {
            this.studentId = studentId;
            return this;
        }

        public StudentPerformanceSummaryDtoBuilder studentName(String studentName) {
            this.studentName = studentName;
            return this;
        }

        public StudentPerformanceSummaryDtoBuilder rollNo(String rollNo) {
            this.rollNo = rollNo;
            return this;
        }

        public StudentPerformanceSummaryDtoBuilder department(String department) {
            this.department = department;
            return this;
        }

        public StudentPerformanceSummaryDtoBuilder semester(Integer semester) {
            this.semester = semester;
            return this;
        }

        public StudentPerformanceSummaryDtoBuilder overallAttendancePercentage(double overallAttendancePercentage) {
            this.overallAttendancePercentage = overallAttendancePercentage;
            return this;
        }

        public StudentPerformanceSummaryDtoBuilder hasLowAttendanceWarning(boolean hasLowAttendanceWarning) {
            this.hasLowAttendanceWarning = hasLowAttendanceWarning;
            return this;
        }

        public StudentPerformanceSummaryDtoBuilder overallMarksPercentage(double overallMarksPercentage) {
            this.overallMarksPercentage = overallMarksPercentage;
            return this;
        }

        public StudentPerformanceSummaryDtoBuilder sgpa(double sgpa) {
            this.sgpa = sgpa;
            return this;
        }

        public StudentPerformanceSummaryDtoBuilder gpaGrade(String gpaGrade) {
            this.gpaGrade = gpaGrade;
            return this;
        }

        public StudentPerformanceSummaryDtoBuilder totalSubjects(int totalSubjects) {
            this.totalSubjects = totalSubjects;
            return this;
        }

        public StudentPerformanceSummaryDtoBuilder subjectAttendances(List<StudentSubjectAttendanceDto> subjectAttendances) {
            this.subjectAttendances = subjectAttendances;
            return this;
        }

        public StudentPerformanceSummaryDtoBuilder marksList(List<StudentMarksDto> marksList) {
            this.marksList = marksList;
            return this;
        }

        public StudentPerformanceSummaryDto build() {
            return new StudentPerformanceSummaryDto(studentId, studentName, rollNo, department, semester, overallAttendancePercentage, hasLowAttendanceWarning, overallMarksPercentage, sgpa, gpaGrade, totalSubjects, subjectAttendances, marksList);
        }
    }
}
