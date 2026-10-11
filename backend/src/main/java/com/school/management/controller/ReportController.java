package com.school.management.controller;

import com.school.management.service.ReportExportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ReportController {

    private final ReportExportService reportExportService;

    public ReportController(ReportExportService reportExportService) {
        this.reportExportService = reportExportService;
    }

    @GetMapping("/reports/attendance/excel")
    @PreAuthorize("hasAnyRole('TEACHER', 'HOD', 'ADMIN')")
    public ResponseEntity<byte[]> exportAttendanceExcel(@RequestParam("subjectId") Long subjectId) {
        byte[] bytes = reportExportService.exportAttendanceExcel(subjectId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"attendance_report_" + subjectId + ".xlsx\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(bytes);
    }

    @GetMapping("/reports/attendance/pdf")
    @PreAuthorize("hasAnyRole('TEACHER', 'HOD', 'ADMIN')")
    public ResponseEntity<byte[]> exportAttendancePdf(@RequestParam("subjectId") Long subjectId) {
        byte[] bytes = reportExportService.exportAttendancePdf(subjectId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"attendance_report_" + subjectId + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(bytes);
    }

    @GetMapping("/student/certificate")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<byte[]> getAttendanceCertificate(Authentication auth) {
        byte[] bytes = reportExportService.generateAttendanceCertificatePdf(auth.getName());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"attendance_certificate.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(bytes);
    }
}
