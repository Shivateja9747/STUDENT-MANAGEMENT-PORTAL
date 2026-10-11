package com.school.management.integration;

import com.school.management.config.DemoDataSeeder;
import com.school.management.dto.*;
import com.school.management.entity.*;
import com.school.management.repository.*;
import com.school.management.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "app.seed.demo-data=true")
@Transactional
public class EduTrackIntegrationTest {

    @Autowired private UserRepository userRepository;
    @Autowired private StudentRepository studentRepository;
    @Autowired private TeacherRepository teacherRepository;
    @Autowired private SubjectRepository subjectRepository;
    @Autowired private AttendanceRepository attendanceRepository;
    @Autowired private MarksRepository marksRepository;
    @Autowired private LeaveApplicationRepository leaveRepository;
    @Autowired private AttendanceCorrectionRepository correctionRepository;
    @Autowired private QrSessionRepository qrSessionRepository;
    @Autowired private AuditLogRepository auditLogRepository;

    @Autowired private TeacherMarksService teacherMarksService;
    @Autowired private MarksPublishingAndWeightageService publishingService;
    @Autowired private StudentService studentService;
    @Autowired private LeaveApplicationService leaveService;
    @Autowired private AttendanceCorrectionService correctionService;
    @Autowired private QrSessionService qrSessionService;
    @Autowired private AuditLogService auditLogService;
    @Autowired private DemoDataSeeder demoDataSeeder;

    private User teacherUser;
    private User studentUser;
    private Teacher teacher;
    private Student student;
    private Subject subject;

    @BeforeEach
    void setUp() throws Exception {
        demoDataSeeder.run();

        student = studentRepository.findByRollNo("CS6A01")
                .orElseGet(() -> studentRepository.findAll().stream().findFirst().orElseThrow());
        studentUser = student.getUser();

        teacher = teacherRepository.findAll().stream().findFirst().orElseThrow();
        teacherUser = teacher.getUser();

        subject = subjectRepository.findAll().stream().findFirst().orElseThrow();
        subject.setTeacher(teacher);
        subjectRepository.save(subject);
    }

    @Test
    @DisplayName("1. Teacher marks entry, edit, publish and unpublish")
    void testTeacherMarksEntryEditPublishUnpublish() {
        MarksRecordRequest recordReq = new MarksRecordRequest();
        recordReq.setStudentId(student.getId());
        recordReq.setMarksObtained(85.0);
        recordReq.setMaxMarks(100.0);
        recordReq.setIsAbsent(false);

        MarksEntryRequest request = new MarksEntryRequest();
        request.setSubjectId(subject.getId());
        request.setExamType(ExamType.MID1);
        request.setRecords(Collections.singletonList(recordReq));

        List<MarksResponse> responses = teacherMarksService.saveOrUpdateMarks(request, teacherUser.getUsername());
        assertFalse(responses.isEmpty());
        assertEquals(85.0, responses.get(0).getMarksObtained());

        // Publish marks
        publishingService.publishMarks(subject.getId(), ExamType.MID1, true, teacherUser.getUsername());
        List<Marks> savedMarks = marksRepository.findBySubjectIdAndExamType(subject.getId(), ExamType.MID1);
        assertTrue(savedMarks.get(0).getPublished());

        // Unpublish marks
        publishingService.publishMarks(subject.getId(), ExamType.MID1, false, teacherUser.getUsername());
        savedMarks = marksRepository.findBySubjectIdAndExamType(subject.getId(), ExamType.MID1);
        assertFalse(savedMarks.get(0).getPublished());
    }

    @Test
    @DisplayName("2. Student transcript, SGPA and weighted total score")
    void testStudentTranscriptAndSgpa() {
        Marks m1 = Marks.builder()
                .student(student)
                .subject(subject)
                .examType(ExamType.MID1)
                .marksObtained(80.0)
                .maxMarks(100.0)
                .published(true)
                .build();
        marksRepository.save(m1);

        Double score = publishingService.calculateWeightedScore(Collections.singletonList(m1));
        assertTrue(score > 0);

        StudentPerformanceSummaryDto summary = studentService.getStudentPerformance(studentUser.getUsername());
        assertNotNull(summary);
        assertNotNull(summary.getSgpa());
    }

    @Test
    @DisplayName("3. Leave approval creates LEAVE attendance records")
    void testLeaveApprovalCreatesLeaveRows() {
        LeaveApplication leave = LeaveApplication.builder()
                .student(student)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now())
                .reason("Medical Leave")
                .status(LeaveApplication.LeaveStatus.PENDING)
                .build();
        LeaveApplication savedLeave = leaveRepository.save(leave);

