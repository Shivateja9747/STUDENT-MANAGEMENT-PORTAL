package com.school.management.service;

import com.school.management.dto.CorrectionRequestDto;
import com.school.management.entity.*;
import com.school.management.repository.AttendanceCorrectionRepository;
import com.school.management.repository.AttendanceRepository;
import com.school.management.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AttendanceCorrectionService {

    private final AttendanceCorrectionRepository correctionRepository;
    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final AuditLogService auditLogService;

    public AttendanceCorrectionService(AttendanceCorrectionRepository correctionRepository, AttendanceRepository attendanceRepository, StudentRepository studentRepository, AuditLogService auditLogService) {
        this.correctionRepository = correctionRepository;
        this.attendanceRepository = attendanceRepository;
        this.studentRepository = studentRepository;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public CorrectionRequestDto requestCorrection(String studentUsername, CorrectionRequestDto dto) {
        Student student = studentRepository.findByUserUsername(studentUsername)
                .orElseThrow(() -> new RuntimeException("Student not found for username: " + studentUsername));

        Attendance attendance = attendanceRepository.findById(dto.getAttendanceId())
                .orElseThrow(() -> new RuntimeException("Attendance record not found: " + dto.getAttendanceId()));

        if (!attendance.getStudent().getId().equals(student.getId())) {
            throw new RuntimeException("Unauthorized: Cannot dispute attendance of another student");
        }

        AttendanceCorrection correction = AttendanceCorrection.builder()
                .requestedBy(student.getUser())
                .attendance(attendance)
                .requestedStatus(dto.getRequestedStatus())
                .reason(dto.getReason().trim())
                .status(AttendanceCorrection.CorrectionStatus.PENDING)
                .build();

        AttendanceCorrection saved = correctionRepository.save(correction);
        auditLogService.log(studentUsername, Role.STUDENT, "CORRECTION_REQUEST",
                "Disputed attendance #" + attendance.getId() + " for " + attendance.getSubject().getCode() + " on " + attendance.getDate(),
                attendance.getStatus().name(), dto.getRequestedStatus().name(), null);

        return mapToDto(saved);
    }

    public List<CorrectionRequestDto> getStudentCorrections(String studentUsername) {
        return correctionRepository.findByStudentUserUsernameOrderByCreatedAtDesc(studentUsername).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<CorrectionRequestDto> getTeacherPendingCorrections(String teacherUsername) {
        return correctionRepository.findByAttendanceSubjectTeacherUserUsernameOrderByCreatedAtDesc(teacherUsername).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public CorrectionRequestDto reviewCorrection(Long correctionId, String teacherUsername, boolean approve, String comment) {
        AttendanceCorrection correction = correctionRepository.findById(correctionId)
                .orElseThrow(() -> new RuntimeException("Correction request not found: " + correctionId));

        if (correction.getStatus() != AttendanceCorrection.CorrectionStatus.PENDING) {
            throw new RuntimeException("Correction request already reviewed");
        }

        Teacher assignedTeacher = correction.getAttendance().getSubject().getTeacher();
        if (assignedTeacher != null && !assignedTeacher.getUser().getUsername().equals(teacherUsername)) {
            throw new org.springframework.security.access.AccessDeniedException("Unauthorized: You can only review correction disputes for your own subjects.");
        }

        correction.setStatus(approve ? AttendanceCorrection.CorrectionStatus.APPROVED : AttendanceCorrection.CorrectionStatus.REJECTED);
        correction.setReviewerComment(comment);

        if (approve) {
            Attendance attendance = correction.getAttendance();
            AttendanceStatus oldStatus = attendance.getStatus();
            attendance.setStatus(correction.getRequestedStatus());
            attendance.setRemarks("Corrected by teacher: " + (comment != null ? comment : "Approved dispute"));
            attendanceRepository.save(attendance);

            auditLogService.log(teacherUsername, Role.TEACHER, "CORRECTION_APPROVE",
                    "Approved attendance correction for student " + correction.getStudent().getRollNo(),
                    oldStatus.name(), correction.getRequestedStatus().name(), null);
        } else {
            auditLogService.log(teacherUsername, Role.TEACHER, "CORRECTION_REJECT",
                    "Rejected attendance correction for student " + correction.getStudent().getRollNo(),
                    correction.getAttendance().getStatus().name(), "REJECTED", null);
        }

        AttendanceCorrection updated = correctionRepository.save(correction);
        return mapToDto(updated);
    }

    private CorrectionRequestDto mapToDto(AttendanceCorrection c) {
        return CorrectionRequestDto.builder()
                .id(c.getId())
                .attendanceId(c.getAttendance().getId())
                .studentId(c.getStudent().getId())
                .studentName(c.getStudent().getName())
                .studentRollNo(c.getStudent().getRollNo())
                .subjectName(c.getAttendance().getSubject().getName())
                .attendanceDate(c.getAttendance().getDate())
                .currentStatus(c.getAttendance().getStatus())
                .requestedStatus(c.getRequestedStatus())
                .reason(c.getReason())
                .status(c.getStatus())
                .reviewerComment(c.getReviewerComment())
                .createdAt(c.getCreatedAt())
                .build();
    }
}
