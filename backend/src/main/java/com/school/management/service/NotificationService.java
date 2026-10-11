package com.school.management.service;

import com.school.management.entity.NotificationPreference;
import com.school.management.entity.User;
import com.school.management.repository.NotificationPreferenceRepository;
import com.school.management.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationPreferenceRepository preferenceRepository;
    private final UserRepository userRepository;

    @Autowired(required = false)
    private JavaMailSender mailSender;

    public NotificationService(NotificationPreferenceRepository preferenceRepository, UserRepository userRepository) {
        this.preferenceRepository = preferenceRepository;
        this.userRepository = userRepository;
    }

    public NotificationPreference getPreferences(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        return preferenceRepository.findByUserId(user.getId())
                .orElseGet(() -> preferenceRepository.save(
                        NotificationPreference.builder()
                                .user(user)
                                .lowAttendanceEmail(false) // Default OFF per requirements
                                .leaveDecisionEmail(false)
                                .marksPublishedEmail(false)
                                .preferredLanguage("en")
                                .build()
                ));
    }

    @Transactional
    public NotificationPreference updatePreferences(String username, Boolean lowAttendance, Boolean leaveDecision, Boolean marksPublished, String language) {
        NotificationPreference pref = getPreferences(username);

        if (lowAttendance != null) pref.setLowAttendanceEmail(lowAttendance);
        if (leaveDecision != null) pref.setLeaveDecisionEmail(leaveDecision);
        if (marksPublished != null) pref.setMarksPublishedEmail(marksPublished);
        if (language != null) pref.setPreferredLanguage(language);

        return preferenceRepository.save(pref);
    }

    @Async
    public void sendEmailAsync(String toAddress, String subject, String bodyText) {
        if (mailSender == null) {
            log.info("Spring Mail Sender not configured. Skipping email to {}: {}", toAddress, subject);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toAddress);
            message.setSubject(subject);
            message.setText(bodyText);
            mailSender.send(message);
            log.info("Async email successfully sent to {}", toAddress);
        } catch (Exception e) {
            log.warn("Failed to send async email to {}: {}", toAddress, e.getMessage());
        }
    }
}
