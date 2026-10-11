package com.school.management.controller;

import com.school.management.dto.StudentMarksDto;
import com.school.management.dto.StudentPerformanceSummaryDto;
import com.school.management.dto.StudentSubjectAttendanceDto;
import com.school.management.service.StudentService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/student")
@PreAuthorize("hasRole('STUDENT')")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/attendance")
    public ResponseEntity<List<StudentSubjectAttendanceDto>> getAttendance(Authentication authentication) {
        return ResponseEntity.ok(studentService.getStudentAttendance(authentication.getName()));
    }

    @GetMapping("/marks")
    public ResponseEntity<List<StudentMarksDto>> getMarks(Authentication authentication) {
        return ResponseEntity.ok(studentService.getStudentMarks(authentication.getName()));
    }

    @GetMapping("/performance")
    public ResponseEntity<StudentPerformanceSummaryDto> getPerformance(Authentication authentication) {
        return ResponseEntity.ok(studentService.getStudentPerformance(authentication.getName()));
    }

    @GetMapping("/report-card/pdf")
    public ResponseEntity<byte[]> downloadReportCardPdf(Authentication authentication) {
        byte[] pdfBytes = studentService.generateReportCardPdf(authentication.getName());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report_card.txt")
                .contentType(MediaType.TEXT_PLAIN)
                .body(pdfBytes);
    }
}
