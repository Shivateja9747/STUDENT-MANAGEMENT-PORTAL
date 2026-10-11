package com.school.management.service;

import com.school.management.dto.StudentRequest;
import com.school.management.dto.UserProfileDto;
import com.school.management.entity.*;
import com.school.management.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminStudentService adminStudentService;
    private final AdminTeacherService adminTeacherService;

    public UserService(UserRepository userRepository, StudentRepository studentRepository, TeacherRepository teacherRepository, PasswordEncoder passwordEncoder, AdminStudentService adminStudentService, AdminTeacherService adminTeacherService) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminStudentService = adminStudentService;
        this.adminTeacherService = adminTeacherService;
    }

    public void changePassword(String username, String currentPassword, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("Incorrect current password.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setMustChangePassword(false);
        userRepository.save(user);
    }

    public String resetUserPasswordByAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        String tempPassword = "Temp@" + (1000 + new Random().nextInt(9000));
        user.setPassword(passwordEncoder.encode(tempPassword));
        user.setMustChangePassword(false);
        userRepository.save(user);

        return tempPassword;
    }

    public UserProfileDto getProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        UserProfileDto.UserProfileDtoBuilder builder = UserProfileDto.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .role(user.getRole().name())
                .email(user.getEmail())
                .phone(user.getPhone())
                .mustChangePassword(Boolean.TRUE.equals(user.getMustChangePassword()));

        if (user.getRole() == Role.STUDENT) {
            Optional<Student> studentOpt = studentRepository.findByUserUsername(username);
            studentOpt.ifPresent(s -> {
                builder.name(s.getName())
                       .rollNo(s.getRollNo())
                       .department(s.getDepartment())
                       .semester(s.getSemester());
            });
        } else if (user.getRole() == Role.TEACHER) {
            Optional<Teacher> teacherOpt = teacherRepository.findByUserUsername(username);
            teacherOpt.ifPresent(t -> {
                builder.name(t.getName())
                       .department(t.getDepartment());
            });
        } else {
            builder.name("System Administrator");
        }

        return builder.build();
    }

    public UserProfileDto updateProfile(String username, String email, String phone) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        user.setEmail(email);
        user.getPhone();
        user.setPhone(phone);
        userRepository.save(user);

        return getProfile(username);
    }

    @Transactional
    public Map<String, Object> bulkImportStudentsCsv(InputStream csvInputStream) {
        int successCount = 0;
        List<String> errors = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(csvInputStream, StandardCharsets.UTF_8))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (lineNumber == 1 && line.toLowerCase().contains("rollno")) {
                    continue; // Skip CSV header row
                }
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length < 4) {
                    errors.add("Line " + lineNumber + ": Invalid column count. Expected: rollNo,name,department,semester");
                    continue;
                }

                String rollNo = parts[0].trim();
                String name = parts[1].trim();
                String dept = parts[2].trim();
                String semStr = parts[3].trim();

                try {
                    int sem = Integer.parseInt(semStr);
                    StudentRequest req = StudentRequest.builder()
                            .rollNo(rollNo)
                            .name(name)
                            .department(dept)
                            .semester(sem)
                            .build();

                    adminStudentService.createStudent(req);
                    successCount++;
                } catch (Exception e) {
                    errors.add("Line " + lineNumber + " (" + rollNo + "): " + e.getMessage());
                }
            }
        } catch (Exception e) {
            errors.add("Failed to parse CSV file: " + e.getMessage());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("successCount", successCount);
        result.put("errors", errors);
        return result;
    }

    @Transactional
    public Map<String, Object> bulkImportTeachersCsv(InputStream csvInputStream) {
        int successCount = 0;
        List<String> errors = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(csvInputStream, StandardCharsets.UTF_8))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (lineNumber == 1 && (line.toLowerCase().contains("name") || line.toLowerCase().contains("username"))) {
                    continue; // Skip CSV header row
                }
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length < 2) {
                    errors.add("Line " + lineNumber + ": Invalid column count. Expected: name,department,[username],[password]");
                    continue;
                }

                String name = parts[0].trim();
                String dept = parts[1].trim();
                String username = parts.length >= 3 ? parts[2].trim() : null;
                String password = parts.length >= 4 ? parts[3].trim() : null;

                try {
                    com.school.management.dto.TeacherRequest req = com.school.management.dto.TeacherRequest.builder()
                            .name(name)
                            .department(dept)
                            .username(username)
                            .password(password)
                            .build();

                    adminTeacherService.createTeacher(req);
                    successCount++;
                } catch (Exception e) {
                    errors.add("Line " + lineNumber + " (" + name + "): " + e.getMessage());
                }
            }
        } catch (Exception e) {
            errors.add("Failed to parse CSV file: " + e.getMessage());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("successCount", successCount);
        result.put("errors", errors);
        return result;
    }

    @Transactional
    public Map<String, Object> promoteSemester(String department, Integer currentSemester) {
        List<Student> students = studentRepository.findByDepartmentAndSemester(department, currentSemester);
        int count = 0;

        for (Student s : students) {
            s.setSemester(currentSemester + 1);
            studentRepository.save(s);
            count++;
        }

        Map<String, Object> res = new HashMap<>();
        res.put("promotedCount", count);
        res.put("department", department);
        res.put("fromSemester", currentSemester);
        res.put("toSemester", currentSemester + 1);
        return res;
    }
}
