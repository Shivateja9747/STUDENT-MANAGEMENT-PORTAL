package com.school.management.controller;

import com.school.management.dto.AtRiskScoreDto;
import com.school.management.service.AtRiskService;
import com.school.management.service.HodService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AtRiskController {

    private final AtRiskService atRiskService;
    private final HodService hodService;

    public AtRiskController(AtRiskService atRiskService, HodService hodService) {
        this.atRiskService = atRiskService;
        this.hodService = hodService;
    }

    @GetMapping("/student/at-risk")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<AtRiskScoreDto> getMyRisk(Authentication auth) {
        return ResponseEntity.ok(atRiskService.getStudentRiskByUsername(auth.getName()));
    }

    @GetMapping("/teacher/at-risk")
    @PreAuthorize("hasAnyRole('TEACHER', 'HOD', 'ADMIN')")
    public ResponseEntity<List<AtRiskScoreDto>> getTeacherRiskPanel(
            Authentication auth,
            @RequestParam(required = false) String department) {
        String dept = department != null ? department : hodService.getDepartmentForUsername(auth.getName());
        return ResponseEntity.ok(atRiskService.getDepartmentRiskPanel(dept));
    }

    @GetMapping("/hod/at-risk")
    @PreAuthorize("hasAnyRole('HOD', 'ADMIN')")
    public ResponseEntity<List<AtRiskScoreDto>> getHodRiskPanel(Authentication auth) {
        String dept = hodService.getDepartmentForUsername(auth.getName());
        return ResponseEntity.ok(atRiskService.getDepartmentRiskPanel(dept));
    }

    @GetMapping("/admin/at-risk")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AtRiskScoreDto>> getAdminRiskPanel(
            @RequestParam(required = false) String department) {
        return ResponseEntity.ok(atRiskService.getDepartmentRiskPanel(department));
    }
}
