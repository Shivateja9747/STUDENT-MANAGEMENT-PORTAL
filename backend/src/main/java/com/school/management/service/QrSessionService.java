package com.school.management.service;

import com.school.management.entity.*;
import com.school.management.repository.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class QrSessionService {

    private final QrSessionRepository qrSessionRepository;
    private final SubjectRepository subjectRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final AttendanceRepository attendanceRepository;
    private final AcademicCalendarService academicCalendarService;
    private final AuditLogService auditLogService;

    // Track used tokens to prevent token reuse
    private final Set<String> usedTokens = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public QrSessionService(QrSessionRepository qrSessionRepository, SubjectRepository subjectRepository, TeacherRepository teacherRepository, StudentRepository studentRepository, AttendanceRepository attendanceRepository, AcademicCalendarService academicCalendarService, AuditLogService auditLogService) {
        this.qrSessionRepository = qrSessionRepository;
        this.subjectRepository = subjectRepository;
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
        this.attendanceRepository = attendanceRepository;
        this.academicCalendarService = academicCalendarService;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public QrSession startSession(String teacherUsername, Long subjectId, Integer period, LocalDate date, Boolean geofenceEnabled, Double centerLat, Double centerLng, Double radiusMeters) {
        Teacher teacher = teacherRepository.findByUserUsername(teacherUsername)
                .orElseThrow(() -> new RuntimeException("Teacher not found for username: " + teacherUsername));

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found: " + subjectId));

        if (subject.getTeacher() != null && !subject.getTeacher().getId().equals(teacher.getId())) {
            throw new AccessDeniedException("Teacher can only start QR sessions for their assigned subjects.");
        }

        LocalDate sessionDate = date != null ? date : LocalDate.now();

        if (academicCalendarService.isHoliday(sessionDate)) {
            throw new RuntimeException("Cannot open QR check-in session on an official holiday.");
        }

        // Close existing open session if any
        qrSessionRepository.findBySubjectIdAndDateAndOpenTrue(subjectId, sessionDate)
                .ifPresent(s -> {
                    s.setOpen(false);
                    qrSessionRepository.save(s);
                });

        String secretKey = UUID.randomUUID().toString() + UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();

        QrSession session = QrSession.builder()
                .teacher(teacher)
                .subject(subject)
                .period(period != null ? period : 1)
                .date(sessionDate)
                .secretKey(secretKey)
                .expiresAt(now.plusMinutes(90))
                .open(true)
                .geofenceEnabled(Boolean.TRUE.equals(geofenceEnabled))
                .centerLat(centerLat)
                .centerLng(centerLng)
                .radiusMeters(radiusMeters != null ? radiusMeters : 100.0)
                .build();

        QrSession saved = qrSessionRepository.save(session);
        auditLogService.log(teacherUsername, Role.TEACHER, "QR_SESSION_START",
                "Started QR session #" + saved.getId() + " for subject " + subject.getCode() + " Period " + saved.getPeriod(),
                null, "OPEN", null);

        return saved;
    }

    public String generateRotatingToken(Long sessionId, String teacherUsername) {
        QrSession session = qrSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found: " + sessionId));

        if (!Boolean.TRUE.equals(session.getOpen())) {
            throw new RuntimeException("Session is closed");
        }

        long now = System.currentTimeMillis();
        long exp = now + (30 * 1000); // 30 seconds valid window

        Key key = Keys.hmacShaKeyFor(session.getSecretKey().getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .setSubject(String.valueOf(sessionId))
                .claim("period", session.getPeriod())
                .claim("nonce", UUID.randomUUID().toString())
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(exp))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    @Transactional
    public Attendance processCheckin(String studentUsername, String token, Double lat, Double lng) {
        if (usedTokens.contains(token)) {
            throw new RuntimeException("QR Token already used / reused token rejected.");
        }

        Student student = studentRepository.findByUserUsername(studentUsername)
                .orElseThrow(() -> new RuntimeException("Student not found for username: " + studentUsername));

        // Find session by parsing claims
        Long sessionId;
        QrSession session;
        try {
            String[] parts = token.split("\\.");
            String unparsedHeaderBody = parts[0] + "." + parts[1];
            // Decode claims without verifying yet to get sessionId
            String bodyJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            String subStr = bodyJson.substring(bodyJson.indexOf("\"sub\":\"") + 7);
            subStr = subStr.substring(0, subStr.indexOf("\""));
            sessionId = Long.parseLong(subStr);

            session = qrSessionRepository.findById(sessionId)
                    .orElseThrow(() -> new RuntimeException("Session not found"));

            Key key = Keys.hmacShaKeyFor(session.getSecretKey().getBytes(StandardCharsets.UTF_8));
            Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        } catch (Exception e) {
            throw new RuntimeException("Invalid or expired QR token (30-second token window expired).");
        }

        if (!Boolean.TRUE.equals(session.getOpen())) {
            throw new RuntimeException("Session is closed by teacher.");
        }

        // Enrollment check: student semester must match subject semester
        if (!student.getSemester().equals(session.getSubject().getSemester())) {
            throw new AccessDeniedException("Non-enrolled student: Student semester does not match subject semester.");
        }

        // Duplicate check-in verification
        Optional<Attendance> existing = attendanceRepository.findByStudentIdAndSubjectIdAndDateAndPeriod(
                student.getId(), session.getSubject().getId(), session.getDate(), session.getPeriod());

        if (existing.isPresent() && existing.get().getStatus() == AttendanceStatus.PRESENT) {
            throw new RuntimeException("Duplicate check-in rejected: Student is already marked PRESENT for this period.");
        }

        String remark = "QR Check-in";
        if (Boolean.TRUE.equals(session.getGeofenceEnabled())) {
            if (lat == null || lng == null || session.getCenterLat() == null || session.getCenterLng() == null) {
                remark = "GEOFENCE_WARNING: Location permissions missing";
            } else {
                double distanceMeters = calculateHaversineDistance(session.getCenterLat(), session.getCenterLng(), lat, lng);
                if (distanceMeters > session.getRadiusMeters()) {
                    remark = "GEOFENCE_WARNING: Out of range (" + Math.round(distanceMeters) + "m > " + Math.round(session.getRadiusMeters()) + "m)";
                }
            }
        }

        usedTokens.add(token);

        Attendance attendance = existing.orElse(Attendance.builder()
                .student(student)
                .subject(session.getSubject())
                .date(session.getDate())
                .period(session.getPeriod())
                .build());

        attendance.setStatus(AttendanceStatus.PRESENT);
        attendance.setRemarks(remark);

        Attendance saved = attendanceRepository.save(attendance);
        auditLogService.log(studentUsername, Role.STUDENT, "QR_CHECKIN",
                "Checked in via QR for subject " + session.getSubject().getCode() + " Period " + session.getPeriod(),
                null, "PRESENT", null);

        return saved;
    }

    @Transactional
    public QrSession closeSession(Long sessionId, String teacherUsername) {
        QrSession session = qrSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found: " + sessionId));

        session.setOpen(false);
        QrSession updated = qrSessionRepository.save(session);
        auditLogService.log(teacherUsername, Role.TEACHER, "QR_SESSION_CLOSE",
                "Closed QR session #" + sessionId, "OPEN", "CLOSED", null);

        return updated;
    }

    public List<Attendance> getSessionAttendances(Long sessionId, String teacherUsername) {
        QrSession session = qrSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found: " + sessionId));

        return attendanceRepository.findBySubjectIdAndDate(session.getSubject().getId(), session.getDate());
    }

    private double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371000; // Radius of Earth in meters
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
