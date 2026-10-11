package com.school.management.repository;

import com.school.management.entity.Timetable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;

@Repository
public interface TimetableRepository extends JpaRepository<Timetable, Long> {

    List<Timetable> findByTeacherIdAndDayOfWeek(Long teacherId, DayOfWeek dayOfWeek);

    List<Timetable> findByTeacherId(Long teacherId);

    List<Timetable> findBySubjectId(Long subjectId);

    List<Timetable> findBySection(String section);

    List<Timetable> findByDayOfWeekAndSection(DayOfWeek dayOfWeek, String section);

    Page<Timetable> findBySectionContainingIgnoreCaseOrRoomContainingIgnoreCase(String section, String room, Pageable pageable);
}
