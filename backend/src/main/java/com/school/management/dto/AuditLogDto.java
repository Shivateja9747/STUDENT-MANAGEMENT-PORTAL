package com.school.management.dto;

import com.school.management.entity.Role;
import java.time.LocalDateTime;

public class AuditLogDto {

    private Long id;
    private String actorUsername;
    private Role actorRole;
    private String action;
    private String details;
    private String oldValue;
    private String newValue;
    private String ipAddress;
    private LocalDateTime timestamp;

    public AuditLogDto() {
    }

    public AuditLogDto(Long id, String actorUsername, Role actorRole, String action, String details, String oldValue, String newValue, String ipAddress, LocalDateTime timestamp) {
        this.id = id;
        this.actorUsername = actorUsername;
        this.actorRole = actorRole;
        this.action = action;
        this.details = details;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.ipAddress = ipAddress;
        this.timestamp = timestamp;
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

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public static AuditLogDtoBuilder builder() {
        return new AuditLogDtoBuilder();
    }

    public static class AuditLogDtoBuilder {
        private Long id;
        private String actorUsername;
        private Role actorRole;
        private String action;
        private String details;
        private String oldValue;
        private String newValue;
        private String ipAddress;
        private LocalDateTime timestamp;

        public AuditLogDtoBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public AuditLogDtoBuilder actorUsername(String actorUsername) {
            this.actorUsername = actorUsername;
            return this;
        }

        public AuditLogDtoBuilder actorRole(Role actorRole) {
            this.actorRole = actorRole;
            return this;
        }

        public AuditLogDtoBuilder action(String action) {
            this.action = action;
            return this;
        }

        public AuditLogDtoBuilder details(String details) {
            this.details = details;
            return this;
        }

        public AuditLogDtoBuilder oldValue(String oldValue) {
            this.oldValue = oldValue;
            return this;
        }

        public AuditLogDtoBuilder newValue(String newValue) {
            this.newValue = newValue;
            return this;
        }

        public AuditLogDtoBuilder ipAddress(String ipAddress) {
            this.ipAddress = ipAddress;
            return this;
        }

        public AuditLogDtoBuilder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public AuditLogDto build() {
            return new AuditLogDto(id, actorUsername, actorRole, action, details, oldValue, newValue, ipAddress, timestamp);
        }
    }
}
