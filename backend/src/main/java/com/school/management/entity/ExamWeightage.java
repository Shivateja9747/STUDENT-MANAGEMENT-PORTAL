package com.school.management.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "exam_weightages")
public class ExamWeightage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private ExamType examType;

    @Column(name = "weightage_percent", nullable = false)
    private Double weightagePercentage;

    public ExamWeightage() {
    }

    public ExamWeightage(Long id, ExamType examType, Double weightagePercentage) {
        this.id = id;
        this.examType = examType;
        this.weightagePercentage = weightagePercentage;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ExamType getExamType() {
        return examType;
    }

    public void setExamType(ExamType examType) {
        this.examType = examType;
    }

    public Double getWeightagePercentage() {
        return weightagePercentage;
    }

    public void setWeightagePercentage(Double weightagePercentage) {
        this.weightagePercentage = weightagePercentage;
    }

    public static ExamWeightageBuilder builder() {
        return new ExamWeightageBuilder();
    }

    public static class ExamWeightageBuilder {
        private Long id;
        private ExamType examType;
        private Double weightagePercentage;

        public ExamWeightageBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ExamWeightageBuilder examType(ExamType examType) {
            this.examType = examType;
            return this;
        }

        public ExamWeightageBuilder weightagePercentage(Double weightagePercentage) {
            this.weightagePercentage = weightagePercentage;
            return this;
        }

        public ExamWeightage build() {
            return new ExamWeightage(id, examType, weightagePercentage);
        }
    }
}
