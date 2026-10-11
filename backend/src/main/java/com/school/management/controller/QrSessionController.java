package com.school.management.controller;

import com.school.management.entity.Attendance;
import com.school.management.entity.QrSession;
import com.school.management.service.QrSessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class QrSessionController {

    private final QrSessionService qrSessionService;

    public QrSessionController(QrSessionService qrSessionService) {
        this.qrSessionService = qrSessionService;
    }

    @PostMapping("/teacher/qr-session/start")
    @PreAuthorize("hasAnyRole('TEACHER', 'HOD', 'ADMIN')")
    public ResponseEntity<QrSession> startSession(
            Authentication auth,
            @RequestBody Map<String, Object> body) {
        Long subjectId = Long.parseLong(body.get("subjectId").toString());
        Integer period = body.get("period") != null ? Integer.parseInt(body.get("period").toString()) : 1;
        Boolean geofence = Boolean.TRUE.equals(body.get("geofenceEnabled"));
        Double lat = body.get("centerLat") != null ? Double.parseDouble(body.get("centerLat").toString()) : null;
        Double lng = body.get("centerLng") != null ? Double.parseDouble(body.get("centerLng").toString()) : null;
        Double radius = body.get("radiusMeters") != null ? Double.parseDouble(body.get("radiusMeters").toString()) : 100.0;

        QrSession session = qrSessionService.startSession(auth.getName(), subjectId, period, LocalDate.now(), geofence, lat, lng, radius);
        return ResponseEntity.ok(session);
    }

    @GetMapping("/teacher/qr-session/{id}/token")
    @PreAuthorize("hasAnyRole('TEACHER', 'HOD', 'ADMIN')")
    public ResponseEntity<Map<String, String>> getToken(@PathVariable Long id, Authentication auth) {
        String token = qrSessionService.generateRotatingToken(id, auth.getName());
        return ResponseEntity.ok(Map.of("token", token, "expiresInSeconds", "30"));
    }

    @PostMapping("/teacher/qr-session/{id}/close")
    @PreAuthorize("hasAnyRole('TEACHER', 'HOD', 'ADMIN')")
    public ResponseEntity<QrSession> closeSession(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(qrSessionService.closeSession(id, auth.getName()));
    }

    @GetMapping("/teacher/qr-session/{id}/attendances")
    @PreAuthorize("hasAnyRole('TEACHER', 'HOD', 'ADMIN')")
    public ResponseEntity<List<Attendance>> getSessionAttendances(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(qrSessionService.getSessionAttendances(id, auth.getName()));
    }

    @PostMapping("/student/checkin")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Map<String, Object>> processCheckin(
            Authentication auth,
            @RequestBody Map<String, Object> body) {
        String token = (String) body.get("token");
        Double lat = body.get("latitude") != null ? Double.parseDouble(body.get("latitude").toString()) : null;
        Double lng = body.get("longitude") != null ? Double.parseDouble(body.get("longitude").toString()) : null;

        Attendance attendance = qrSessionService.processCheckin(auth.getName(), token, lat, lng);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Successfully checked in as PRESENT",
                "attendanceId", attendance.getId(),
                "remarks", attendance.getRemarks() != null ? attendance.getRemarks() : ""
        ));
    }
}
