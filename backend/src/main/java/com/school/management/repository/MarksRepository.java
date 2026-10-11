package com.school.management.repository;

import com.school.management.entity.ExamType;
import com.school.management.entity.Marks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MarksRepository extends JpaRepository<Marks, Long> {

    @Query("SELECT m FROM Marks m WHERE m.student.id = :studentId AND m.exam.subject.id = :subjectId AND m.exam.examType = :examType")
    Optional<Marks> findByStudentIdAndSubjectIdAndExamType(@Param("studentId") Long studentId, @Param("subjectId") Long subjectId, @Param("examType") ExamType examType);

    @Query("SELECT m FROM Marks m WHERE m.exam.subject.id = :subjectId AND m.exam.examType = :examType")
    List<Marks> findBySubjectIdAndExamType(@Param("subjectId") Long subjectId, @Param("examType") ExamType examType);

    List<Marks> findByStudentId(Long studentId);

    @Query("SELECT m FROM Marks m WHERE m.student.id = :studentId AND m.exam.subject.id = :subjectId")
    List<Marks> findByStudentIdAndSubjectId(@Param("studentId") Long studentId, @Param("subjectId") Long subjectId);

    @Query("SELECT COUNT(m) > 0 FROM Marks m WHERE m.exam.subject.id = :subjectId")
    boolean existsBySubjectId(@Param("subjectId") Long subjectId);
}
