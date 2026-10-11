package com.school.management.controller;

import com.school.management.entity.NotificationPreference;
import com.school.management.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
@PreAuthorize("isAuthenticated()")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/notification-preferences")
    public ResponseEntity<NotificationPreference> getPreferences(Authentication auth) {
        return ResponseEntity.ok(notificationService.getPreferences(auth.getName()));
    }

    @PutMapping("/notification-preferences")
    public ResponseEntity<NotificationPreference> updatePreferences(
            Authentication auth,
            @RequestBody Map<String, Object> body) {
        Boolean lowAtt = body.containsKey("lowAttendanceEmail") ? (Boolean) body.get("lowAttendanceEmail") : null;
        Boolean leaveDec = body.containsKey("leaveDecisionEmail") ? (Boolean) body.get("leaveDecisionEmail") : null;
        Boolean marksPub = body.containsKey("marksPublishedEmail") ? (Boolean) body.get("marksPublishedEmail") : null;
        String lang = (String) body.get("preferredLanguage");

        NotificationPreference updated = notificationService.updatePreferences(auth.getName(), lowAtt, leaveDec, marksPub, lang);
        return ResponseEntity.ok(updated);
    }
}
