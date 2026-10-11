package com.school.management.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "exams",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_exams_subject_year_type", columnNames = {"subject_id", "academic_year_id", "exam_type"})
    }
)
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_year_id", nullable = true)
    private AcademicYear academicYear;

    @Enumerated(EnumType.STRING)
    @Column(name = "exam_type", nullable = false, length = 50)
    private ExamType examType;

    @Column(name = "max_marks", nullable = false)
    private Double maxMarks = 100.0;

    @Column(nullable = false)
    private Boolean published = false;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    public Exam() {
    }

    public Exam(Long id, Subject subject, AcademicYear academicYear, ExamType examType, Double maxMarks, Boolean published, LocalDateTime publishedAt) {
        this.id = id;
        this.subject = subject;
        this.academicYear = academicYear;
        this.examType = examType;
        this.maxMarks = maxMarks != null ? maxMarks : 100.0;
        this.published = published != null ? published : false;
        this.publishedAt = publishedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public AcademicYear getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(AcademicYear academicYear) {
        this.academicYear = academicYear;
    }

    public ExamType getExamType() {
        return examType;
    }

    public void setExamType(ExamType examType) {
        this.examType = examType;
    }

    public Double getMaxMarks() {
        return maxMarks;
    }

    public void setMaxMarks(Double maxMarks) {
        this.maxMarks = maxMarks != null ? maxMarks : 100.0;
    }

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published != null ? published : false;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    public static ExamBuilder builder() {
        return new ExamBuilder();
    }

    public static class ExamBuilder {
        private Long id;
        private Subject subject;
        private AcademicYear academicYear;
        private ExamType examType;
        private Double maxMarks = 100.0;
        private Boolean published = false;
        private LocalDateTime publishedAt;

        public ExamBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ExamBuilder subject(Subject subject) {
            this.subject = subject;
            return this;
        }

        public ExamBuilder academicYear(AcademicYear academicYear) {
            this.academicYear = academicYear;
            return this;
        }

        public ExamBuilder examType(ExamType examType) {
            this.examType = examType;
            return this;
        }

        public ExamBuilder maxMarks(Double maxMarks) {
            this.maxMarks = maxMarks != null ? maxMarks : 100.0;
            return this;
        }

        public ExamBuilder published(Boolean published) {
            this.published = published != null ? published : false;
            return this;
        }

        public ExamBuilder publishedAt(LocalDateTime publishedAt) {
            this.publishedAt = publishedAt;
            return this;
        }

        public Exam build() {
            return new Exam(id, subject, academicYear, examType, maxMarks, published, publishedAt);
        }
    }
}
