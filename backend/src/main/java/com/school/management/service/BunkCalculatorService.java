package com.school.management.service;

import com.school.management.dto.BunkCalculatorDto;
import com.school.management.entity.Attendance;
import com.school.management.entity.AttendanceStatus;
import com.school.management.entity.Student;
import com.school.management.entity.Subject;
import com.school.management.repository.AttendanceRepository;
import com.school.management.repository.StudentRepository;
import com.school.management.repository.SubjectRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BunkCalculatorService {

    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final AttendanceRepository attendanceRepository;

    public BunkCalculatorService(StudentRepository studentRepository, SubjectRepository subjectRepository, AttendanceRepository attendanceRepository) {
        this.studentRepository = studentRepository;
        this.subjectRepository = subjectRepository;
        this.attendanceRepository = attendanceRepository;
    }

    public List<BunkCalculatorDto> calculateBunkMarginForStudent(String studentUsername, Double targetPct) {
        Student student = studentRepository.findByUserUsername(studentUsername)
                .orElseThrow(() -> new RuntimeException("Student not found for username: " + studentUsername));

        double target = targetPct != null ? targetPct : 75.0;
        List<Subject> subjects = subjectRepository.findBySemester(student.getSemester());
        List<BunkCalculatorDto> results = new ArrayList<>();

        for (Subject subject : subjects) {
            List<Attendance> records = attendanceRepository.findByStudentIdAndSubjectId(student.getId(), subject.getId());
            int total = 0;
            int attended = 0;

            for (Attendance a : records) {
                if (a.getStatus() != AttendanceStatus.LEAVE) {
                    total++;
                    if (a.getStatus() == AttendanceStatus.PRESENT || a.getStatus() == AttendanceStatus.LATE) {
                        attended++;
                    }
                }
            }

            double currentPct = total > 0 ? ((double) attended / total) * 100.0 : 100.0;
            int canMiss = 0;
            int mustAttend = 0;
            String msg;

            if (total == 0) {
                msg = "No classes conducted yet.";
            } else if (currentPct >= target) {
                // Formula: (attended - target*total) / target
                double targetRatio = target / 100.0;
                canMiss = (int) Math.floor((attended - (targetRatio * total)) / targetRatio);
                if (canMiss < 0) canMiss = 0;
                msg = "You can miss up to " + canMiss + " class(es) and remain above " + target + "%.";
            } else {
                // Formula: (target*total - attended) / (1 - target)
                double targetRatio = target / 100.0;
                mustAttend = (int) Math.ceil(((targetRatio * total) - attended) / (1.0 - targetRatio));
                if (mustAttend < 0) mustAttend = 0;
                msg = "You must attend the next " + mustAttend + " class(es) to reach " + target + "%.";
            }

            results.add(BunkCalculatorDto.builder()
                    .subjectId(subject.getId())
                    .subjectCode(subject.getCode())
                    .subjectName(subject.getName())
                    .totalClasses(total)
                    .attendedClasses(attended)
                    .currentPercentage(Math.round(currentPct * 10.0) / 10.0)
                    .targetPercentage(target)
                    .classesCanMiss(canMiss)
                    .classesToAttend(mustAttend)
                    .statusMessage(msg)
                    .build());
        }

        return results;
    }
}
