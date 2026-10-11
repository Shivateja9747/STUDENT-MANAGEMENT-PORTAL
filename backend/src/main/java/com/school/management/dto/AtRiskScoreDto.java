package com.school.management.dto;

import java.util.List;

public class AtRiskScoreDto {

    private Long studentId;
    private String studentName;
    private String rollNo;
    private String department;
    private Integer semester;
    private String section;
    private int riskScore;
    private String riskLevel;
    private double attendancePercentage;
    private double marksPercentage;
    private int failedExamsCount;
    private String trendMessage;
    private List<String> reasons;

    public AtRiskScoreDto() {
    }

    public AtRiskScoreDto(Long studentId, String studentName, String rollNo, String department, Integer semester, String section, int riskScore, String riskLevel, double attendancePercentage, double marksPercentage, int failedExamsCount, String trendMessage, List<String> reasons) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.rollNo = rollNo;
        this.department = department;
        this.semester = semester;
        this.section = section;
        this.riskScore = riskScore;
        this.riskLevel = riskLevel;
        this.attendancePercentage = attendancePercentage;
        this.marksPercentage = marksPercentage;
        this.failedExamsCount = failedExamsCount;
        this.trendMessage = trendMessage;
        this.reasons = reasons;
    }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getRollNo() { return rollNo; }
    public void setRollNo(String rollNo) { this.rollNo = rollNo; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public Integer getSemester() { return semester; }
    public void setSemester(Integer semester) { this.semester = semester; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    public int getRiskScore() { return riskScore; }
    public void setRiskScore(int riskScore) { this.riskScore = riskScore; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }

    public double getAttendancePercentage() { return attendancePercentage; }
    public void setAttendancePercentage(double attendancePercentage) { this.attendancePercentage = attendancePercentage; }

    public double getMarksPercentage() { return marksPercentage; }
    public void setMarksPercentage(double marksPercentage) { this.marksPercentage = marksPercentage; }

    public int getFailedExamsCount() { return failedExamsCount; }
    public void setFailedExamsCount(int failedExamsCount) { this.failedExamsCount = failedExamsCount; }

    public String getTrendMessage() { return trendMessage; }
    public void setTrendMessage(String trendMessage) { this.trendMessage = trendMessage; }

    public List<String> getReasons() { return reasons; }
    public void setReasons(List<String> reasons) { this.reasons = reasons; }

    public static AtRiskScoreDtoBuilder builder() { return new AtRiskScoreDtoBuilder(); }

    public static class AtRiskScoreDtoBuilder {
        private Long studentId;
        private String studentName;
        private String rollNo;
        private String department;
        private Integer semester;
        private String section;
        private int riskScore;
        private String riskLevel;
        private double attendancePercentage;
        private double marksPercentage;
        private int failedExamsCount;
        private String trendMessage;
        private List<String> reasons;

        public AtRiskScoreDtoBuilder studentId(Long studentId) { this.studentId = studentId; return this; }
        public AtRiskScoreDtoBuilder studentName(String studentName) { this.studentName = studentName; return this; }
        public AtRiskScoreDtoBuilder rollNo(String rollNo) { this.rollNo = rollNo; return this; }
        public AtRiskScoreDtoBuilder department(String department) { this.department = department; return this; }
        public AtRiskScoreDtoBuilder semester(Integer semester) { this.semester = semester; return this; }
        public AtRiskScoreDtoBuilder section(String section) { this.section = section; return this; }
        public AtRiskScoreDtoBuilder riskScore(int riskScore) { this.riskScore = riskScore; return this; }
        public AtRiskScoreDtoBuilder riskLevel(String riskLevel) { this.riskLevel = riskLevel; return this; }
        public AtRiskScoreDtoBuilder attendancePercentage(double attendancePercentage) { this.attendancePercentage = attendancePercentage; return this; }
        public AtRiskScoreDtoBuilder marksPercentage(double marksPercentage) { this.marksPercentage = marksPercentage; return this; }
        public AtRiskScoreDtoBuilder failedExamsCount(int failedExamsCount) { this.failedExamsCount = failedExamsCount; return this; }
        public AtRiskScoreDtoBuilder trendMessage(String trendMessage) { this.trendMessage = trendMessage; return this; }
        public AtRiskScoreDtoBuilder reasons(List<String> reasons) { this.reasons = reasons; return this; }

        public AtRiskScoreDto build() {
            return new AtRiskScoreDto(studentId, studentName, rollNo, department, semester, section, riskScore, riskLevel, attendancePercentage, marksPercentage, failedExamsCount, trendMessage, reasons);
        }
    }
}
