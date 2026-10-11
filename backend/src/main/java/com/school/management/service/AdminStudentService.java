package com.school.management.service;

import com.school.management.dto.PagedResponse;
import com.school.management.dto.StudentRequest;
import com.school.management.dto.StudentResponse;
import com.school.management.entity.Role;
import com.school.management.entity.Student;
import com.school.management.entity.User;
import com.school.management.repository.StudentRepository;
import com.school.management.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminStudentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminStudentService(StudentRepository studentRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public StudentResponse createStudent(StudentRequest request) {
        String username = (request.getUsername() != null && !request.getUsername().trim().isEmpty())
                ? request.getUsername().trim()
                : request.getRollNo().trim();

        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists: " + username);
        }
        if (studentRepository.existsByRollNo(request.getRollNo())) {
            throw new RuntimeException("Roll number already exists: " + request.getRollNo());
        }

        String rawPassword = (request.getPassword() != null && !request.getPassword().trim().isEmpty())
                ? request.getPassword().trim()
                : "student123";

        String email = (request.getEmail() != null && !request.getEmail().trim().isEmpty())
                ? request.getEmail().trim()
                : username.toLowerCase() + "@edutrack.edu";

        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(rawPassword))
                .role(Role.STUDENT)
                .email(email)
                .fullName(request.getName().trim())
                .mustChangePassword(false)
                .build();

        Student student = Student.builder()
                .user(user)
                .rollNo(request.getRollNo().trim())
                .name(request.getName().trim())
                .department(request.getDepartment().trim())
                .semester(request.getSemester())
                .build();

        Student savedStudent = studentRepository.save(student);
        return mapToResponse(savedStudent);
    }

    public List<StudentResponse> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public PagedResponse<StudentResponse> getStudentsPaged(int page, int size, String sortBy, String sortDir, String search) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Student> studentsPage;
        if (search != null && !search.trim().isEmpty()) {
            String query = search.trim();
            studentsPage = studentRepository.findByNameContainingIgnoreCaseOrRollNoContainingIgnoreCaseOrDepartmentContainingIgnoreCase(query, query, query, pageable);
        } else {
            studentsPage = studentRepository.findAll(pageable);
        }

        List<StudentResponse> content = studentsPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return new PagedResponse<>(content, studentsPage.getNumber(), studentsPage.getSize(), studentsPage.getTotalElements(), studentsPage.getTotalPages(), studentsPage.isLast());
    }

    public StudentResponse getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));
        return mapToResponse(student);
    }

    @Transactional
    public StudentResponse updateStudent(Long id, StudentRequest request) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));

        if (!student.getRollNo().equalsIgnoreCase(request.getRollNo()) &&
                studentRepository.existsByRollNo(request.getRollNo())) {
            throw new RuntimeException("Roll number already exists: " + request.getRollNo());
        }

        student.setRollNo(request.getRollNo().trim());
        student.setName(request.getName().trim());
        student.setDepartment(request.getDepartment().trim());
        student.setSemester(request.getSemester());

        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            student.getUser().setPassword(passwordEncoder.encode(request.getPassword().trim()));
        }

        Student updated = studentRepository.save(student);
        return mapToResponse(updated);
    }

    @Transactional
    public void deleteStudent(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));
        studentRepository.delete(student);
    }

    private StudentResponse mapToResponse(Student student) {
        return StudentResponse.builder()
                .id(student.getId())
                .userId(student.getUser().getId())
                .username(student.getUser().getUsername())
                .rollNo(student.getRollNo())
                .name(student.getName())
                .department(student.getDepartment())
                .semester(student.getSemester())
                .build();
    }
}
