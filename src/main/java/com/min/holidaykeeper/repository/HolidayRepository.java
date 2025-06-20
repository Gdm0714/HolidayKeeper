package com.min.holidaykeeper.repository;

import com.min.holidaykeeper.entity.Holiday;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface HolidayRepository extends JpaRepository<Holiday, Long> {
    @Query("SELECT h FROM Holiday h WHERE " +
           "(:holidayYear IS NULL OR h.holidayYear = :holidayYear) AND " +
           "(:countryCode IS NULL OR h.countryCode = :countryCode) AND " +
           "(:from IS NULL OR h.date >= :from) AND " +
           "(:to IS NULL OR h.date <= :to) AND " +
           "(:type IS NULL OR :type MEMBER OF h.types)")
    Page<Holiday> searchHolidays(@Param("holidayYear") Integer holidayYear,
                                @Param("countryCode") String countryCode,
                                @Param("from") LocalDate from,
                                @Param("to") LocalDate to,
                                @Param("type") String type,
                                Pageable pageable);
    
    @Modifying
    @Query("DELETE FROM Holiday h WHERE h.countryCode = :countryCode AND h.holidayYear = :holidayYear")
    void deleteByCountryCodeAndYear(@Param("countryCode") String countryCode, @Param("holidayYear") int holidayYear);

    boolean existsByCountryCodeAndHolidayYear(String countryCode, int year);
}
