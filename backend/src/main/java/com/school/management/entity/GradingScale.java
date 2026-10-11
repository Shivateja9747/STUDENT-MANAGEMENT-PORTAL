package com.school.management.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "grading_scale")
public class GradingScale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 10)
    private String grade;

    @Column(name = "min_percentage", nullable = false)
    private Double minPercentage;

    @Column(name = "max_percentage", nullable = false)
    private Double maxPercentage;

    @Column(name = "gpa_point", nullable = false)
    private Double gpaPoint;

    @Column(length = 255)
    private String description;

    public GradingScale() {
    }

    public GradingScale(Long id, String grade, Double minPercentage, Double maxPercentage, Double gpaPoint, String description) {
        this.id = id;
        this.grade = grade;
        this.minPercentage = minPercentage;
        this.maxPercentage = maxPercentage;
        this.gpaPoint = gpaPoint;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public Double getMinPercentage() {
        return minPercentage;
    }

    public void setMinPercentage(Double minPercentage) {
        this.minPercentage = minPercentage;
    }

    public Double getMaxPercentage() {
        return maxPercentage;
    }

    public void setMaxPercentage(Double maxPercentage) {
        this.maxPercentage = maxPercentage;
    }

    public Double getGpaPoint() {
        return gpaPoint;
    }

    public void setGpaPoint(Double gpaPoint) {
        this.gpaPoint = gpaPoint;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
