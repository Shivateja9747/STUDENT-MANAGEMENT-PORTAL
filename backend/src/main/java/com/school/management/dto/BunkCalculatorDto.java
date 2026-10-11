package com.school.management.dto;

public class BunkCalculatorDto {

    private Long subjectId;
    private String subjectCode;
    private String subjectName;
    private int totalClasses;
    private int attendedClasses;
    private double currentPercentage;
    private double targetPercentage;
    private int classesCanMiss;
    private int classesToAttend;
    private String statusMessage;

    public BunkCalculatorDto() {
    }

    public BunkCalculatorDto(Long subjectId, String subjectCode, String subjectName, int totalClasses, int attendedClasses, double currentPercentage, double targetPercentage, int classesCanMiss, int classesToAttend, String statusMessage) {
        this.subjectId = subjectId;
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.totalClasses = totalClasses;
        this.attendedClasses = attendedClasses;
        this.currentPercentage = currentPercentage;
        this.targetPercentage = targetPercentage;
        this.classesCanMiss = classesCanMiss;
        this.classesToAttend = classesToAttend;
        this.statusMessage = statusMessage;
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

    public int getAttendedClasses() {
        return attendedClasses;
    }

    public void setAttendedClasses(int attendedClasses) {
        this.attendedClasses = attendedClasses;
    }

    public double getCurrentPercentage() {
        return currentPercentage;
    }

    public void setCurrentPercentage(double currentPercentage) {
        this.currentPercentage = currentPercentage;
    }

    public double getTargetPercentage() {
        return targetPercentage;
    }

    public void setTargetPercentage(double targetPercentage) {
        this.targetPercentage = targetPercentage;
    }

    public int getClassesCanMiss() {
        return classesCanMiss;
    }

    public void setClassesCanMiss(int classesCanMiss) {
        this.classesCanMiss = classesCanMiss;
    }

    public int getClassesToAttend() {
        return classesToAttend;
    }

    public void setClassesToAttend(int classesToAttend) {
        this.classesToAttend = classesToAttend;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public static BunkCalculatorDtoBuilder builder() {
        return new BunkCalculatorDtoBuilder();
    }

    public static class BunkCalculatorDtoBuilder {
        private Long subjectId;
        private String subjectCode;
        private String subjectName;
        private int totalClasses;
        private int attendedClasses;
        private double currentPercentage;
        private double targetPercentage;
        private int classesCanMiss;
        private int classesToAttend;
        private String statusMessage;

        public BunkCalculatorDtoBuilder subjectId(Long subjectId) {
            this.subjectId = subjectId;
            return this;
        }

        public BunkCalculatorDtoBuilder subjectCode(String subjectCode) {
            this.subjectCode = subjectCode;
            return this;
        }

        public BunkCalculatorDtoBuilder subjectName(String subjectName) {
            this.subjectName = subjectName;
            return this;
        }

        public BunkCalculatorDtoBuilder totalClasses(int totalClasses) {
            this.totalClasses = totalClasses;
            return this;
        }

        public BunkCalculatorDtoBuilder attendedClasses(int attendedClasses) {
            this.attendedClasses = attendedClasses;
            return this;
        }

        public BunkCalculatorDtoBuilder currentPercentage(double currentPercentage) {
            this.currentPercentage = currentPercentage;
            return this;
        }

        public BunkCalculatorDtoBuilder targetPercentage(double targetPercentage) {
            this.targetPercentage = targetPercentage;
            return this;
        }

        public BunkCalculatorDtoBuilder classesCanMiss(int classesCanMiss) {
            this.classesCanMiss = classesCanMiss;
            return this;
        }

        public BunkCalculatorDtoBuilder classesToAttend(int classesToAttend) {
            this.classesToAttend = classesToAttend;
            return this;
        }

        public BunkCalculatorDtoBuilder statusMessage(String statusMessage) {
            this.statusMessage = statusMessage;
            return this;
        }

        public BunkCalculatorDto build() {
            return new BunkCalculatorDto(subjectId, subjectCode, subjectName, totalClasses, attendedClasses, currentPercentage, targetPercentage, classesCanMiss, classesToAttend, statusMessage);
        }
    }
}
