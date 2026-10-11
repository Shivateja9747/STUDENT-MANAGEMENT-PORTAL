package com.school.management.repository;

import com.school.management.entity.AcademicCalendar;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AcademicCalendarRepository extends JpaRepository<AcademicCalendar, Long> {

    List<AcademicCalendar> findByDateBetween(LocalDate startDate, LocalDate endDate);

    Optional<AcademicCalendar> findByDateAndType(LocalDate date, AcademicCalendar.CalendarType type);

    boolean existsByDateAndType(LocalDate date, AcademicCalendar.CalendarType type);

    Page<AcademicCalendar> findByTitleContainingIgnoreCase(String query, Pageable pageable);
}
