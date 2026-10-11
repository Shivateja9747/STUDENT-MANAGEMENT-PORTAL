package com.school.management.entity;

import jakarta.persistence.*;
import java.time.DayOfWeek;

@Entity
@Table(name = "timetables")
public class Timetable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DayOfWeek dayOfWeek;

    @Column(nullable = false)
    private Integer period; // 1 to 8

    private String room;

    @Column(nullable = false)
    private String section;

    public Timetable() {
    }

    public Timetable(Long id, Subject subject, Teacher teacher, DayOfWeek dayOfWeek, Integer period, String room, String section) {
        this.id = id;
        this.subject = subject;
        this.teacher = teacher;
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

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
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

    public static TimetableBuilder builder() {
        return new TimetableBuilder();
    }

    public static class TimetableBuilder {
        private Long id;
        private Subject subject;
        private Teacher teacher;
        private DayOfWeek dayOfWeek;
        private Integer period;
        private String room;
        private String section;

        public TimetableBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public TimetableBuilder subject(Subject subject) {
            this.subject = subject;
            return this;
        }

        public TimetableBuilder teacher(Teacher teacher) {
            this.teacher = teacher;
            return this;
        }

        public TimetableBuilder dayOfWeek(DayOfWeek dayOfWeek) {
            this.dayOfWeek = dayOfWeek;
            return this;
        }

        public TimetableBuilder period(Integer period) {
            this.period = period;
            return this;
        }

        public TimetableBuilder room(String room) {
            this.room = room;
            return this;
        }

        public TimetableBuilder section(String section) {
            this.section = section;
            return this;
        }

        public Timetable build() {
            return new Timetable(id, subject, teacher, dayOfWeek, period, room, section);
        }
    }
}
