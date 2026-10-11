package com.school.management.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "qr_checkins",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_qrc_session_student", columnNames = {"qr_session_id", "student_id"})
    }
)
public class QrCheckin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qr_session_id", nullable = false)
    private QrSession qrSession;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "checked_in_at", nullable = false)
    private LocalDateTime checkedInAt = LocalDateTime.now();

    @Column(name = "outside_geofence", nullable = false)
    private Boolean outsideGeofence = false;

    public QrCheckin() {
    }

    public QrCheckin(Long id, QrSession qrSession, Student student, LocalDateTime checkedInAt, Boolean outsideGeofence) {
        this.id = id;
        this.qrSession = qrSession;
        this.student = student;
        this.checkedInAt = checkedInAt != null ? checkedInAt : LocalDateTime.now();
        this.outsideGeofence = outsideGeofence != null ? outsideGeofence : false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public QrSession getQrSession() {
        return qrSession;
    }

    public void setQrSession(QrSession qrSession) {
        this.qrSession = qrSession;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public LocalDateTime getCheckedInAt() {
        return checkedInAt;
    }

    public void setCheckedInAt(LocalDateTime checkedInAt) {
        this.checkedInAt = checkedInAt;
    }

    public Boolean getOutsideGeofence() {
        return outsideGeofence;
    }

    public void setOutsideGeofence(Boolean outsideGeofence) {
        this.outsideGeofence = outsideGeofence != null ? outsideGeofence : false;
    }
}
