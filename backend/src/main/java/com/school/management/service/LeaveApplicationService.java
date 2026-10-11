package com.school.management.service;

import com.school.management.dto.LeaveApplicationDto;
import com.school.management.entity.*;
import com.school.management.repository.AttendanceRepository;
import com.school.management.repository.LeaveApplicationRepository;
import com.school.management.repository.StudentRepository;
import com.school.management.repository.SubjectRepository;
import com.school.management.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeaveApplicationService {

    private final LeaveApplicationRepository leaveRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;
    private final AcademicCalendarService academicCalendarService;

    public LeaveApplicationService(LeaveApplicationRepository leaveRepository, StudentRepository studentRepository, SubjectRepository subjectRepository, AttendanceRepository attendanceRepository, UserRepository userRepository, AuditLogService auditLogService, AcademicCalendarService academicCalendarService) {
        this.leaveRepository = leaveRepository;
        this.studentRepository = studentRepository;
        this.subjectRepository = subjectRepository;
        this.attendanceRepository = attendanceRepository;
        this.userRepository = userRepository;
        this.auditLogService = auditLogService;
        this.academicCalendarService = academicCalendarService;
    }

    @Transactional
    public LeaveApplicationDto applyLeave(String studentUsername, LeaveApplicationDto dto) {
        Student student = studentRepository.findByUserUsername(studentUsername)
                .orElseThrow(() -> new RuntimeException("Student not found for username: " + studentUsername));

        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new RuntimeException("End date cannot be before start date");
        }

        LeaveApplication application = LeaveApplication.builder()
                .student(student)
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .reason(dto.getReason().trim())
                .status(LeaveApplication.LeaveStatus.PENDING)
                .build();

        LeaveApplication saved = leaveRepository.save(application);
        auditLogService.log(studentUsername, Role.STUDENT, "LEAVE_APPLY",
                "Applied leave from " + saved.getStartDate() + " to " + saved.getEndDate(), null, "PENDING", null);

        return mapToDto(saved);
    }

    public List<LeaveApplicationDto> getStudentLeaves(String studentUsername) {
        return leaveRepository.findByStudentUserUsernameOrderByCreatedAtDesc(studentUsername).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<LeaveApplicationDto> getDepartmentLeaves(String department) {
        return leaveRepository.findByStudentDepartmentOrderByCreatedAtDesc(department).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<LeaveApplicationDto> getPendingLeaves() {
        return leaveRepository.findByStatusOrderByCreatedAtDesc(LeaveApplication.LeaveStatus.PENDING).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public LeaveApplicationDto reviewLeave(Long leaveId, String reviewerUsername, Role reviewerRole, boolean approve, String comment) {
        LeaveApplication app = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("Leave application not found: " + leaveId));

        if (app.getStatus() != LeaveApplication.LeaveStatus.PENDING) {
            throw new RuntimeException("Leave application already reviewed");
        }

        User reviewer = userRepository.findByUsername(reviewerUsername).orElse(null);

        app.setStatus(approve ? LeaveApplication.LeaveStatus.APPROVED : LeaveApplication.LeaveStatus.REJECTED);
        app.setReviewerComment(comment);
        app.setReviewedBy(reviewer);
        app.setReviewedAt(LocalDateTime.now());

        LeaveApplication updated = leaveRepository.save(app);

        // If approved, auto-mark LEAVE in Attendance table for all student subjects for covered working dates
        if (approve) {
            List<Subject> subjects = subjectRepository.findBySemester(app.getStudent().getSemester());
            LocalDate current = app.getStartDate();
            while (!current.isAfter(app.getEndDate())) {
                boolean isWeekend = (current.getDayOfWeek() == DayOfWeek.SATURDAY || current.getDayOfWeek() == DayOfWeek.SUNDAY);
                boolean isHoliday = academicCalendarService.isHoliday(current);

                if (!isWeekend && !isHoliday) {
                    for (Subject subject : subjects) {
                        Attendance attendance = attendanceRepository
                                .findByStudentIdAndSubjectIdAndDate(app.getStudent().getId(), subject.getId(), current)
                                .orElse(Attendance.builder()
                                        .student(app.getStudent())
                                        .subject(subject)
                                        .date(current)
                                        .period(1)
                                        .build());

                        if (attendance.getStatus() == AttendanceStatus.PRESENT) {
                            attendance.setRemarks("Approved Leave Conflict: Prior Present record preserved.");
                        } else {
                            attendance.setStatus(AttendanceStatus.LEAVE);
                            attendance.setRemarks("Approved Leave: " + app.getReason());
                        }
                        attendanceRepository.save(attendance);
                    }
                }
                current = current.plusDays(1);
            }
        }

        auditLogService.log(reviewerUsername, reviewerRole, "LEAVE_REVIEW",
                "Reviewed leave #" + leaveId + ": " + updated.getStatus(), "PENDING", updated.getStatus().name(), null);

        return mapToDto(updated);
    }

    public LeaveApplicationDto mapToDto(LeaveApplication app) {
        return LeaveApplicationDto.builder()
                .id(app.getId())
                .studentId(app.getStudent().getId())
                .studentName(app.getStudent().getName())
                .studentRollNo(app.getStudent().getRollNo())
                .studentDepartment(app.getStudent().getDepartment())
                .startDate(app.getStartDate())
                .endDate(app.getEndDate())
                .reason(app.getReason())
                .status(app.getStatus())
                .reviewerComment(app.getReviewerComment())
                .reviewedBy(app.getReviewedBy() != null ? app.getReviewedBy().getUsername() : null)
                .createdAt(app.getCreatedAt())
                .build();
    }
}
