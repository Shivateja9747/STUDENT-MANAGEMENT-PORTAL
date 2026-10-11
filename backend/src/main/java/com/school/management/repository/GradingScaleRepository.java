package com.school.management.repository;

import com.school.management.entity.GradingScale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GradingScaleRepository extends JpaRepository<GradingScale, Long> {
    Optional<GradingScale> findByGrade(String grade);
}
