package com.school.management.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "academic_calendar")
public class AcademicCalendar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(name = "event_date", nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "event_type", nullable = false, length = 50)
    private CalendarType type; // HOLIDAY, EXAM, SEMESTER_START, SEMESTER_END

    private String description;

    public AcademicCalendar() {
    }

    public AcademicCalendar(Long id, String title, LocalDate date, CalendarType type, String description) {
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

    public CalendarType getType() {
        return type;
    }

    public void setType(CalendarType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public enum CalendarType {
        HOLIDAY,
        EXAM,
        SEMESTER_START,
        SEMESTER_END
    }

    public static AcademicCalendarBuilder builder() {
        return new AcademicCalendarBuilder();
    }

    public static class AcademicCalendarBuilder {
        private Long id;
        private String title;
        private LocalDate date;
        private CalendarType type;
        private String description;

        public AcademicCalendarBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public AcademicCalendarBuilder title(String title) {
            this.title = title;
            return this;
        }

        public AcademicCalendarBuilder date(LocalDate date) {
            this.date = date;
            return this;
        }

        public AcademicCalendarBuilder type(CalendarType type) {
            this.type = type;
            return this;
        }

        public AcademicCalendarBuilder description(String description) {
            this.description = description;
            return this;
        }

        public AcademicCalendar build() {
            return new AcademicCalendar(id, title, date, type, description);
        }
    }
}
