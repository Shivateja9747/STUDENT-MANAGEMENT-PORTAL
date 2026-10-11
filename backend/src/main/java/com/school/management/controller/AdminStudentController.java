package com.school.management.controller;

import com.school.management.dto.StudentPerformanceSummaryDto;
import com.school.management.dto.StudentRequest;
import com.school.management.dto.StudentResponse;
import com.school.management.entity.Student;
import com.school.management.repository.StudentRepository;
import com.school.management.service.AdminStudentService;
import com.school.management.service.StudentService;
import com.school.management.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminStudentController {

    private final AdminStudentService adminStudentService;
    private final UserService userService;
    private final StudentService studentService;
    private final StudentRepository studentRepository;

    public AdminStudentController(AdminStudentService adminStudentService, UserService userService, StudentService studentService, StudentRepository studentRepository) {
        this.adminStudentService = adminStudentService;
        this.userService = userService;
        this.studentService = studentService;
        this.studentRepository = studentRepository;
    }

    @PostMapping("/students")
    public ResponseEntity<StudentResponse> createStudent(@Valid @RequestBody StudentRequest request) {
        StudentResponse response = adminStudentService.createStudent(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/students")
    public ResponseEntity<?> getAllStudents(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir,
            @RequestParam(value = "search", required = false) String search) {
        if (page != null) {
            return ResponseEntity.ok(adminStudentService.getStudentsPaged(page, size, sortBy, sortDir, search));
        }
        return ResponseEntity.ok(adminStudentService.getAllStudents());
    }

    @GetMapping("/students/{id}")
    public ResponseEntity<StudentResponse> getStudentById(@PathVariable Long id) {
        return ResponseEntity.ok(adminStudentService.getStudentById(id));
    }

    @PutMapping("/students/{id}")
    public ResponseEntity<StudentResponse> updateStudent(@PathVariable Long id, @Valid @RequestBody StudentRequest request) {
        return ResponseEntity.ok(adminStudentService.updateStudent(id, request));
    }

    @DeleteMapping("/students/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        adminStudentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    // CSV Bulk Import
    @PostMapping("/students/import")
    public ResponseEntity<Map<String, Object>> importStudents(@RequestParam(value = "file", required = false) MultipartFile file, @RequestBody(required = false) String csvBody) {
        try {
            ByteArrayInputStream inputStream;
            if (file != null && !file.isEmpty()) {
                inputStream = new ByteArrayInputStream(file.getBytes());
            } else if (csvBody != null && !csvBody.trim().isEmpty()) {
                inputStream = new ByteArrayInputStream(csvBody.getBytes(StandardCharsets.UTF_8));
            } else {
                throw new RuntimeException("Please provide a CSV file or CSV text content.");
            }
            Map<String, Object> result = userService.bulkImportStudentsCsv(inputStream);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new RuntimeException("CSV import failed: " + e.getMessage());
        }
    }

    @GetMapping("/students/import-template")
    public ResponseEntity<byte[]> getStudentImportTemplate() {
        String csvContent = "rollNo,name,department,semester\n" +
                "CS6A01,Aarav Sharma,Computer Science,6\n" +
                "CS6A02,Ananya Patel,Computer Science,6\n";
        byte[] bytes = csvContent.getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"student_import_template.csv\"")
                .contentType(org.springframework.http.MediaType.parseMediaType("text/csv"))
                .body(bytes);
    }

    // Semester Promotion
    @PostMapping("/students/promote")
    public ResponseEntity<Map<String, Object>> promoteSemester(@RequestBody Map<String, Object> body) {
        String department = (String) body.get("department");
        Integer currentSemester = Integer.parseInt(body.get("currentSemester").toString());
        Map<String, Object> result = userService.promoteSemester(department, currentSemester);
        return ResponseEntity.ok(result);
    }

    // Admin view full student performance
    @GetMapping("/students/{id}/performance")
    public ResponseEntity<StudentPerformanceSummaryDto> getStudentPerformanceForAdmin(@PathVariable Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));
        return ResponseEntity.ok(studentService.getStudentPerformance(student.getUser().getUsername()));
    }

    // Admin Reset Password for any user
    @PostMapping("/users/{userId}/reset-password")
    public ResponseEntity<Map<String, String>> resetUserPassword(@PathVariable Long userId) {
        String tempPassword = userService.resetUserPasswordByAdmin(userId);
        return ResponseEntity.ok(Map.of("message", "Password reset successfully.", "tempPassword", tempPassword));
    }
}
