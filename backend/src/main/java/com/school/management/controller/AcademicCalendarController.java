package com.school.management.controller;

import com.school.management.dto.CalendarDto;
import com.school.management.dto.PagedResponse;
import com.school.management.service.AcademicCalendarService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AcademicCalendarController {

    private final AcademicCalendarService calendarService;

    public AcademicCalendarController(AcademicCalendarService calendarService) {
        this.calendarService = calendarService;
    }

    @PostMapping("/admin/calendar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CalendarDto> createEvent(@Valid @RequestBody CalendarDto dto, Authentication auth) {
        return new ResponseEntity<>(calendarService.createEvent(dto, auth.getName()), HttpStatus.CREATED);
    }

    @GetMapping("/calendar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAllEvents(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "date") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir,
            @RequestParam(value = "search", required = false) String search) {
        if (page != null) {
            return ResponseEntity.ok(calendarService.getEventsPaged(page, size, sortBy, sortDir, search));
        }
        return ResponseEntity.ok(calendarService.getAllEvents());
    }

    @DeleteMapping("/admin/calendar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id, Authentication auth) {
        calendarService.deleteEvent(id, auth.getName());
        return ResponseEntity.noContent().build();
    }
}
