package com.school.management.controller;

import com.school.management.dto.TeacherRequest;
import com.school.management.dto.TeacherResponse;
import com.school.management.service.AdminTeacherService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.school.management.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/teachers")
@PreAuthorize("hasRole('ADMIN')")
public class AdminTeacherController {

    private final AdminTeacherService teacherService;
    private final UserService userService;

    public AdminTeacherController(AdminTeacherService teacherService, UserService userService) {
        this.teacherService = teacherService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<TeacherResponse> createTeacher(@Valid @RequestBody TeacherRequest request) {
        TeacherResponse response = teacherService.createTeacher(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<?> getAllTeachers(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir,
            @RequestParam(value = "search", required = false) String search) {
        if (page != null) {
            return ResponseEntity.ok(teacherService.getTeachersPaged(page, size, sortBy, sortDir, search));
        }
        return ResponseEntity.ok(teacherService.getAllTeachers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeacherResponse> getTeacherById(@PathVariable Long id) {
        return ResponseEntity.ok(teacherService.getTeacherById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeacherResponse> updateTeacher(@PathVariable Long id, @Valid @RequestBody TeacherRequest request) {
        return ResponseEntity.ok(teacherService.updateTeacher(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeacher(@PathVariable Long id) {
        teacherService.deleteTeacher(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/import")
    public ResponseEntity<Map<String, Object>> importTeachers(@RequestParam(value = "file", required = false) MultipartFile file, @RequestBody(required = false) String csvBody) {
        try {
            ByteArrayInputStream inputStream;
            if (file != null && !file.isEmpty()) {
                inputStream = new ByteArrayInputStream(file.getBytes());
            } else if (csvBody != null && !csvBody.trim().isEmpty()) {
                inputStream = new ByteArrayInputStream(csvBody.getBytes(StandardCharsets.UTF_8));
            } else {
                throw new RuntimeException("Please provide a CSV file or CSV text content.");
            }
            Map<String, Object> result = userService.bulkImportTeachersCsv(inputStream);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new RuntimeException("CSV import failed: " + e.getMessage());
        }
    }

    @GetMapping("/import-template")
    public ResponseEntity<byte[]> getImportTemplate() {
        String csvContent = "name,department,username,password\n" +
                "Dr. Ramesh Kumar,Computer Science,prof_ramesh,Teacher@123\n" +
                "Dr. Sunita Sharma,Electronics,prof_sunita,Teacher@123\n";
        byte[] bytes = csvContent.getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"teacher_import_template.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(bytes);
    }
}
