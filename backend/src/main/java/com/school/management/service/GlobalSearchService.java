package com.school.management.service;

import com.school.management.entity.Announcement;
import com.school.management.entity.Student;
import com.school.management.entity.Subject;
import com.school.management.entity.Teacher;
import com.school.management.repository.AnnouncementRepository;
import com.school.management.repository.StudentRepository;
import com.school.management.repository.SubjectRepository;
import com.school.management.repository.TeacherRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GlobalSearchService {

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final SubjectRepository subjectRepository;
    private final AnnouncementRepository announcementRepository;

    public GlobalSearchService(StudentRepository studentRepository, TeacherRepository teacherRepository, SubjectRepository subjectRepository, AnnouncementRepository announcementRepository) {
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.subjectRepository = subjectRepository;
        this.announcementRepository = announcementRepository;
    }

    public List<Map<String, String>> searchAll(String query) {
        if (query == null || query.trim().length() < 2) {
            return Collections.emptyList();
        }

        String q = query.trim();
        List<Map<String, String>> results = new ArrayList<>();

        // 1. Search Students
        try {
            List<Student> students = studentRepository.findByNameContainingIgnoreCaseOrRollNoContainingIgnoreCaseOrDepartmentContainingIgnoreCase(q, q, q, PageRequest.of(0, 5)).getContent();
            for (Student s : students) {
                Map<String, String> item = new HashMap<>();
                item.put("title", s.getName() + " (" + s.getRollNo() + ")");
                item.put("type", "STUDENT");
                item.put("subtitle", "Dept: " + s.getDepartment() + " | Sem: " + s.getSemester() + " | Sec: " + s.getSection());
                item.put("id", String.valueOf(s.getId()));
                results.add(item);
            }
        } catch (Exception ignored) {}

        // 2. Search Teachers
        try {
            List<Teacher> teachers = teacherRepository.findByNameContainingIgnoreCaseOrDepartmentContainingIgnoreCase(q, q, PageRequest.of(0, 5)).getContent();
            for (Teacher t : teachers) {
                Map<String, String> item = new HashMap<>();
                item.put("title", t.getName());
                item.put("type", "TEACHER");
                item.put("subtitle", "Department: " + t.getDepartment());
                item.put("id", String.valueOf(t.getId()));
                results.add(item);
            }
        } catch (Exception ignored) {}

        // 3. Search Subjects
        try {
            List<Subject> subjects = subjectRepository.findAll().stream()
                    .filter(sub -> sub.getName().toLowerCase().contains(q.toLowerCase()) || sub.getCode().toLowerCase().contains(q.toLowerCase()))
                    .limit(5)
                    .toList();
            for (Subject sub : subjects) {
                Map<String, String> item = new HashMap<>();
                item.put("title", sub.getName() + " [" + sub.getCode() + "]");
                item.put("type", "SUBJECT");
                item.put("subtitle", "Semester " + sub.getSemester());
                item.put("id", String.valueOf(sub.getId()));
                results.add(item);
            }
        } catch (Exception ignored) {}

        // 4. Search Announcements
        try {
            List<Announcement> announcements = announcementRepository.findAll().stream()
                    .filter(a -> a.getTitle().toLowerCase().contains(q.toLowerCase()) || a.getContent().toLowerCase().contains(q.toLowerCase()))
                    .limit(5)
                    .toList();
            for (Announcement a : announcements) {
                Map<String, String> item = new HashMap<>();
                item.put("title", a.getTitle());
                item.put("type", "ANNOUNCEMENT");
                item.put("subtitle", a.getContent().length() > 60 ? a.getContent().substring(0, 57) + "..." : a.getContent());
                item.put("id", String.valueOf(a.getId()));
                results.add(item);
            }
        } catch (Exception ignored) {}

        return results;
    }
}
