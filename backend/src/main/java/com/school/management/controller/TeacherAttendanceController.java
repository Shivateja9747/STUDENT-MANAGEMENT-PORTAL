package com.school.management.controller;

import com.school.management.dto.*;
import com.school.management.service.TeacherAttendanceService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/teacher")
@PreAuthorize("hasRole('TEACHER')")
public class TeacherAttendanceController {

    private final TeacherAttendanceService attendanceService;

    public TeacherAttendanceController(TeacherAttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @GetMapping("/subjects")
    public ResponseEntity<List<SubjectResponse>> getTeacherSubjects(Authentication authentication) {
        return ResponseEntity.ok(attendanceService.getTeacherSubjects(authentication.getName()));
    }

    @GetMapping("/subjects/{id}/students")
    public ResponseEntity<List<StudentResponse>> getStudentsForSubject(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(attendanceService.getStudentsForTeacherSubject(id, authentication.getName()));
    }

    @PostMapping("/attendance")
    public ResponseEntity<List<AttendanceResponse>> saveAttendance(
            @Valid @RequestBody BulkAttendanceRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(attendanceService.saveBulkAttendance(request, authentication.getName()));
    }

    @GetMapping("/attendance")
    public ResponseEntity<List<AttendanceResponse>> getAttendance(
            @RequestParam Long subjectId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false, defaultValue = "1") Integer period,
            Authentication authentication) {
        return ResponseEntity.ok(attendanceService.getAttendanceBySubjectDateAndPeriod(subjectId, date, period, authentication.getName()));
    }

    @GetMapping("/attendance/history")
    public ResponseEntity<List<Map<String, Object>>> getMonthlyAttendanceGrid(
            @RequestParam Long subjectId,
            @RequestParam int year,
            @RequestParam int month,
            Authentication authentication) {
        return ResponseEntity.ok(attendanceService.getMonthlyAttendanceGrid(subjectId, year, month, authentication.getName()));
    }

    @GetMapping("/defaulters")
    public ResponseEntity<List<Map<String, Object>>> getDefaulters(
            @RequestParam Long subjectId,
            @RequestParam(required = false) Double threshold,
            Authentication authentication) {
        return ResponseEntity.ok(attendanceService.getDefaulters(subjectId, threshold, authentication.getName()));
    }
}
