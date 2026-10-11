package com.school.management.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(
    name = "attendance_records",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_att_student_subj_date_period", columnNames = {"student_id", "subject_id", "attendance_date", "period"})
    }
)
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;

    @Column(nullable = false)
    private Integer period = 1;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttendanceStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marked_by")
    private User markedBy;

    @Column(nullable = false)
    private String source = "MANUAL";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qr_session_id")
    private QrSession qrSession;

    @Column(length = 255)
    private String remark;

    public Attendance() {
    }

    public Attendance(Long id, Student student, Subject subject, LocalDate attendanceDate, Integer period, AttendanceStatus status, User markedBy, String source, QrSession qrSession, String remark) {
        this.id = id;
        this.student = student;
        this.subject = subject;
        this.attendanceDate = attendanceDate;
        this.period = period != null ? period : 1;
        this.status = status;
        this.markedBy = markedBy;
        this.source = source != null ? source : "MANUAL";
        this.qrSession = qrSession;
        this.remark = remark;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public LocalDate getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(LocalDate attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public LocalDate getDate() {
        return attendanceDate;
    }

    public void setDate(LocalDate date) {
        this.attendanceDate = date;
    }

    public Integer getPeriod() {
        return period;
    }

    public void setPeriod(Integer period) {
        this.period = period != null ? period : 1;
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    public void setStatus(AttendanceStatus status) {
        this.status = status;
    }

    public User getMarkedBy() {
        return markedBy;
    }

    public void setMarkedBy(User markedBy) {
        this.markedBy = markedBy;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source != null ? source : "MANUAL";
    }

    public QrSession getQrSession() {
        return qrSession;
    }

    public void setQrSession(QrSession qrSession) {
        this.qrSession = qrSession;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRemarks() {
        return remark;
    }

    public void setRemarks(String remarks) {
        this.remark = remarks;
    }

    public static AttendanceBuilder builder() {
        return new AttendanceBuilder();
    }

    public static class AttendanceBuilder {
        private Long id;
        private Student student;
        private Subject subject;
        private LocalDate attendanceDate;
        private Integer period = 1;
        private AttendanceStatus status;
        private User markedBy;
        private String source = "MANUAL";
        private QrSession qrSession;
        private String remark;

        public AttendanceBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public AttendanceBuilder student(Student student) {
            this.student = student;
            return this;
        }

        public AttendanceBuilder subject(Subject subject) {
            this.subject = subject;
            return this;
        }

        public AttendanceBuilder attendanceDate(LocalDate attendanceDate) {
            this.attendanceDate = attendanceDate;
            return this;
        }

        public AttendanceBuilder date(LocalDate date) {
            this.attendanceDate = date;
            return this;
        }

        public AttendanceBuilder period(Integer period) {
            this.period = period != null ? period : 1;
            return this;
        }

        public AttendanceBuilder status(AttendanceStatus status) {
            this.status = status;
            return this;
        }

        public AttendanceBuilder markedBy(User markedBy) {
            this.markedBy = markedBy;
            return this;
        }

        public AttendanceBuilder source(String source) {
            this.source = source != null ? source : "MANUAL";
            return this;
        }

        public AttendanceBuilder qrSession(QrSession qrSession) {
            this.qrSession = qrSession;
            return this;
        }

        public AttendanceBuilder remark(String remark) {
            this.remark = remark;
            return this;
        }

        public Attendance build() {
            return new Attendance(id, student, subject, attendanceDate, period, status, markedBy, source, qrSession, remark);
        }
    }
}
