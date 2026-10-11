package com.school.management.dto;

public class StudentSubjectAttendanceDto {

    private Long subjectId;
    private String subjectCode;
    private String subjectName;
    private int totalClasses;
    private int presentClasses;
    private int absentClasses;
    private double percentage;
    private boolean lowAttendance;

    public StudentSubjectAttendanceDto() {
    }

    public StudentSubjectAttendanceDto(Long subjectId, String subjectCode, String subjectName, int totalClasses, int presentClasses, int absentClasses, double percentage, boolean lowAttendance) {
        this.subjectId = subjectId;
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.totalClasses = totalClasses;
        this.presentClasses = presentClasses;
        this.absentClasses = absentClasses;
        this.percentage = percentage;
        this.lowAttendance = lowAttendance;
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

    public int getTotalClasses() {
        return totalClasses;
    }

    public void setTotalClasses(int totalClasses) {
        this.totalClasses = totalClasses;
    }

    public int getPresentClasses() {
        return presentClasses;
    }

    public void setPresentClasses(int presentClasses) {
        this.presentClasses = presentClasses;
    }

    public int getAbsentClasses() {
        return absentClasses;
    }

    public void setAbsentClasses(int absentClasses) {
        this.absentClasses = absentClasses;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public boolean isLowAttendance() {
        return lowAttendance;
    }

    public void setLowAttendance(boolean lowAttendance) {
        this.lowAttendance = lowAttendance;
    }

    public static StudentSubjectAttendanceDtoBuilder builder() {
        return new StudentSubjectAttendanceDtoBuilder();
    }

    public static class StudentSubjectAttendanceDtoBuilder {
        private Long subjectId;
        private String subjectCode;
        private String subjectName;
        private int totalClasses;
        private int presentClasses;
        private int absentClasses;
        private double percentage;
        private boolean lowAttendance;

        public StudentSubjectAttendanceDtoBuilder subjectId(Long subjectId) {
            this.subjectId = subjectId;
            return this;
        }

        public StudentSubjectAttendanceDtoBuilder subjectCode(String subjectCode) {
            this.subjectCode = subjectCode;
            return this;
        }

        public StudentSubjectAttendanceDtoBuilder subjectName(String subjectName) {
            this.subjectName = subjectName;
            return this;
        }

        public StudentSubjectAttendanceDtoBuilder totalClasses(int totalClasses) {
            this.totalClasses = totalClasses;
            return this;
        }

        public StudentSubjectAttendanceDtoBuilder presentClasses(int presentClasses) {
            this.presentClasses = presentClasses;
            return this;
        }

        public StudentSubjectAttendanceDtoBuilder absentClasses(int absentClasses) {
            this.absentClasses = absentClasses;
            return this;
        }

        public StudentSubjectAttendanceDtoBuilder percentage(double percentage) {
            this.percentage = percentage;
            return this;
        }

        public StudentSubjectAttendanceDtoBuilder lowAttendance(boolean lowAttendance) {
            this.lowAttendance = lowAttendance;
            return this;
        }

        public StudentSubjectAttendanceDto build() {
            return new StudentSubjectAttendanceDto(subjectId, subjectCode, subjectName, totalClasses, presentClasses, absentClasses, percentage, lowAttendance);
        }
    }
}
