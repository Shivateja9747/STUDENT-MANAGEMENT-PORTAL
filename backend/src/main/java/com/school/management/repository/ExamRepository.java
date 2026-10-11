package com.school.management.repository;

import com.school.management.entity.Exam;
import com.school.management.entity.ExamType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
    Optional<Exam> findBySubjectIdAndAcademicYearIdAndExamType(Long subjectId, Long academicYearId, ExamType examType);
    List<Exam> findBySubjectIdAndAcademicYearId(Long subjectId, Long academicYearId);
    List<Exam> findBySubjectId(Long subjectId);
}
