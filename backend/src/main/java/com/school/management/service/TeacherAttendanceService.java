package com.school.management.service;

import com.school.management.config.AttendanceConfig;
import com.school.management.dto.*;
import com.school.management.entity.*;
import com.school.management.repository.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TeacherAttendanceService {

    private final TeacherRepository teacherRepository;
    private final SubjectRepository subjectRepository;
    private final StudentRepository studentRepository;
    private final AttendanceRepository attendanceRepository;
    private final AcademicCalendarService academicCalendarService;

    public TeacherAttendanceService(TeacherRepository teacherRepository, SubjectRepository subjectRepository, StudentRepository studentRepository, AttendanceRepository attendanceRepository, AcademicCalendarService academicCalendarService) {
        this.teacherRepository = teacherRepository;
        this.subjectRepository = subjectRepository;
        this.studentRepository = studentRepository;
        this.attendanceRepository = attendanceRepository;
        this.academicCalendarService = academicCalendarService;
    }

    public Teacher getTeacherByUsername(String username) {
        return teacherRepository.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Teacher profile not found for user: " + username));
    }

    public List<SubjectResponse> getTeacherSubjects(String username) {
        Teacher teacher = getTeacherByUsername(username);
        return subjectRepository.findByTeacherId(teacher.getId()).stream()
                .map(sub -> SubjectResponse.builder()
                        .id(sub.getId())
                        .code(sub.getCode())
                        .name(sub.getName())
                        .semester(sub.getSemester())
                        .teacherId(teacher.getId())
                        .teacherName(teacher.getName())
                        .build())
                .collect(Collectors.toList());
    }

    public List<StudentResponse> getStudentsForTeacherSubject(Long subjectId, String username) {
        Teacher teacher = getTeacherByUsername(username);
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found with id: " + subjectId));

        if (subject.getTeacher() == null || !subject.getTeacher().getId().equals(teacher.getId())) {
            throw new AccessDeniedException("Subject is not assigned to you.");
        }

        List<Student> students = studentRepository.findBySemester(subject.getSemester());

        return students.stream()
                .map(s -> StudentResponse.builder()
                        .id(s.getId())
                        .userId(s.getUser().getId())
                        .username(s.getUser().getUsername())
                        .rollNo(s.getRollNo())
                        .name(s.getName())
                        .department(s.getDepartment())
                        .semester(s.getSemester())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public List<AttendanceResponse> saveBulkAttendance(BulkAttendanceRequest request, String username) {
        if (request.getDate().isAfter(LocalDate.now())) {
            throw new RuntimeException("Attendance date cannot be in the future.");
        }

        // Validate holiday / exam day restriction
        if (academicCalendarService.isHoliday(request.getDate())) {
            throw new RuntimeException("Attendance marking is blocked on official academic holidays.");
        }

        // Validate 7-day editing window for teachers
        if (request.getDate().isBefore(LocalDate.now().minusDays(AttendanceConfig.TEACHER_EDIT_WINDOW_DAYS))) {
            throw new RuntimeException("Editing attendance for dates older than " + AttendanceConfig.TEACHER_EDIT_WINDOW_DAYS + " days is restricted.");
        }

        Teacher teacher = getTeacherByUsername(username);
        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Subject not found with id: " + request.getSubjectId()));

        if (subject.getTeacher() == null || !subject.getTeacher().getId().equals(teacher.getId())) {
            throw new AccessDeniedException("Subject is not assigned to you.");
        }

        Integer period = request.getPeriod() != null ? request.getPeriod() : 1;

        for (AttendanceRecordRequest rec : request.getRecords()) {
            Student student = studentRepository.findById(rec.getStudentId())
                    .orElseThrow(() -> new RuntimeException("Student not found with id: " + rec.getStudentId()));

            if (!student.getSemester().equals(subject.getSemester())) {
                throw new RuntimeException("Student " + student.getRollNo() + " (Semester " + student.getSemester() + 
                        ") does not belong to subject " + subject.getCode() + " (Semester " + subject.getSemester() + ").");
            }

            Optional<Attendance> existingOpt = attendanceRepository.findByStudentIdAndSubjectIdAndDateAndPeriod(
                    student.getId(), subject.getId(), request.getDate(), period);

            Attendance attendance;
            if (existingOpt.isPresent()) {
                attendance = existingOpt.get();
                attendance.setStatus(rec.getStatus());
                attendance.setRemark(rec.getRemark());
            } else {
                attendance = Attendance.builder()
                        .student(student)
                        .subject(subject)
                        .date(request.getDate())
                        .period(period)
                        .status(rec.getStatus())
                        .remark(rec.getRemark())
                        .build();
            }
            try {
                attendanceRepository.save(attendance);
            } catch (org.springframework.dao.DataIntegrityViolationException e) {
                Optional<Attendance> existing = attendanceRepository.findByStudentIdAndSubjectIdAndDateAndPeriod(
                        student.getId(), subject.getId(), request.getDate(), period);
                if (existing.isPresent()) {
                    Attendance att = existing.get();
                    att.setStatus(rec.getStatus());
                    att.setRemark(rec.getRemark());
                    attendanceRepository.save(att);
                } else {
                    throw e;
                }
            }
        }

        return getAttendanceBySubjectDateAndPeriod(subject.getId(), request.getDate(), period, username);
    }

    public List<AttendanceResponse> getAttendanceBySubjectDateAndPeriod(Long subjectId, LocalDate date, Integer period, String username) {
        Teacher teacher = getTeacherByUsername(username);
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found with id: " + subjectId));

        if (subject.getTeacher() == null || !subject.getTeacher().getId().equals(teacher.getId())) {
            throw new AccessDeniedException("Subject is not assigned to you.");
        }

        Integer targetPeriod = period != null ? period : 1;
        List<Attendance> list = attendanceRepository.findBySubjectIdAndDateAndPeriod(subjectId, date, targetPeriod);

        return list.stream()
                .map(a -> AttendanceResponse.builder()
                        .id(a.getId())
                        .studentId(a.getStudent().getId())
                        .studentName(a.getStudent().getName())
                        .studentRollNo(a.getStudent().getRollNo())
                        .subjectId(a.getSubject().getId())
                        .subjectName(a.getSubject().getName())
                        .date(a.getDate())
                        .period(a.getPeriod())
                        .status(a.getStatus())
                        .remark(a.getRemark())
                        .build())
                .collect(Collectors.toList());
    }

    // Monthly Attendance Grid Matrix
    public List<Map<String, Object>> getMonthlyAttendanceGrid(Long subjectId, int year, int month, String username) {
        Teacher teacher = getTeacherByUsername(username);
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found with id: " + subjectId));

        if (subject.getTeacher() == null || !subject.getTeacher().getId().equals(teacher.getId())) {
            throw new AccessDeniedException("Subject is not assigned to you.");
        }

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        List<Student> students = studentRepository.findBySemester(subject.getSemester());
        List<Attendance> monthAttendances = attendanceRepository.findBySubjectIdAndDateBetween(subjectId, startDate, endDate);

        List<Map<String, Object>> result = new ArrayList<>();

        for (Student s : students) {
            Map<String, Object> studentRow = new LinkedHashMap<>();
            studentRow.put("studentId", s.getId());
            studentRow.put("rollNo", s.getRollNo());
            studentRow.put("name", s.getName());

            List<Attendance> studentRecords = monthAttendances.stream()
                    .filter(a -> a.getStudent().getId().equals(s.getId()))
                    .collect(Collectors.toList());

            Map<Integer, String> dayMap = new HashMap<>();
            int presentCount = 0;
            int lateCount = 0;
            int leaveCount = 0;
            int absentCount = 0;

            for (Attendance a : studentRecords) {
                int day = a.getDate().getDayOfMonth();
                String code = "A";
                if (a.getStatus() == AttendanceStatus.PRESENT) { code = "P"; presentCount++; }
                else if (a.getStatus() == AttendanceStatus.LATE) { code = "L"; lateCount++; }
                else if (a.getStatus() == AttendanceStatus.LEAVE) { code = "LV"; leaveCount++; }
                else if (a.getStatus() == AttendanceStatus.ABSENT) { code = "A"; absentCount++; }

                dayMap.put(day, code);
            }

            studentRow.put("dayStatusMap", dayMap);

            int total = studentRecords.size();
            int attended = presentCount + (AttendanceConfig.COUNT_LATE_AS_PRESENT ? lateCount : 0);
            int denominator = total - (AttendanceConfig.EXCLUDE_LEAVE_FROM_DENOMINATOR ? leaveCount : 0);
            double pct = denominator > 0 ? Math.round(((double) attended / denominator) * 100.0 * 100.0) / 100.0 : 100.0;

            studentRow.put("totalClasses", total);
            studentRow.put("percentage", pct);

            result.add(studentRow);
        }

        return result;
    }

    // Defaulters List
    public List<Map<String, Object>> getDefaulters(Long subjectId, Double threshold, String username) {
        Teacher teacher = getTeacherByUsername(username);
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found with id: " + subjectId));

        if (subject.getTeacher() == null || !subject.getTeacher().getId().equals(teacher.getId())) {
            throw new AccessDeniedException("Subject is not assigned to you.");
        }

        double cutoff = threshold != null ? threshold : AttendanceConfig.DEFAULT_ATTENDANCE_THRESHOLD;
        List<Student> students = studentRepository.findBySemester(subject.getSemester());
        List<Attendance> allAttendances = attendanceRepository.findBySubjectId(subjectId);

        List<Map<String, Object>> defaulters = new ArrayList<>();

        for (Student s : students) {
            List<Attendance> studentRecords = allAttendances.stream()
                    .filter(a -> a.getStudent().getId().equals(s.getId()))
                    .collect(Collectors.toList());

            int total = studentRecords.size();
            int present = (int) studentRecords.stream().filter(a -> a.getStatus() == AttendanceStatus.PRESENT).count();
            int late = (int) studentRecords.stream().filter(a -> a.getStatus() == AttendanceStatus.LATE).count();
            int leave = (int) studentRecords.stream().filter(a -> a.getStatus() == AttendanceStatus.LEAVE).count();

            int attended = present + (AttendanceConfig.COUNT_LATE_AS_PRESENT ? late : 0);
            int denominator = total - (AttendanceConfig.EXCLUDE_LEAVE_FROM_DENOMINATOR ? leave : 0);
            double pct = denominator > 0 ? Math.round(((double) attended / denominator) * 100.0 * 100.0) / 100.0 : 100.0;

            if (pct < cutoff) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("studentId", s.getId());
                map.put("rollNo", s.getRollNo());
                map.put("name", s.getName());
                map.put("department", s.getDepartment());
                map.put("percentage", pct);
                map.put("totalClasses", total);
                map.put("attendedClasses", attended);
                defaulters.add(map);
            }
        }

        return defaulters;
    }
}
