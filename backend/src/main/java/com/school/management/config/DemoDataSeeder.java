package com.school.management.config;

import com.school.management.entity.*;
import com.school.management.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

@Component
@ConditionalOnProperty(name = "app.seed.demo-data", havingValue = "true", matchIfMissing = false)
public class DemoDataSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DemoDataSeeder.class);

    private final UserRepository userRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final AttendanceRepository attendanceRepository;
    private final MarksRepository marksRepository;
    private final PasswordEncoder passwordEncoder;

    public DemoDataSeeder(UserRepository userRepository,
                          TeacherRepository teacherRepository,
                          StudentRepository studentRepository,
                          SubjectRepository subjectRepository,
                          AttendanceRepository attendanceRepository,
                          MarksRepository marksRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
        this.subjectRepository = subjectRepository;
        this.attendanceRepository = attendanceRepository;
        this.marksRepository = marksRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.existsByUsername("cshod")) {
            logger.info("--> Demo data already seeded. Skipping DemoDataSeeder.");
            return;
        }

        logger.info("--> Seeding EduTrack Demo Staff, Subject & Student Data into Database...");
        List<String[]> seededCredentialsLog = new ArrayList<>();

        // 1. Seed Admin Account
        if (!userRepository.existsByUsername("admin")) {
            User adminUser = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .email("admin@edutrack.com")
                    .phone("9876599999")
                    .mustChangePassword(false)
                    .build();
            userRepository.save(adminUser);
        }
        seededCredentialsLog.add(new String[]{"admin", "admin123", "ADMIN", "System Administrator"});

        // 2. Seed HOD Accounts (cshod & hod_cse)
        User cshodUser = User.builder()
                .username("cshod")
                .password(passwordEncoder.encode("cshod123"))
                .role(Role.HOD)
                .email("hod.cs@edutrack.com")
                .phone("9876500000")
                .mustChangePassword(false)
                .build();

        Teacher cshodTeacher = Teacher.builder()
                .user(cshodUser)
                .name("Dr. HOD Computer Science")
                .department("Computer Science")
                .build();
        teacherRepository.save(cshodTeacher);
        seededCredentialsLog.add(new String[]{"cshod", "cshod123", "HOD", "Computer Science HOD"});

        User hodCseUser = User.builder()
                .username("hod_cse")
                .password(passwordEncoder.encode("Demo@12345"))
                .role(Role.HOD)
                .email("hod.cse@edutrack.com")
                .phone("9876500001")
                .mustChangePassword(false)
                .build();

        Teacher hodCseTeacher = Teacher.builder()
                .user(hodCseUser)
                .name("Dr. CSE Department Head")
                .department("Computer Science")
                .build();
        teacherRepository.save(hodCseTeacher);

        // 3. Seed Default Primary Teacher (teacher1 / teacher123)
        User teacher1User = User.builder()
                .username("teacher1")
                .password(passwordEncoder.encode("teacher123"))
                .role(Role.TEACHER)
                .email("teacher1@edutrack.com")
                .phone("9876511111")
                .mustChangePassword(false)
                .build();
        Teacher teacher1 = Teacher.builder()
                .user(teacher1User)
                .name("Prof. Teacher One")
                .department("Computer Science")
                .build();
        teacherRepository.save(teacher1);
        seededCredentialsLog.add(new String[]{"teacher1", "teacher123", "TEACHER", "Computer Science"});

        // 4. Seed Faculty Members from Curriculum Table
        String[][] facultyData = {
                {"prof_shirisha", "Dr. K. Shirisha Reddy", "Computer Science"},
                {"prof_divya", "Mrs. Divya", "Computer Science"},
                {"prof_aparna", "Mrs. M. Aparna", "Computer Science"},
                {"prof_prashanth", "Mr. T. Prashanth", "Computer Science"},
                {"prof_lalbahadur", "Mr. K. Lalbahadur", "Computer Science"},
                {"prof_shivarama", "Mr. A. Shivarama Krishna", "Computer Science"},
                {"prof_jyothi", "Mrs. Jyothi Reddy", "Humanities & Sciences"},
                {"prof_anitha", "Mrs. B. Anitha", "Computer Science"}
        };

        Map<String, Teacher> teacherMap = new HashMap<>();

        for (String[] fd : facultyData) {
            String username = fd[0];
            String name = fd[1];
            String dept = fd[2];

            User tUser = User.builder()
                    .username(username)
                    .password(passwordEncoder.encode("Demo@12345"))
                    .role(Role.TEACHER)
                    .email(username + "@edutrack.com")
                    .phone("9876511" + (10 + teacherMap.size()))
                    .mustChangePassword(false)
                    .build();
            Teacher t = Teacher.builder()
                    .user(tUser)
                    .name(name)
                    .department(dept)
                    .build();
            Teacher saved = teacherRepository.save(t);
            teacherMap.put(name, saved);
            seededCredentialsLog.add(new String[]{username, "Demo@12345", "TEACHER", name + " (" + dept + ")"});
        }

        // 5. Seed Subjects mapped to actual Faculty
        Object[][] subjectMappings = {
                {"CS601-DAA", "Design and Analysis of Algorithms(DAA)", "Dr. K. Shirisha Reddy"},
                {"CS602-ML", "Machine Learning(ML)", "Mrs. Divya"},
                {"CS603-CN", "Computer Networks(CN)", "Mrs. M. Aparna"},
                {"CS604-BEFA", "Business Economics & Financial Analysis(BEFA)", "Mr. T. Prashanth"},
                {"CS605-IDS", "Introduction to Data Science(IDS)", "Mr. K. Lalbahadur"},
                {"CS606-MLLAB", "Machine Learning Lab(ML LAB)", "Mrs. Divya"},
                {"CS607-CNLAB", "Computer Networks Lab(CN LAB)", "Mrs. M. Aparna"},
                {"CS608-AECSLAB", "Advanced English Communication Skills Lab(AECS Lab)", "Mrs. Jyothi Reddy"},
                {"CS609-FLUTTER", "Skill Development Course (UI-design-Flutter-Lab)", "Mrs. B. Anitha"}
        };

        List<Subject> cseSubjects = new ArrayList<>();
        for (Object[] sm : subjectMappings) {
            String code = (String) sm[0];
            String name = (String) sm[1];
            String teacherName = (String) sm[2];
            Teacher assignedTeacher = teacherMap.getOrDefault(teacherName, teacher1);

            Subject subject = Subject.builder()
                    .code(code)
                    .name(name)
                    .semester(6)
                    .teacher(assignedTeacher)
                    .build();
            cseSubjects.add(subjectRepository.save(subject));
        }

        // 6. Seed Primary Student (student1 / student123 -> Shiva Kumar)
        User student1User = User.builder()
                .username("student1")
                .password(passwordEncoder.encode("student123"))
                .role(Role.STUDENT)
                .email("shiva@edutrack.com")
                .phone("9876522000")
                .mustChangePassword(false)
                .build();

        Student student1 = Student.builder()
                .user(student1User)
                .rollNo("CS6A01")
                .name("Shiva Kumar")
                .department("Computer Science")
                .semester(6)
                .section("A")
                .build();
        Student savedStudent1 = studentRepository.save(student1);
        seededCredentialsLog.add(new String[]{"student1", "student123", "STUDENT", "Sec A - Shiva Kumar"});

        // 7. Seed Requested Student List
        String[] studentNames = {
                "Shiva", "Nithin", "Harshith", "Sai", "Yeshwanth",
                "Yasaswini", "Lucky", "Shailesh", "Karthik", "Ananya Patel",
                "Rohan Verma", "Ishita Gupta", "Aditi Rao", "Kavya Reddy",
                "Devansh Malhotra", "Pooja Deshmukh", "Tarun Choudhury", "Sneha Iyer",
                "Varun Mehta", "Pranav Das"
        };

        List<Student> seededStudents = new ArrayList<>();
        seededStudents.add(savedStudent1);

        for (int i = 0; i < studentNames.length; i++) {
            String section = (i < 10) ? "A" : "B";
            String rollNo = String.format("CS6%s%02d", section, i + 1);
            String username = studentNames[i].toLowerCase().replaceAll("\\s+", "");

            if (userRepository.existsByUsername(username)) {
                username = username + (i + 1);
            }

            User sUser = User.builder()
                    .username(username)
                    .password(passwordEncoder.encode("Demo@12345"))
                    .role(Role.STUDENT)
                    .email(username + "@edutrack.com")
                    .phone("9876522" + String.format("%02d", i + 1))
                    .mustChangePassword(false)
                    .build();

            Student st = Student.builder()
                    .user(sUser)
                    .rollNo(rollNo)
                    .name(studentNames[i])
                    .department("Computer Science")
                    .semester(6)
                    .section(section)
                    .build();

            seededStudents.add(studentRepository.save(st));
            if (i < 10) {
                seededCredentialsLog.add(new String[]{username, "Demo@12345", "STUDENT", "Sec " + section + " - " + studentNames[i]});
            }
        }

        // 8. Seed Attendance Records across past 25 Working Days
        List<LocalDate> workingDays = new ArrayList<>();
        LocalDate curDate = LocalDate.now().minusDays(1);
        while (workingDays.size() < 25) {
            if (curDate.getDayOfWeek() != DayOfWeek.SATURDAY && curDate.getDayOfWeek() != DayOfWeek.SUNDAY) {
                workingDays.add(curDate);
            }
            curDate = curDate.minusDays(1);
        }

        Random rand = new Random(42);
        for (int i = 0; i < seededStudents.size(); i++) {
            Student student = seededStudents.get(i);
            double targetAttRate = (i == 0) ? 0.92 : (i < 15 ? 0.85 : 0.65);

            for (Subject subject : cseSubjects) {
                for (LocalDate date : workingDays) {
                    AttendanceStatus status;
                    String remark = null;
                    double p = rand.nextDouble();
                    if (p < targetAttRate) {
                        status = (rand.nextDouble() < 0.05) ? AttendanceStatus.LATE : AttendanceStatus.PRESENT;
                    } else if (p < targetAttRate + 0.05) {
                        status = AttendanceStatus.LEAVE;
                        remark = "Medical leave";
                    } else {
                        status = AttendanceStatus.ABSENT;
                    }

                    Attendance att = Attendance.builder()
                            .student(student)
                            .subject(subject)
                            .date(date)
                            .period(1)
                            .status(status)
                            .remark(remark)
                            .build();

                    attendanceRepository.save(att);
                }
            }
        }

        // 9. Seed Marks Records
        for (int i = 0; i < seededStudents.size(); i++) {
            Student student = seededStudents.get(i);
            for (Subject subject : cseSubjects) {
                double score = 70.0 + rand.nextInt(26);
                Marks marks = Marks.builder()
                        .student(student)
                        .subject(subject)
                        .examType(ExamType.MID1)
                        .marksObtained(score)
                        .maxMarks(100.0)
                        .published(true)
                        .isAbsent(false)
                        .build();
                marksRepository.save(marks);
            }
        }

        logger.info("==========================================================================");
        logger.info("                EDUTRACK DEMO SEEDED FACULTY & STUDENTS                   ");
        logger.info("==========================================================================");
        logger.info(String.format("%-16s | %-16s | %-12s | %-30s", "USERNAME", "PASSWORD", "ROLE", "DETAILS"));
        logger.info("--------------------------------------------------------------------------");
        for (String[] row : seededCredentialsLog) {
            logger.info(String.format("%-16s | %-16s | %-12s | %-30s", row[0], row[1], row[2], row[3]));
        }
        logger.info("--------------------------------------------------------------------------");
    }
}
