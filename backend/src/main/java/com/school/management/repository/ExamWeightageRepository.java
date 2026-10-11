package com.school.management.repository;

import com.school.management.entity.ExamType;
import com.school.management.entity.ExamWeightage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExamWeightageRepository extends JpaRepository<ExamWeightage, Long> {

    Optional<ExamWeightage> findByExamType(ExamType examType);
}
