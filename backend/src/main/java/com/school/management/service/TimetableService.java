package com.school.management.service;

import com.school.management.dto.PagedResponse;
import com.school.management.dto.TimetableDto;
import com.school.management.entity.Subject;
import com.school.management.entity.Teacher;
import com.school.management.entity.Timetable;
import com.school.management.repository.SubjectRepository;
import com.school.management.repository.TeacherRepository;
import com.school.management.repository.TimetableRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TimetableService {

    private final TimetableRepository timetableRepository;
    private final SubjectRepository subjectRepository;
    private final TeacherRepository teacherRepository;
    private final AuditLogService auditLogService;

    public TimetableService(TimetableRepository timetableRepository, SubjectRepository subjectRepository, TeacherRepository teacherRepository, AuditLogService auditLogService) {
        this.timetableRepository = timetableRepository;
        this.subjectRepository = subjectRepository;
        this.teacherRepository = teacherRepository;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public TimetableDto createEntry(TimetableDto dto, String adminUsername) {
        Subject subject = subjectRepository.findById(dto.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Subject not found: " + dto.getSubjectId()));

        Teacher teacher = teacherRepository.findById(dto.getTeacherId())
                .orElseThrow(() -> new RuntimeException("Teacher not found: " + dto.getTeacherId()));

        Timetable entry = Timetable.builder()
                .subject(subject)
                .teacher(teacher)
                .dayOfWeek(dto.getDayOfWeek())
                .period(dto.getPeriod())
                .room(dto.getRoom() != null ? dto.getRoom().trim() : "Room 101")
                .section(dto.getSection() != null ? dto.getSection().trim().toUpperCase() : "A")
                .build();

        Timetable saved = timetableRepository.save(entry);
        auditLogService.log(adminUsername, com.school.management.entity.Role.ADMIN, "TIMETABLE_CREATE",
                "Assigned " + subject.getCode() + " to " + teacher.getName() + " on " + dto.getDayOfWeek() + " Period " + dto.getPeriod(), null, saved.getId().toString(), null);

        return mapToDto(saved);
    }

    public List<TimetableDto> getTodayClassesForTeacher(String teacherUsername) {
        Teacher teacher = teacherRepository.findByUserUsername(teacherUsername)
                .orElseThrow(() -> new RuntimeException("Teacher record not found for user: " + teacherUsername));

        DayOfWeek today = LocalDate.now().getDayOfWeek();
        return timetableRepository.findByTeacherIdAndDayOfWeek(teacher.getId(), today).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<TimetableDto> getAllEntries() {
        return timetableRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public PagedResponse<TimetableDto> getEntriesPaged(int page, int size, String sortBy, String sortDir, String search) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Timetable> pageResult;
        if (search != null && !search.trim().isEmpty()) {
            String query = search.trim();
            pageResult = timetableRepository.findBySectionContainingIgnoreCaseOrRoomContainingIgnoreCase(query, query, pageable);
        } else {
            pageResult = timetableRepository.findAll(pageable);
        }

        List<TimetableDto> content = pageResult.getContent().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        return new PagedResponse<>(content, pageResult.getNumber(), pageResult.getSize(), pageResult.getTotalElements(), pageResult.getTotalPages(), pageResult.isLast());
    }

    @Transactional
    public void deleteEntry(Long id, String adminUsername) {
        Timetable entry = timetableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Timetable entry not found: " + id));
        timetableRepository.delete(entry);
        auditLogService.log(adminUsername, com.school.management.entity.Role.ADMIN, "TIMETABLE_DELETE",
                "Deleted timetable entry ID " + id, entry.getSubject().getCode(), null, null);
    }

    private TimetableDto mapToDto(Timetable t) {
        return TimetableDto.builder()
                .id(t.getId())
                .subjectId(t.getSubject().getId())
                .subjectCode(t.getSubject().getCode())
                .subjectName(t.getSubject().getName())
                .teacherId(t.getTeacher().getId())
                .teacherName(t.getTeacher().getName())
                .dayOfWeek(t.getDayOfWeek())
                .period(t.getPeriod())
                .room(t.getRoom())
                .section(t.getSection())
                .build();
    }
}