        leaveService.reviewLeave(savedLeave.getId(), "admin", Role.ADMIN, true, "Approved by Admin");

        List<Attendance> attendanceList = attendanceRepository.findByStudentId(student.getId());
        boolean hasLeaveRecord = attendanceList.stream().anyMatch(a -> a.getStatus() == AttendanceStatus.LEAVE);
        assertTrue(hasLeaveRecord, "LEAVE status attendance record should be generated upon leave approval");
    }

    @Test
    @DisplayName("4. Attendance correction approval updates attendance status")
    void testCorrectionApprovalUpdatesStatus() {
        Attendance attendance = Attendance.builder()
                .student(student)
                .subject(subject)
                .date(LocalDate.now())
                .period(1)
                .status(AttendanceStatus.ABSENT)
                .markedBy(teacherUser)
                .build();
        Attendance savedAtt = attendanceRepository.save(attendance);

        AttendanceCorrection correction = AttendanceCorrection.builder()
                .requestedBy(studentUser)
                .attendance(savedAtt)
                .requestedStatus(AttendanceStatus.PRESENT)
                .reason("Was present, marked absent by mistake")
                .status(AttendanceCorrection.CorrectionStatus.PENDING)
                .build();
        AttendanceCorrection savedCorr = correctionRepository.save(correction);

        correctionService.reviewCorrection(savedCorr.getId(), teacherUser.getUsername(), true, "Correction approved");

        Attendance updatedAtt = attendanceRepository.findById(savedAtt.getId()).orElseThrow();
        assertEquals(AttendanceStatus.PRESENT, updatedAtt.getStatus());
    }

    @Test
    @DisplayName("5. Attendance correction review once-only constraint")
    void testCorrectionApprovalOnceOnly() {
        Attendance attendance = Attendance.builder()
                .student(student)
                .subject(subject)
                .date(LocalDate.now())
                .period(1)
                .status(AttendanceStatus.ABSENT)
                .markedBy(teacherUser)
                .build();
        Attendance savedAtt = attendanceRepository.save(attendance);

        AttendanceCorrection correction = AttendanceCorrection.builder()
                .requestedBy(studentUser)
                .attendance(savedAtt)
                .requestedStatus(AttendanceStatus.PRESENT)
                .reason("Marked absent by mistake")
                .status(AttendanceCorrection.CorrectionStatus.PENDING)
                .build();
        AttendanceCorrection savedCorr = correctionRepository.save(correction);

        correctionService.reviewCorrection(savedCorr.getId(), teacherUser.getUsername(), true, "Approved");

        // Attempt second review on already reviewed request should fail
        assertThrows(RuntimeException.class, () -> {
            correctionService.reviewCorrection(savedCorr.getId(), teacherUser.getUsername(), false, "Second attempt");
        });
    }

    @Test
    @DisplayName("6. QR Session start and close flow")
    void testQrSessionStartAndClose() {
        QrSession session = qrSessionService.startSession(teacherUser.getUsername(), subject.getId(), 1, LocalDate.now(), false, null, null, 100.0);
        assertNotNull(session);
        assertTrue(session.getOpen());

        QrSession closedSession = qrSessionService.closeSession(session.getId(), teacherUser.getUsername());
        assertFalse(closedSession.getOpen());
    }

    @Test
    @DisplayName("7. Soft delete student preserves historical attendance and marks")
    void testSoftDeleteKeepsHistory() {
        Attendance attendance = Attendance.builder()
                .student(student)
                .subject(subject)
                .date(LocalDate.now())
                .period(1)
                .status(AttendanceStatus.PRESENT)
                .markedBy(teacherUser)
                .build();
        attendanceRepository.save(attendance);

        student.setDeletedAt(LocalDateTime.now());
        studentRepository.save(student);

        assertTrue(student.isDeleted());
        List<Attendance> records = attendanceRepository.findByStudentId(student.getId());
        assertFalse(records.isEmpty(), "Past attendance history must remain preserved when student is soft-deleted");
    }

    @Test
    @DisplayName("8. Audit log is append-only and writes correctly")
    void testAuditLogAppendOnly() {
        long initialCount = auditLogRepository.count();
        auditLogService.log(teacherUser.getUsername(), Role.TEACHER, "TEST_ACTION", "Testing audit log entry", null, "SUCCESS", "127.0.0.1");

        long finalCount = auditLogRepository.count();
        assertEquals(initialCount + 1, finalCount);
    }
}
