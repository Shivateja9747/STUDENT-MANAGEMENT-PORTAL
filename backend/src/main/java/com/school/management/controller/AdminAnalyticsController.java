package com.school.management.controller;

import com.school.management.entity.Attendance;
import com.school.management.entity.AttendanceStatus;
import com.school.management.entity.Student;
import com.school.management.entity.Subject;
import com.school.management.repository.AttendanceRepository;
import com.school.management.repository.StudentRepository;
import com.school.management.repository.SubjectRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/analytics")
@PreAuthorize("hasRole('ADMIN')")
public class AdminAnalyticsController {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;

    public AdminAnalyticsController(AttendanceRepository attendanceRepository, StudentRepository studentRepository, SubjectRepository subjectRepository) {
        this.attendanceRepository = attendanceRepository;
        this.studentRepository = studentRepository;
        this.subjectRepository = subjectRepository;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAdminAnalytics() {
        Map<String, Object> response = new HashMap<>();

        // 1. Attendance Trend (Last 30 Days)
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30);

        List<Attendance> recentAttendances = attendanceRepository.findAll().stream()
                .filter(a -> !a.getDate().isBefore(startDate) && !a.getDate().isAfter(endDate))
                .collect(Collectors.toList());

        Map<LocalDate, List<Attendance>> dateGrouped = recentAttendances.stream()
                .collect(Collectors.groupingBy(Attendance::getDate, TreeMap::new, Collectors.toList()));

        List<Map<String, Object>> trendList = new ArrayList<>();
        for (Map.Entry<LocalDate, List<Attendance>> entry : dateGrouped.entrySet()) {
            List<Attendance> list = entry.getValue();
            long presentCount = list.stream().filter(a -> a.getStatus() == AttendanceStatus.PRESENT || a.getStatus() == AttendanceStatus.LATE).count();
            long totalNonLeave = list.stream().filter(a -> a.getStatus() != AttendanceStatus.LEAVE).count();
            double pct = totalNonLeave > 0 ? round(((double) presentCount / totalNonLeave) * 100.0) : 100.0;

            Map<String, Object> point = new HashMap<>();
            point.put("date", entry.getKey().toString());
            point.put("attendancePct", pct);
            trendList.add(point);
        }
        response.put("attendanceTrend", trendList);

        // 2. Department Breakdown
        List<Student> students = studentRepository.findAll();
        Map<String, List<Student>> deptStudentsMap = students.stream()
                .collect(Collectors.groupingBy(Student::getDepartment));

        List<Map<String, Object>> deptList = new ArrayList<>();
        for (Map.Entry<String, List<Student>> entry : deptStudentsMap.entrySet()) {
            String dept = entry.getKey();
            List<Student> deptStudents = entry.getValue();

            List<Attendance> deptAttendances = attendanceRepository.findAll().stream()
                    .filter(a -> deptStudents.stream().anyMatch(s -> s.getId().equals(a.getStudent().getId())))
                    .collect(Collectors.toList());

            long present = deptAttendances.stream().filter(a -> a.getStatus() == AttendanceStatus.PRESENT || a.getStatus() == AttendanceStatus.LATE).count();
            long denominator = deptAttendances.stream().filter(a -> a.getStatus() != AttendanceStatus.LEAVE).count();
            double pct = denominator > 0 ? round(((double) present / denominator) * 100.0) : 100.0;

            Map<String, Object> dMap = new HashMap<>();
            dMap.put("department", dept);
            dMap.put("studentCount", deptStudents.size());
            dMap.put("attendancePct", pct);
            deptList.add(dMap);
        }
        response.put("departmentBreakdown", deptList);

        // 3. Lowest Subjects Attendance
        List<Subject> subjects = subjectRepository.findAll();
        List<Map<String, Object>> subjectList = new ArrayList<>();

        for (Subject sub : subjects) {
            List<Attendance> subAttendances = attendanceRepository.findBySubjectId(sub.getId());
            long present = subAttendances.stream().filter(a -> a.getStatus() == AttendanceStatus.PRESENT || a.getStatus() == AttendanceStatus.LATE).count();
            long denominator = subAttendances.stream().filter(a -> a.getStatus() != AttendanceStatus.LEAVE).count();
            double pct = denominator > 0 ? round(((double) present / denominator) * 100.0) : 100.0;

            Map<String, Object> sMap = new HashMap<>();
            sMap.put("subjectCode", sub.getCode());
            sMap.put("subjectName", sub.getName());
            sMap.put("attendancePct", pct);
            subjectList.add(sMap);
        }

        subjectList.sort(Comparator.comparingDouble(a -> (Double) a.get("attendancePct")));
        response.put("lowestSubjects", subjectList.stream().limit(5).collect(Collectors.toList()));

        return ResponseEntity.ok(response);
    }

    private double round(double val) {
        BigDecimal bd = BigDecimal.valueOf(val);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
