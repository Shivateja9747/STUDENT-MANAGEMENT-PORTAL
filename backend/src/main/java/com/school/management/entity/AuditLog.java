package com.school.management.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String actorUsername;

    @Enumerated(EnumType.STRING)
    private Role actorRole;

    @Column(nullable = false)
    private String action;

    @Column(length = 1000)
    private String details;

    @Column(length = 1000)
    private String oldValue;

    @Column(length = 1000)
    private String newValue;

    private String ipAddress;

    @Column(name = "occurred_at", nullable = false)
    private LocalDateTime occurredAt;

    public AuditLog() {
    }

    public AuditLog(Long id, String actorUsername, Role actorRole, String action, String details, String oldValue, String newValue, String ipAddress, LocalDateTime occurredAt) {
        this.id = id;
        this.actorUsername = actorUsername;
        this.actorRole = actorRole;
        this.action = action;
        this.details = details;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.ipAddress = ipAddress;
        this.occurredAt = occurredAt != null ? occurredAt : LocalDateTime.now();
    }

    @PrePersist
    public void onCreate() {
        if (occurredAt == null) {
            occurredAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getActorUsername() {
        return actorUsername;
    }

    public void setActorUsername(String actorUsername) {
        this.actorUsername = actorUsername;
    }

    public Role getActorRole() {
        return actorRole;
    }

    public void setActorRole(Role actorRole) {
        this.actorRole = actorRole;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(LocalDateTime occurredAt) {
        this.occurredAt = occurredAt;
    }

    public static AuditLogBuilder builder() {
        return new AuditLogBuilder();
    }

    public static class AuditLogBuilder {
        private Long id;
        private String actorUsername;
        private Role actorRole;
        private String action;
        private String details;
        private String oldValue;
        private String newValue;
        private String ipAddress;
        private LocalDateTime occurredAt;

        public AuditLogBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public AuditLogBuilder actorUsername(String actorUsername) {
            this.actorUsername = actorUsername;
            return this;
        }

        public AuditLogBuilder actorRole(Role actorRole) {
            this.actorRole = actorRole;
            return this;
        }

        public AuditLogBuilder action(String action) {
            this.action = action;
            return this;
        }

        public AuditLogBuilder details(String details) {
            this.details = details;
            return this;
        }

        public AuditLogBuilder oldValue(String oldValue) {
            this.oldValue = oldValue;
            return this;
        }

        public AuditLogBuilder newValue(String newValue) {
            this.newValue = newValue;
            return this;
        }

        public AuditLogBuilder ipAddress(String ipAddress) {
            this.ipAddress = ipAddress;
            return this;
        }

        public AuditLogBuilder occurredAt(LocalDateTime occurredAt) {
            this.occurredAt = occurredAt;
            return this;
        }

        public AuditLog build() {
            return new AuditLog(id, actorUsername, actorRole, action, details, oldValue, newValue, ipAddress, occurredAt);
        }
    }
}
