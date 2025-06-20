package com.min.holidaykeeper.repository;

import com.min.holidaykeeper.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, String> {
}
