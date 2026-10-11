package com.school.management.controller;

import com.school.management.dto.MarksEntryRequest;
import com.school.management.dto.MarksResponse;
import com.school.management.entity.ExamType;
import com.school.management.service.TeacherMarksService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/teacher/marks")
@PreAuthorize("hasRole('TEACHER')")
public class TeacherMarksController {

    private final TeacherMarksService marksService;

    public TeacherMarksController(TeacherMarksService marksService) {
        this.marksService = marksService;
    }

    @PostMapping
    public ResponseEntity<List<MarksResponse>> saveMarks(
            @Valid @RequestBody MarksEntryRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(marksService.saveOrUpdateMarks(request, authentication.getName()));
    }

    @PutMapping
    public ResponseEntity<List<MarksResponse>> updateMarks(
            @Valid @RequestBody MarksEntryRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(marksService.saveOrUpdateMarks(request, authentication.getName()));
    }

    @GetMapping
    public ResponseEntity<List<MarksResponse>> getMarks(
            @RequestParam Long subjectId,
            @RequestParam ExamType examType,
            Authentication authentication) {
        return ResponseEntity.ok(marksService.getMarksBySubjectAndExamType(subjectId, examType, authentication.getName()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMarks(@PathVariable Long id, Authentication authentication) {
        marksService.deleteMarks(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getMarksStats(
            @RequestParam Long subjectId,
            @RequestParam ExamType examType,
            Authentication authentication) {
        return ResponseEntity.ok(marksService.getMarksStats(subjectId, examType, authentication.getName()));
    }
}
