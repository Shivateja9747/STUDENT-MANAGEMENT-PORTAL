package com.school.management.controller;

import com.school.management.dto.CorrectionRequestDto;
import com.school.management.service.AttendanceCorrectionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AttendanceCorrectionController {

    private final AttendanceCorrectionService correctionService;

    public AttendanceCorrectionController(AttendanceCorrectionService correctionService) {
        this.correctionService = correctionService;
    }

    @PostMapping("/student/corrections")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<CorrectionRequestDto> requestCorrection(@Valid @RequestBody CorrectionRequestDto dto, Authentication auth) {
        return new ResponseEntity<>(correctionService.requestCorrection(auth.getName(), dto), HttpStatus.CREATED);
    }

    @GetMapping("/student/corrections")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<CorrectionRequestDto>> getMyCorrections(Authentication auth) {
        return ResponseEntity.ok(correctionService.getStudentCorrections(auth.getName()));
    }

    @GetMapping("/teacher/corrections/pending")
    @PreAuthorize("hasAnyRole('TEACHER', 'HOD', 'ADMIN')")
    public ResponseEntity<List<CorrectionRequestDto>> getTeacherCorrections(Authentication auth) {
        return ResponseEntity.ok(correctionService.getTeacherPendingCorrections(auth.getName()));
    }

    @PostMapping("/teacher/corrections/{id}/review")
    @PreAuthorize("hasAnyRole('TEACHER', 'HOD', 'ADMIN')")
    public ResponseEntity<CorrectionRequestDto> reviewCorrection(@PathVariable Long id, @RequestBody Map<String, Object> body, Authentication auth) {
        boolean approve = Boolean.TRUE.equals(body.get("approve"));
        String comment = (String) body.get("comment");
        return ResponseEntity.ok(correctionService.reviewCorrection(id, auth.getName(), approve, comment));
    }
}
