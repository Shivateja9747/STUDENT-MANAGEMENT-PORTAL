package com.school.management.controller;

import com.school.management.dto.LeaveApplicationDto;
import com.school.management.dto.PagedResponse;
import com.school.management.dto.StudentResponse;
import com.school.management.entity.LeaveApplication;
import com.school.management.service.HodService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/hod")
@PreAuthorize("hasRole('HOD') or hasRole('ADMIN')")
public class HodController {

    private final HodService hodService;

    public HodController(HodService hodService) {
        this.hodService = hodService;
    }

    @GetMapping("/students")
    public ResponseEntity<PagedResponse<StudentResponse>> getDepartmentStudents(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String section) {
        return ResponseEntity.ok(hodService.getDepartmentStudents(authentication.getName(), page, size, sortBy, sortDir, search, section));
    }

    @GetMapping("/leaves")
    public ResponseEntity<PagedResponse<LeaveApplicationDto>> getDepartmentLeaves(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(hodService.getDepartmentLeaves(authentication.getName(), page, size));
    }

    @PostMapping("/leaves/{id}/review")
    public ResponseEntity<LeaveApplicationDto> reviewDepartmentLeave(
            Authentication authentication,
            @PathVariable Long id,
            @RequestParam LeaveApplication.LeaveStatus status,
            @RequestParam(required = false) String reviewNote) {
        return ResponseEntity.ok(hodService.reviewDepartmentLeave(id, status, reviewNote, authentication.getName()));
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getDepartmentSummary(Authentication authentication) {
        return ResponseEntity.ok(hodService.getDepartmentSummary(authentication.getName()));
    }
}
