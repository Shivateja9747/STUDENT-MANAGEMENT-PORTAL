package com.school.management.repository;

import com.school.management.entity.TeacherSubjectAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherSubjectAssignmentRepository extends JpaRepository<TeacherSubjectAssignment, Long> {
    List<TeacherSubjectAssignment> findByTeacherId(Long teacherId);
    List<TeacherSubjectAssignment> findBySubjectId(Long subjectId);
    Optional<TeacherSubjectAssignment> findBySubjectIdAndSectionAndAcademicYearId(Long subjectId, String section, Long academicYearId);
}
