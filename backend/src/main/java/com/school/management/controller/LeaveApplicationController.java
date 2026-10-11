package com.school.management.controller;

import com.school.management.dto.LeaveApplicationDto;
import com.school.management.entity.Role;
import com.school.management.service.FileStorageService;
import com.school.management.service.LeaveApplicationService;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class LeaveApplicationController {

    private final LeaveApplicationService leaveService;
    private final FileStorageService fileStorageService;

    public LeaveApplicationController(LeaveApplicationService leaveService, FileStorageService fileStorageService) {
        this.leaveService = leaveService;
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/student/leave")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<LeaveApplicationDto> applyLeave(@Valid @RequestBody LeaveApplicationDto dto, Authentication auth) {
        return new ResponseEntity<>(leaveService.applyLeave(auth.getName(), dto), HttpStatus.CREATED);
    }

    @PostMapping("/student/leave/attachment")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Map<String, String>> uploadAttachment(@RequestParam("file") MultipartFile file) {
        String filename = fileStorageService.storeFile(file);
        return ResponseEntity.ok(Map.of("filename", filename, "fileUrl", "/api/leave/attachments/" + filename));
    }

    @GetMapping("/leave/attachments/{filename}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Resource> downloadAttachment(@PathVariable String filename) {
        try {
            Path filePath = fileStorageService.loadFileAsPath(filename);
            Resource resource = new UrlResource(filePath.toUri());

            String contentType = "application/octet-stream";
            if (filename.toLowerCase().endsWith(".pdf")) contentType = "application/pdf";
            else if (filename.toLowerCase().endsWith(".jpg") || filename.toLowerCase().endsWith(".jpeg")) contentType = "image/jpeg";
            else if (filename.toLowerCase().endsWith(".png")) contentType = "image/png";

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(resource);
        } catch (Exception e) {
            throw new RuntimeException("Could not download attachment: " + e.getMessage());
        }
    }

    @GetMapping("/student/leave")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<LeaveApplicationDto>> getMyLeaves(Authentication auth) {
        return ResponseEntity.ok(leaveService.getStudentLeaves(auth.getName()));
    }

    @GetMapping("/teacher/leave/pending")
    @PreAuthorize("hasAnyRole('TEACHER', 'HOD', 'ADMIN')")
    public ResponseEntity<List<LeaveApplicationDto>> getPendingLeaves() {
        return ResponseEntity.ok(leaveService.getPendingLeaves());
    }

    @PostMapping("/teacher/leave/{id}/review")
    @PreAuthorize("hasAnyRole('TEACHER', 'HOD', 'ADMIN')")
    public ResponseEntity<LeaveApplicationDto> reviewLeave(@PathVariable Long id, @RequestBody Map<String, Object> body, Authentication auth) {
        boolean approve = Boolean.TRUE.equals(body.get("approve"));
        String comment = (String) body.get("comment");

        Role reviewerRole = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().contains("HOD")) ? Role.HOD : Role.TEACHER;

        return ResponseEntity.ok(leaveService.reviewLeave(id, auth.getName(), reviewerRole, approve, comment));
    }
}
