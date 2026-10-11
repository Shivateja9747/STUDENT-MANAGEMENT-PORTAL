package com.school.management.service;

import com.school.management.dto.StudentMarksDto;
import com.school.management.dto.StudentSubjectAttendanceDto;
import com.school.management.entity.ParentShareLink;
import com.school.management.entity.Student;

import com.school.management.repository.ParentShareLinkRepository;
import com.school.management.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ParentShareService {

    private final ParentShareLinkRepository shareLinkRepository;
    private final StudentRepository studentRepository;
    private final StudentService studentService;

    public ParentShareService(ParentShareLinkRepository shareLinkRepository, StudentRepository studentRepository, StudentService studentService) {
        this.shareLinkRepository = shareLinkRepository;
        this.studentRepository = studentRepository;
        this.studentService = studentService;
    }

    @Transactional
    public ParentShareLink createShareLink(String studentUsername, Integer durationDays) {
        Student student = studentRepository.findByUserUsername(studentUsername)
                .orElseThrow(() -> new RuntimeException("Student profile not found for username: " + studentUsername));

        int days = (durationDays != null && durationDays > 0) ? durationDays : 30;
        String token = UUID.randomUUID().toString().replace("-", "");

        ParentShareLink link = ParentShareLink.builder()
                .student(student)
                .token(token)
                .expiresAt(LocalDateTime.now().plusDays(days))
                .revoked(false)
                .build();

        return shareLinkRepository.save(link);
    }

    public List<ParentShareLink> getStudentShareLinks(String studentUsername) {
        return shareLinkRepository.findByStudentUserUsernameOrderByCreatedAtDesc(studentUsername);
    }

    @Transactional
    public void revokeShareLink(String studentUsername, Long linkId) {
        ParentShareLink link = shareLinkRepository.findById(linkId)
                .orElseThrow(() -> new RuntimeException("Share link not found with id: " + linkId));

        if (!link.getStudent().getUser().getUsername().equals(studentUsername)) {
            throw new RuntimeException("Unauthorized to revoke another student's share link");
        }

        link.setRevoked(true);
        shareLinkRepository.save(link);
    }

    public Map<String, Object> getPublicParentPortalData(String token) {
        ParentShareLink link = shareLinkRepository.findByTokenAndRevokedFalse(token)
                .orElseThrow(() -> new RuntimeException("Invalid or revoked parent share link."));

        if (LocalDateTime.now().isAfter(link.getExpiresAt())) {
            throw new RuntimeException("This parent share link has expired.");
        }

        Student student = link.getStudent();
        String username = student.getUser().getUsername();

        List<StudentSubjectAttendanceDto> attendance = studentService.getStudentAttendance(username);
        List<StudentMarksDto> publishedMarks = studentService.getStudentMarks(username);

        int totalClasses = attendance.stream().mapToInt(StudentSubjectAttendanceDto::getTotalClasses).sum();
        int presentClasses = attendance.stream().mapToInt(StudentSubjectAttendanceDto::getPresentClasses).sum();
        double overallAttendance = totalClasses > 0 ? Math.round((double) presentClasses / totalClasses * 1000.0) / 10.0 : 100.0;

        Map<String, Object> portalData = new HashMap<>();
        portalData.put("studentName", student.getName());
        portalData.put("rollNo", student.getRollNo());
        portalData.put("department", student.getDepartment());
        portalData.put("semester", student.getSemester());
        portalData.put("overallAttendancePercentage", overallAttendance);
        portalData.put("subjectAttendances", attendance);
        portalData.put("publishedMarks", publishedMarks);
        portalData.put("expiresAt", link.getExpiresAt());

        return portalData;
    }
}
