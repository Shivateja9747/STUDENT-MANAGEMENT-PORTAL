package com.school.management.config;

import com.school.management.entity.*;
import com.school.management.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final AttendanceRepository attendanceRepository;
    private final MarksRepository marksRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
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
        logger.info("--> Initializing EduTrack Data (Faculty, Curriculum & Students)...");

        // 1. Seed Admin
        if (!userRepository.existsByUsername("admin")) {
            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .email("admin@edutrack.edu")
                    .phone("1234567890")
                    .mustChangePassword(false)
                    .build();
            userRepository.save(admin);
            logger.info("--> Seeded default admin user 'admin'");
        }

        // 1b. Seed Default Demo Teacher (teacher1 / teacher123)
        if (!userRepository.existsByUsername("teacher1")) {
            User tUser = User.builder()
                    .username("teacher1")
                    .password(passwordEncoder.encode("teacher123"))
                    .role(Role.TEACHER)
                    .email("teacher1@edutrack.edu")
                    .phone("9876543210")
                    .mustChangePassword(false)
                    .build();

            Teacher teacher1 = Teacher.builder()
                    .user(tUser)
                    .name("Dr. K. Shirisha Reddy")
                    .department("Computer Science")
                    .build();
            teacherRepository.save(teacher1);
            logger.info("--> Seeded default teacher1 user 'teacher1'");
        }

        // 1c. Seed Default Demo Student (student1 / student123)
        if (!userRepository.existsByUsername("student1") && !studentRepository.existsByRollNo("CS6A00")) {
            User sUser = User.builder()
                    .username("student1")
                    .password(passwordEncoder.encode("student123"))
                    .role(Role.STUDENT)
                    .email("student1@edutrack.edu")
                    .phone("9876543211")
                    .mustChangePassword(false)
                    .build();

            Student student1 = Student.builder()
                    .user(sUser)
                    .rollNo("CS6A00")
                    .name("Shiva (Sample Student)")
                    .department("Computer Science")
                    .semester(6)
                    .section("A")
                    .build();
            studentRepository.save(student1);
            logger.info("--> Seeded default student1 user 'student1'");
        }

        // 2. Seed Faculty Members from Curriculum
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

            Teacher teacher;
            if (!userRepository.existsByUsername(username)) {
                User tUser = User.builder()
                        .username(username)
                        .password(passwordEncoder.encode("Demo@12345"))
                        .role(Role.TEACHER)
                        .email(username + "@edutrack.edu")
                        .phone("9876511" + (10 + teacherMap.size()))
                        .mustChangePassword(false)
                        .build();

                teacher = Teacher.builder()
                        .user(tUser)
                        .name(name)
                        .department(dept)
                        .build();
                teacher = teacherRepository.save(teacher);
                logger.info("--> Seeded faculty: " + name);
            } else {
                teacher = teacherRepository.findByUserUsername(username).orElse(null);
            }
            if (teacher != null) {
                teacherMap.put(name, teacher);
            }
        }

        // 3. Seed Subjects & Map to Curriculum Faculty
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

        List<Subject> seededSubjects = new ArrayList<>();
        Teacher defaultTeacher = teacherMap.values().stream().findFirst().orElse(null);

        for (Object[] sm : subjectMappings) {
            String code = (String) sm[0];
            String name = (String) sm[1];
            String teacherName = (String) sm[2];
            Teacher assignedTeacher = teacherMap.getOrDefault(teacherName, defaultTeacher);

            if (!subjectRepository.existsByCode(code)) {
                Subject subject = Subject.builder()
                        .code(code)
                        .name(name)
                        .semester(6)
                        .teacher(assignedTeacher)
                        .build();
                seededSubjects.add(subjectRepository.save(subject));
                logger.info("--> Seeded subject: " + name + " assigned to " + teacherName);
            } else {
                subjectRepository.findByCode(code).ifPresent(seededSubjects::add);
            }
        }

        // 4. Seed Requested Student List
        String[] studentNames = {
                "Shiva", "Nithin", "Harshith", "Sai", "Yeshwanth",
                "Yasaswini", "Lucky", "Shailesh", "Karthik", "Ananya Patel",
                "Rohan Verma", "Ishita Gupta", "Aditi Rao", "Kavya Reddy",
                "Devansh Malhotra", "Pooja Deshmukh", "Tarun Choudhury", "Sneha Iyer",
                "Varun Mehta", "Pranav Das"
        };

        List<Student> seededStudents = new ArrayList<>();

        for (int i = 0; i < studentNames.length; i++) {
            String name = studentNames[i];
            String section = (i < 10) ? "A" : "B";
            String rollNo = String.format("CS6%s%02d", section, i + 1);
            String username = name.toLowerCase().replaceAll("\\s+", "");

            if (!studentRepository.existsByRollNo(rollNo)) {
                if (userRepository.existsByUsername(username)) {
                    username = username + (i + 1);
                }

                User sUser = User.builder()
                        .username(username)
                        .password(passwordEncoder.encode("Demo@12345"))
                        .role(Role.STUDENT)
                        .email(username + "@edutrack.edu")
                        .phone("9876522" + String.format("%02d", i + 1))
                        .mustChangePassword(false)
                        .build();

                Student st = Student.builder()
                        .user(sUser)
                        .rollNo(rollNo)
                        .name(name)
                        .department("Computer Science")
                        .semester(6)
                        .section(section)
                        .build();

                seededStudents.add(studentRepository.save(st));
                logger.info("--> Seeded student: " + name + " (" + rollNo + ")");
            } else {
                studentRepository.findByRollNo(rollNo).ifPresent(seededStudents::add);
            }
        }

        // 5. Seed Attendance Records for Seeded Students if none exist
        if (attendanceRepository.count() == 0 && !seededStudents.isEmpty() && !seededSubjects.isEmpty()) {
            List<LocalDate> workingDays = new ArrayList<>();
            LocalDate curDate = LocalDate.now().minusDays(1);
            while (workingDays.size() < 20) {
                if (curDate.getDayOfWeek() != DayOfWeek.SATURDAY && curDate.getDayOfWeek() != DayOfWeek.SUNDAY) {
                    workingDays.add(curDate);
                }
                curDate = curDate.minusDays(1);
            }

            Random rand = new Random(42);
            for (int i = 0; i < seededStudents.size(); i++) {
                Student student = seededStudents.get(i);
                double targetAttRate = (i == 0) ? 0.95 : (i < 10 ? 0.88 : 0.75);

                for (Subject subject : seededSubjects) {
                    for (LocalDate date : workingDays) {
                        AttendanceStatus status;
                        String remark = null;
                        double p = rand.nextDouble();
                        if (p < targetAttRate) {
                            status = (rand.nextDouble() < 0.05) ? AttendanceStatus.LATE : AttendanceStatus.PRESENT;
                        } else if (p < targetAttRate + 0.05) {
                            status = AttendanceStatus.LEAVE;
                            remark = "Leave request approved";
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
            logger.info("--> Seeded attendance history for students.");
        }

        // 6. Seed Marks Records for Seeded Students if none exist
        if (marksRepository.count() == 0 && !seededStudents.isEmpty() && !seededSubjects.isEmpty()) {
            Random rand = new Random(42);
            for (Student student : seededStudents) {
                for (Subject subject : seededSubjects) {
                    double score = 72.0 + rand.nextInt(25);
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
            logger.info("--> Seeded marks records for students.");
        }

        logger.info("--> EduTrack Data Initialization Complete!");
    }
}
