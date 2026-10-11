package com.school.management.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "qr_sessions")
public class QrSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(nullable = false, length = 10)
    private String section = "A";

    @Column(nullable = false, unique = true, length = 100)
    private String token;

    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt = LocalDateTime.now();

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "geofence_lat")
    private Double geofenceLat;

    @Column(name = "geofence_lng")
    private Double geofenceLng;

    @Column(name = "geofence_radius")
    private Double geofenceRadius = 100.0;

    public QrSession() {
    }

    public QrSession(Long id, Teacher teacher, Subject subject, String section, String token, LocalDateTime generatedAt, LocalDateTime expiresAt, LocalDateTime closedAt, Boolean isActive, Double geofenceLat, Double geofenceLng, Double geofenceRadius) {
        this.id = id;
        this.teacher = teacher;
        this.subject = subject;
        this.section = section != null ? section : "A";
        this.token = token;
        this.generatedAt = generatedAt != null ? generatedAt : LocalDateTime.now();
        this.expiresAt = expiresAt;
        this.closedAt = closedAt;
        this.isActive = isActive != null ? isActive : true;
        this.geofenceLat = geofenceLat;
        this.geofenceLng = geofenceLng;
        this.geofenceRadius = geofenceRadius != null ? geofenceRadius : 100.0;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Teacher getTeacher() { return teacher; }
    public void setTeacher(Teacher teacher) { this.teacher = teacher; }

    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    public String getToken() { return token != null ? token : getSecretKey(); }
    public void setToken(String token) { this.token = token; }

    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public LocalDateTime getClosedAt() { return closedAt; }
    public void setClosedAt(LocalDateTime closedAt) { this.closedAt = closedAt; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive != null ? isActive : true; }

    public Double getGeofenceLat() { return geofenceLat; }
    public void setGeofenceLat(Double geofenceLat) { this.geofenceLat = geofenceLat; }

    public Double getGeofenceLng() { return geofenceLng; }
    public void setGeofenceLng(Double geofenceLng) { this.geofenceLng = geofenceLng; }

    public Double getGeofenceRadius() { return geofenceRadius; }
    public void setGeofenceRadius(Double geofenceRadius) { this.geofenceRadius = geofenceRadius; }

    // Backward compatibility helper methods
    public Integer getPeriod() { return 1; }
    public LocalDate getDate() { return generatedAt != null ? generatedAt.toLocalDate() : LocalDate.now(); }
    public Boolean getOpen() { return isActive; }
    public void setOpen(Boolean open) { this.isActive = open != null ? open : true; }
    public String getSecretKey() { return token; }
    public void setSecretKey(String secretKey) { this.token = secretKey; }
    public Boolean getGeofenceEnabled() { return geofenceLat != null && geofenceLng != null; }
    public Double getCenterLat() { return geofenceLat; }
    public Double getCenterLng() { return geofenceLng; }
    public Double getRadiusMeters() { return geofenceRadius; }

    public static QrSessionBuilder builder() { return new QrSessionBuilder(); }

    public static class QrSessionBuilder {
        private Long id;
        private Teacher teacher;
        private Subject subject;
        private String section = "A";
        private String token;
        private LocalDateTime generatedAt = LocalDateTime.now();
        private LocalDateTime expiresAt;
        private LocalDateTime closedAt;
        private Boolean isActive = true;
        private Double geofenceLat;
        private Double geofenceLng;
        private Double geofenceRadius = 100.0;

        public QrSessionBuilder id(Long id) { this.id = id; return this; }
        public QrSessionBuilder teacher(Teacher teacher) { this.teacher = teacher; return this; }
        public QrSessionBuilder subject(Subject subject) { this.subject = subject; return this; }
        public QrSessionBuilder section(String section) { this.section = section; return this; }
        public QrSessionBuilder token(String token) { this.token = token; return this; }
        public QrSessionBuilder generatedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; return this; }
        public QrSessionBuilder expiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; return this; }
        public QrSessionBuilder closedAt(LocalDateTime closedAt) { this.closedAt = closedAt; return this; }
        public QrSessionBuilder isActive(Boolean isActive) { this.isActive = isActive; return this; }
        public QrSessionBuilder geofenceLat(Double geofenceLat) { this.geofenceLat = geofenceLat; return this; }
        public QrSessionBuilder geofenceLng(Double geofenceLng) { this.geofenceLng = geofenceLng; return this; }
        public QrSessionBuilder geofenceRadius(Double geofenceRadius) { this.geofenceRadius = geofenceRadius; return this; }
        public QrSessionBuilder geofenceEnabled(Boolean geofenceEnabled) { return this; }
        public QrSessionBuilder centerLat(Double centerLat) { this.geofenceLat = centerLat; return this; }
        public QrSessionBuilder centerLng(Double centerLng) { this.geofenceLng = centerLng; return this; }
        public QrSessionBuilder radiusMeters(Double radiusMeters) { this.geofenceRadius = radiusMeters; return this; }
        public QrSessionBuilder open(Boolean open) { this.isActive = open; return this; }
        public QrSessionBuilder secretKey(String secretKey) { this.token = secretKey; return this; }
        public QrSessionBuilder period(Integer period) { return this; }
        public QrSessionBuilder date(LocalDate date) { return this; }

        public QrSession build() {
            return new QrSession(id, teacher, subject, section, token, generatedAt, expiresAt, closedAt, isActive, geofenceLat, geofenceLng, geofenceRadius);
        }
    }
}
