package com.school.management.service;

import com.school.management.dto.LeaveApplicationDto;
import com.school.management.dto.PagedResponse;
import com.school.management.dto.StudentResponse;
import com.school.management.entity.*;
import com.school.management.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class HodService {

    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final LeaveApplicationRepository leaveApplicationRepository;
    private final AttendanceRepository attendanceRepository;
    private final LeaveApplicationService leaveApplicationService;

    public HodService(TeacherRepository teacherRepository,
                      StudentRepository studentRepository,
                      LeaveApplicationRepository leaveApplicationRepository,
                      AttendanceRepository attendanceRepository,
                      LeaveApplicationService leaveApplicationService) {
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
        this.leaveApplicationRepository = leaveApplicationRepository;
        this.attendanceRepository = attendanceRepository;
        this.leaveApplicationService = leaveApplicationService;
    }

    public String getDepartmentForUsername(String username) {
        return teacherRepository.findByUserUsername(username)
                .map(Teacher::getDepartment)
                .orElse("CS");
    }

    public PagedResponse<StudentResponse> getDepartmentStudents(String hodUsername, int page, int size, String sortBy, String sortDir, String search, String section) {
        String dept = getDepartmentForUsername(hodUsername);
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Student> pageResult;
        if (section != null && !section.trim().isEmpty()) {
            pageResult = studentRepository.findByDepartmentAndSection(dept, section.trim(), pageable);
        } else if (search != null && !search.trim().isEmpty()) {
            String q = search.trim();
            pageResult = studentRepository.findByDepartmentAndNameContainingIgnoreCaseOrDepartmentAndRollNoContainingIgnoreCase(dept, q, dept, q, pageable);
        } else {
            pageResult = studentRepository.findByDepartment(dept, pageable);
        }

        List<StudentResponse> content = pageResult.getContent().stream()
                .map(s -> StudentResponse.builder()
                        .id(s.getId())
                        .userId(s.getUser().getId())
                        .username(s.getUser().getUsername())
                        .rollNo(s.getRollNo())
                        .name(s.getName())
                        .department(s.getDepartment())
                        .semester(s.getSemester())
                        .build())
                .collect(Collectors.toList());

        return new PagedResponse<>(content, pageResult.getNumber(), pageResult.getSize(), pageResult.getTotalElements(), pageResult.getTotalPages(), pageResult.isLast());
    }

    public PagedResponse<LeaveApplicationDto> getDepartmentLeaves(String hodUsername, int page, int size) {
        String dept = getDepartmentForUsername(hodUsername);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<LeaveApplication> pageResult = leaveApplicationRepository.findAll(pageable);
        List<LeaveApplicationDto> deptLeaves = pageResult.getContent().stream()
                .filter(l -> l.getStudent() != null && dept.equalsIgnoreCase(l.getStudent().getDepartment()))
                .map(leaveApplicationService::mapToDto)
                .collect(Collectors.toList());

        return new PagedResponse<>(deptLeaves, pageResult.getNumber(), pageResult.getSize(), deptLeaves.size(), pageResult.getTotalPages(), pageResult.isLast());
    }

    @Transactional
    public LeaveApplicationDto reviewDepartmentLeave(Long leaveId, LeaveApplication.LeaveStatus status, String reviewNote, String hodUsername) {
        String dept = getDepartmentForUsername(hodUsername);
        LeaveApplication leave = leaveApplicationRepository.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("Leave application not found with id: " + leaveId));

        if (!dept.equalsIgnoreCase(leave.getStudent().getDepartment())) {
            throw new AccessDeniedException("HOD can only review leave applications within their own department (" + dept + ").");
        }

        boolean approve = status == LeaveApplication.LeaveStatus.APPROVED;
        return leaveApplicationService.reviewLeave(leaveId, hodUsername, Role.HOD, approve, reviewNote);
    }

    public Map<String, Object> getDepartmentSummary(String hodUsername) {
        String dept = getDepartmentForUsername(hodUsername);
        List<Student> students = studentRepository.findAll().stream()
                .filter(s -> dept.equalsIgnoreCase(s.getDepartment()))
                .collect(Collectors.toList());

        int totalStudents = students.size();
        double totalPct = 0.0;
        int lowAttendanceCount = 0;

        for (Student s : students) {
            List<Attendance> attendances = attendanceRepository.findByStudentId(s.getId());
            long total = attendances.size();
            long present = attendances.stream().filter(a -> a.getStatus() == AttendanceStatus.PRESENT || a.getStatus() == AttendanceStatus.LATE).count();
            double pct = total > 0 ? ((double) present / total) * 100.0 : 100.0;
            totalPct += pct;
            if (pct < 75.0 && total > 0) {
                lowAttendanceCount++;
            }
        }

        double avgAttendance = totalStudents > 0 ? BigDecimal.valueOf(totalPct / totalStudents).setScale(2, RoundingMode.HALF_UP).doubleValue() : 100.0;

        Map<String, Object> summary = new HashMap<>();
        summary.put("department", dept);
        summary.put("totalStudents", totalStudents);
        summary.put("averageAttendance", avgAttendance);
        summary.put("lowAttendanceCount", lowAttendanceCount);
        return summary;
    }
}
