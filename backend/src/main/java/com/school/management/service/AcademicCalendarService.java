package com.school.management.service;

import com.school.management.dto.CalendarDto;
import com.school.management.dto.PagedResponse;
import com.school.management.entity.AcademicCalendar;
import com.school.management.repository.AcademicCalendarRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AcademicCalendarService {

    private final AcademicCalendarRepository calendarRepository;
    private final AuditLogService auditLogService;

    public AcademicCalendarService(AcademicCalendarRepository calendarRepository, AuditLogService auditLogService) {
        this.calendarRepository = calendarRepository;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public CalendarDto createEvent(CalendarDto dto, String adminUsername) {
        AcademicCalendar event = AcademicCalendar.builder()
                .title(dto.getTitle().trim())
                .date(dto.getDate())
                .type(dto.getType())
                .description(dto.getDescription() != null ? dto.getDescription().trim() : null)
                .build();

        AcademicCalendar saved = calendarRepository.save(event);
        auditLogService.log(adminUsername, com.school.management.entity.Role.ADMIN, "CALENDAR_CREATE",
                "Created event " + saved.getTitle() + " on " + saved.getDate(), null, saved.getType().name(), null);

        return mapToDto(saved);
    }

    public List<CalendarDto> getAllEvents() {
        return calendarRepository.findAll(Sort.by(Sort.Direction.ASC, "date")).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<CalendarDto> getEventsBetween(LocalDate start, LocalDate end) {
        return calendarRepository.findByDateBetween(start, end).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public boolean isHoliday(LocalDate date) {
        return calendarRepository.existsByDateAndType(date, AcademicCalendar.CalendarType.HOLIDAY);
    }

    public PagedResponse<CalendarDto> getEventsPaged(int page, int size, String sortBy, String sortDir, String search) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<AcademicCalendar> pageResult;
        if (search != null && !search.trim().isEmpty()) {
            pageResult = calendarRepository.findByTitleContainingIgnoreCase(search.trim(), pageable);
        } else {
            pageResult = calendarRepository.findAll(pageable);
        }

        List<CalendarDto> content = pageResult.getContent().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        return new PagedResponse<>(content, pageResult.getNumber(), pageResult.getSize(), pageResult.getTotalElements(), pageResult.getTotalPages(), pageResult.isLast());
    }

    @Transactional
    public void deleteEvent(Long id, String adminUsername) {
        AcademicCalendar event = calendarRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Calendar event not found: " + id));
        calendarRepository.delete(event);
        auditLogService.log(adminUsername, com.school.management.entity.Role.ADMIN, "CALENDAR_DELETE",
                "Deleted event " + event.getTitle() + " (" + event.getDate() + ")", event.getTitle(), null, null);
    }

    private CalendarDto mapToDto(AcademicCalendar calendar) {
        return CalendarDto.builder()
                .id(calendar.getId())
                .title(calendar.getTitle())
                .date(calendar.getDate())
                .type(calendar.getType())
                .description(calendar.getDescription())
                .build();
    }
}
