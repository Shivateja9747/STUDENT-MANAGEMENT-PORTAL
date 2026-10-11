package com.school.management.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;

public class TimetableDto {

    private Long id;

    @NotNull(message = "Subject ID is required")
    private Long subjectId;

    private String subjectCode;
    private String subjectName;

    @NotNull(message = "Teacher ID is required")
    private Long teacherId;

    private String teacherName;

    @NotNull(message = "Day of week is required")
    private DayOfWeek dayOfWeek;

    @NotNull(message = "Period is required")
    @Min(value = 1, message = "Period must be between 1 and 8")
    @Max(value = 8, message = "Period must be between 1 and 8")
    private Integer period;

    private String room;

    @NotBlank(message = "Section is required")
    private String section;

    public TimetableDto() {
    }

    public TimetableDto(Long id, Long subjectId, String subjectCode, String subjectName, Long teacherId, String teacherName, DayOfWeek dayOfWeek, Integer period, String room, String section) {
        this.id = id;
        this.subjectId = subjectId;
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.teacherId = teacherId;
        this.teacherName = teacherName;
        this.dayOfWeek = dayOfWeek;
        this.period = period;
        this.room = room;
        this.section = section;
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

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public Integer getPeriod() {
        return period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public static TimetableDtoBuilder builder() {
        return new TimetableDtoBuilder();
    }

    public static class TimetableDtoBuilder {
        private Long id;
        private Long subjectId;
        private String subjectCode;
        private String subjectName;
        private Long teacherId;
        private String teacherName;
        private DayOfWeek dayOfWeek;
        private Integer period;
        private String room;
        private String section;

        public TimetableDtoBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public TimetableDtoBuilder subjectId(Long subjectId) {
            this.subjectId = subjectId;
            return this;
        }

        public TimetableDtoBuilder subjectCode(String subjectCode) {
            this.subjectCode = subjectCode;
            return this;
        }

        public TimetableDtoBuilder subjectName(String subjectName) {
            this.subjectName = subjectName;
            return this;
        }

        public TimetableDtoBuilder teacherId(Long teacherId) {
            this.teacherId = teacherId;
            return this;
        }

        public TimetableDtoBuilder teacherName(String teacherName) {
            this.teacherName = teacherName;
            return this;
        }

        public TimetableDtoBuilder dayOfWeek(DayOfWeek dayOfWeek) {
            this.dayOfWeek = dayOfWeek;
            return this;
        }

        public TimetableDtoBuilder period(Integer period) {
            this.period = period;
            return this;
        }

        public TimetableDtoBuilder room(String room) {
            this.room = room;
            return this;
        }

        public TimetableDtoBuilder section(String section) {
            this.section = section;
            return this;
        }

        public TimetableDto build() {
            return new TimetableDto(id, subjectId, subjectCode, subjectName, teacherId, teacherName, dayOfWeek, period, room, section);
        }
    }
}
