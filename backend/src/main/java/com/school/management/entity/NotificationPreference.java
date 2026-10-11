package com.school.management.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "notification_preferences")
public class NotificationPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private Boolean lowAttendanceEmail = true;

    @Column(nullable = false)
    private Boolean leaveDecisionEmail = true;

    @Column(nullable = false)
    private Boolean marksPublishedEmail = true;

    @Column(nullable = false)
    private String preferredLanguage = "en";

    public NotificationPreference() {
    }

    public NotificationPreference(Long id, User user, Boolean lowAttendanceEmail, Boolean leaveDecisionEmail, Boolean marksPublishedEmail, String preferredLanguage) {
        this.id = id;
        this.user = user;
        this.lowAttendanceEmail = lowAttendanceEmail != null ? lowAttendanceEmail : true;
        this.leaveDecisionEmail = leaveDecisionEmail != null ? leaveDecisionEmail : true;
        this.marksPublishedEmail = marksPublishedEmail != null ? marksPublishedEmail : true;
        this.preferredLanguage = preferredLanguage != null ? preferredLanguage : "en";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Boolean getLowAttendanceEmail() { return lowAttendanceEmail; }
    public void setLowAttendanceEmail(Boolean lowAttendanceEmail) { this.lowAttendanceEmail = lowAttendanceEmail != null ? lowAttendanceEmail : true; }

    public Boolean getLeaveDecisionEmail() { return leaveDecisionEmail; }
    public void setLeaveDecisionEmail(Boolean leaveDecisionEmail) { this.leaveDecisionEmail = leaveDecisionEmail != null ? leaveDecisionEmail : true; }

    public Boolean getMarksPublishedEmail() { return marksPublishedEmail; }
    public void setMarksPublishedEmail(Boolean marksPublishedEmail) { this.marksPublishedEmail = marksPublishedEmail != null ? marksPublishedEmail : true; }

    public String getPreferredLanguage() { return preferredLanguage; }
    public void setPreferredLanguage(String preferredLanguage) { this.preferredLanguage = preferredLanguage != null ? preferredLanguage : "en"; }

    public static NotificationPreferenceBuilder builder() { return new NotificationPreferenceBuilder(); }

    public static class NotificationPreferenceBuilder {
        private Long id;
        private User user;
        private Boolean lowAttendanceEmail = true;
        private Boolean leaveDecisionEmail = true;
        private Boolean marksPublishedEmail = true;
        private String preferredLanguage = "en";

        public NotificationPreferenceBuilder id(Long id) { this.id = id; return this; }
        public NotificationPreferenceBuilder user(User user) { this.user = user; return this; }
        public NotificationPreferenceBuilder lowAttendanceEmail(Boolean lowAttendanceEmail) { this.lowAttendanceEmail = lowAttendanceEmail; return this; }
        public NotificationPreferenceBuilder leaveDecisionEmail(Boolean leaveDecisionEmail) { this.leaveDecisionEmail = leaveDecisionEmail; return this; }
        public NotificationPreferenceBuilder marksPublishedEmail(Boolean marksPublishedEmail) { this.marksPublishedEmail = marksPublishedEmail; return this; }
        public NotificationPreferenceBuilder preferredLanguage(String preferredLanguage) { this.preferredLanguage = preferredLanguage; return this; }

        public NotificationPreference build() {
            return new NotificationPreference(id, user, lowAttendanceEmail, leaveDecisionEmail, marksPublishedEmail, preferredLanguage);
        }
    }
}
