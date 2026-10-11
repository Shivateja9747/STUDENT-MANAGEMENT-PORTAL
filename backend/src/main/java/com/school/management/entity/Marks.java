package com.school.management.entity;

import jakarta.persistence.*;

@Entity
@Table(
    name = "marks",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_marks_student_exam", columnNames = {"student_id", "exam_id"})
    }
)
public class Marks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @Column(name = "marks_obtained", nullable = false)
    private Double marksObtained = 0.0;

    @Column(name = "is_absent", nullable = false)
    private Boolean isAbsent = false;

    @Column(length = 255)
    private String remarks;

    public Marks() {
    }

    public Marks(Long id, Student student, Exam exam, Double marksObtained, Boolean isAbsent, String remarks) {
        this.id = id;
        this.student = student;
        this.exam = exam;
        this.marksObtained = marksObtained != null ? marksObtained : 0.0;
        this.isAbsent = isAbsent != null ? isAbsent : false;
        this.remarks = remarks;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Exam getExam() {
        return exam;
    }

    public void setExam(Exam exam) {
        this.exam = exam;
    }

    public Double getMarksObtained() {
        return marksObtained;
    }

    public void setMarksObtained(Double marksObtained) {
        this.marksObtained = marksObtained != null ? marksObtained : 0.0;
    }

    public Boolean getIsAbsent() {
        return isAbsent;
    }

    public void setIsAbsent(Boolean isAbsent) {
        this.isAbsent = isAbsent != null ? isAbsent : false;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    // Helper accessors delegating to Exam entity
    public Subject getSubject() {
        return exam != null ? exam.getSubject() : null;
    }

    public ExamType getExamType() {
        return exam != null ? exam.getExamType() : null;
    }

    public Double getMaxMarks() {
        return exam != null ? exam.getMaxMarks() : 100.0;
    }

    public void setMaxMarks(Double maxMarks) {
        if (exam != null) {
            exam.setMaxMarks(maxMarks);
        }
    }

    public Boolean getPublished() {
        return exam != null ? exam.getPublished() : false;
    }

    public void setPublished(Boolean published) {
        if (exam != null) {
            exam.setPublished(published);
        }
    }

    public static MarksBuilder builder() {
        return new MarksBuilder();
    }

    public static class MarksBuilder {
        private Long id;
        private Student student;
        private Exam exam;
        private Subject subject;
        private ExamType examType;
        private Double marksObtained = 0.0;
        private Double maxMarks = 100.0;
        private Boolean isAbsent = false;
        private Boolean published = false;
        private String remarks;

        public MarksBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public MarksBuilder student(Student student) {
            this.student = student;
            return this;
        }

        public MarksBuilder exam(Exam exam) {
            this.exam = exam;
            return this;
        }

        public MarksBuilder subject(Subject subject) {
            this.subject = subject;
            return this;
        }

        public MarksBuilder examType(ExamType examType) {
            this.examType = examType;
            return this;
        }

        public MarksBuilder marksObtained(Double marksObtained) {
            this.marksObtained = marksObtained != null ? marksObtained : 0.0;
            return this;
        }

        public MarksBuilder maxMarks(Double maxMarks) {
            this.maxMarks = maxMarks;
            return this;
        }

        public MarksBuilder isAbsent(Boolean isAbsent) {
            this.isAbsent = isAbsent != null ? isAbsent : false;
            return this;
        }

        public MarksBuilder published(Boolean published) {
            this.published = published != null ? published : false;
            return this;
        }

        public MarksBuilder remarks(String remarks) {
            this.remarks = remarks;
            return this;
        }

        public Marks build() {
            if (exam == null && (subject != null || examType != null)) {
                exam = Exam.builder()
                        .subject(subject)
                        .examType(examType)
                        .maxMarks(maxMarks != null ? maxMarks : 100.0)
                        .published(published != null ? published : false)
                        .build();
            }
            return new Marks(id, student, exam, marksObtained, isAbsent, remarks);
        }
    }
}
