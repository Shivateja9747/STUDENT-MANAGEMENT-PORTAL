package com.school.management.dto;

import com.school.management.entity.AcademicCalendar;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class CalendarDto {

    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotNull(message = "Type is required")
    private AcademicCalendar.CalendarType type;

    private String description;

    public CalendarDto() {
    }

    public CalendarDto(Long id, String title, LocalDate date, AcademicCalendar.CalendarType type, String description) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.type = type;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public AcademicCalendar.CalendarType getType() {
        return type;
    }

    public void setType(AcademicCalendar.CalendarType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static CalendarDtoBuilder builder() {
        return new CalendarDtoBuilder();
    }

    public static class CalendarDtoBuilder {
        private Long id;
        private String title;
        private LocalDate date;
        private AcademicCalendar.CalendarType type;
        private String description;

        public CalendarDtoBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public CalendarDtoBuilder title(String title) {
            this.title = title;
            return this;
        }

        public CalendarDtoBuilder date(LocalDate date) {
            this.date = date;
            return this;
        }

        public CalendarDtoBuilder type(AcademicCalendar.CalendarType type) {
            this.type = type;
            return this;
        }

        public CalendarDtoBuilder description(String description) {
            this.description = description;
            return this;
        }

        public CalendarDto build() {
            return new CalendarDto(id, title, date, type, description);
        }
    }
}
