package com.school.management.repository;

import com.school.management.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByUserIdAndDeletedAtIsNull(Long userId);
    default Optional<Student> findByUserId(Long userId) { return findByUserIdAndDeletedAtIsNull(userId); }

    Optional<Student> findByUserUsernameAndDeletedAtIsNull(String username);
    default Optional<Student> findByUserUsername(String username) { return findByUserUsernameAndDeletedAtIsNull(username); }

    Optional<Student> findByRollNoAndDeletedAtIsNull(String rollNo);
    default Optional<Student> findByRollNo(String rollNo) { return findByRollNoAndDeletedAtIsNull(rollNo); }

    Boolean existsByRollNo(String rollNo);

    List<Student> findBySemesterAndDeletedAtIsNull(Integer semester);
    default List<Student> findBySemester(Integer semester) { return findBySemesterAndDeletedAtIsNull(semester); }

    List<Student> findByDepartmentAndSemesterAndDeletedAtIsNull(String department, Integer semester);
    default List<Student> findByDepartmentAndSemester(String department, Integer semester) { return findByDepartmentAndSemesterAndDeletedAtIsNull(department, semester); }

    Page<Student> findByDepartmentAndDeletedAtIsNull(String department, Pageable pageable);
    default Page<Student> findByDepartment(String department, Pageable pageable) { return findByDepartmentAndDeletedAtIsNull(department, pageable); }

    Page<Student> findByDepartmentAndSectionAndDeletedAtIsNull(String department, String section, Pageable pageable);
    default Page<Student> findByDepartmentAndSection(String department, String section, Pageable pageable) { return findByDepartmentAndSectionAndDeletedAtIsNull(department, section, pageable); }

    Page<Student> findBySectionAndDeletedAtIsNull(String section, Pageable pageable);
    default Page<Student> findBySection(String section, Pageable pageable) { return findBySectionAndDeletedAtIsNull(section, pageable); }

    Page<Student> findByNameContainingIgnoreCaseOrRollNoContainingIgnoreCaseOrDepartmentContainingIgnoreCase(String name, String rollNo, String department, Pageable pageable);
    Page<Student> findByDepartmentAndNameContainingIgnoreCaseOrDepartmentAndRollNoContainingIgnoreCase(String dept1, String name, String dept2, String rollNo, Pageable pageable);
}
