package com.school.management.controller;

import com.school.management.dto.TimetableDto;
import com.school.management.service.TimetableService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TimetableController {

    private final TimetableService timetableService;

    public TimetableController(TimetableService timetableService) {
        this.timetableService = timetableService;
    }

    @PostMapping("/admin/timetable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TimetableDto> createEntry(@Valid @RequestBody TimetableDto dto, Authentication auth) {
        return new ResponseEntity<>(timetableService.createEntry(dto, auth.getName()), HttpStatus.CREATED);
    }

    @GetMapping("/timetable")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAllEntries(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir,
            @RequestParam(value = "search", required = false) String search) {
        if (page != null) {
            return ResponseEntity.ok(timetableService.getEntriesPaged(page, size, sortBy, sortDir, search));
        }
        return ResponseEntity.ok(timetableService.getAllEntries());
    }

    @GetMapping("/teacher/timetable/today")
    @PreAuthorize("hasAnyRole('TEACHER', 'HOD', 'ADMIN')")
    public ResponseEntity<List<TimetableDto>> getTodayClasses(Authentication auth) {
        return ResponseEntity.ok(timetableService.getTodayClassesForTeacher(auth.getName()));
    }

    @DeleteMapping("/admin/timetable/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEntry(@PathVariable Long id, Authentication auth) {
        timetableService.deleteEntry(id, auth.getName());
        return ResponseEntity.noContent().build();
    }
}
