package com.school.management.service;

import com.school.management.dto.PagedResponse;
import com.school.management.dto.SubjectRequest;
import com.school.management.dto.SubjectResponse;
import com.school.management.entity.Subject;
import com.school.management.entity.Teacher;
import com.school.management.repository.AttendanceRepository;
import com.school.management.repository.MarksRepository;
import com.school.management.repository.SubjectRepository;
import com.school.management.repository.TeacherRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminSubjectService {

    private final SubjectRepository subjectRepository;
    private final TeacherRepository teacherRepository;
    private final AttendanceRepository attendanceRepository;
    private final MarksRepository marksRepository;

    public AdminSubjectService(SubjectRepository subjectRepository, TeacherRepository teacherRepository, AttendanceRepository attendanceRepository, MarksRepository marksRepository) {
        this.subjectRepository = subjectRepository;
        this.teacherRepository = teacherRepository;
        this.attendanceRepository = attendanceRepository;
        this.marksRepository = marksRepository;
    }

    @Transactional
    public SubjectResponse createSubject(SubjectRequest request) {
        if (subjectRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Subject code already exists: " + request.getCode());
        }

        Teacher teacher = null;
        if (request.getTeacherId() != null) {
            teacher = teacherRepository.findById(request.getTeacherId())
                    .orElseThrow(() -> new RuntimeException("Teacher not found with id: " + request.getTeacherId()));
        }

        Subject subject = Subject.builder()
                .code(request.getCode().trim())
                .name(request.getName().trim())
                .semester(request.getSemester())
                .teacher(teacher)
                .build();

        Subject saved = subjectRepository.save(subject);
        return mapToResponse(saved);
    }

    public List<SubjectResponse> getAllSubjects() {
        return subjectRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public PagedResponse<SubjectResponse> getSubjectsPaged(int page, int size, String sortBy, String sortDir, String search) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Subject> pageResult;
        if (search != null && !search.trim().isEmpty()) {
            String query = search.trim();
            pageResult = subjectRepository.findByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(query, query, pageable);
        } else {
            pageResult = subjectRepository.findAll(pageable);
        }

        List<SubjectResponse> content = pageResult.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return new PagedResponse<>(content, pageResult.getNumber(), pageResult.getSize(), pageResult.getTotalElements(), pageResult.getTotalPages(), pageResult.isLast());
    }

    public SubjectResponse getSubjectById(Long id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subject not found with id: " + id));
        return mapToResponse(subject);
    }

    @Transactional
    public SubjectResponse updateSubject(Long id, SubjectRequest request) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subject not found with id: " + id));

        if (!subject.getCode().equalsIgnoreCase(request.getCode()) &&
                subjectRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Subject code already exists: " + request.getCode());
        }

        Teacher teacher = null;
        if (request.getTeacherId() != null) {
            teacher = teacherRepository.findById(request.getTeacherId())
                    .orElseThrow(() -> new RuntimeException("Teacher not found with id: " + request.getTeacherId()));
        }

        subject.setCode(request.getCode().trim());
        subject.setName(request.getName().trim());
        subject.setSemester(request.getSemester());
        subject.setTeacher(teacher);

        Subject updated = subjectRepository.save(subject);
        return mapToResponse(updated);
    }

    @Transactional
    public void deleteSubject(Long id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subject not found with id: " + id));

        if (attendanceRepository.existsBySubjectId(id) || marksRepository.existsBySubjectId(id)) {
            throw new RuntimeException("Cannot delete subject because attendance or marks records are associated with it.");
        }

        subjectRepository.delete(subject);
    }

    private SubjectResponse mapToResponse(Subject subject) {
        return SubjectResponse.builder()
                .id(subject.getId())
                .code(subject.getCode())
                .name(subject.getName())
                .semester(subject.getSemester())
                .teacherId(subject.getTeacher() != null ? subject.getTeacher().getId() : null)
                .teacherName(subject.getTeacher() != null ? subject.getTeacher().getName() : "Unassigned")
                .build();
    }
}
