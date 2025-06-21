package com.min.holidaykeeper.repository;

import com.min.holidaykeeper.entity.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface HolidayRepository extends JpaRepository<Holiday, Long>, HolidayCustomRepository {
    
    @Modifying
    @Query("DELETE FROM Holiday h WHERE h.countryCode = :countryCode AND h.holidayYear = :holidayYear")
    void deleteByCountryCodeAndYear(@Param("countryCode") String countryCode, @Param("holidayYear") int holidayYear);

    boolean existsByCountryCodeAndHolidayYear(String countryCode, int year);
}
