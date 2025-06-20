package com.min.holidaykeeper.service;

import com.min.holidaykeeper.api.NagerApiService;
import com.min.holidaykeeper.dto.CountryDto;
import com.min.holidaykeeper.dto.HolidayDto;
import com.min.holidaykeeper.entity.Country;
import com.min.holidaykeeper.entity.Holiday;
import com.min.holidaykeeper.repository.CountryRepository;
import com.min.holidaykeeper.repository.HolidayRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Transactional
@RequiredArgsConstructor
public class HolidayService {
    private final CountryRepository countryRepository;
    private final HolidayRepository holidayRepository;
    private final NagerApiService nagerApiService;

    private static final int START_YEAR = 2023;
    private static final int END_YEAR = 2024;
    private static final String[] INITIAL_COUNTRIES = {"KR", "US", "JP", "CN", "DE", "FR", "GB", "CA", "AU", "BR"};

    private final AtomicBoolean initialized = new AtomicBoolean(false);

    @PostConstruct
    public void initializeData() {
        CompletableFuture.runAsync(() -> {
            initializeCountries();
            getHolidaysByHolidayYear();
        });
    }

    @Transactional(readOnly = true)
    public Page<Holiday> searchHolidays(Integer year, String countryCode,
                                        LocalDate fromDate, LocalDate toDate,
                                        String type, Pageable pageable) {
        return holidayRepository.searchHolidays(year, countryCode, fromDate, toDate, type, pageable);
    }

    private void initializeCountries() {
        List<CountryDto> countries = nagerApiService.getCountries();

        List<Country> countryList = countries.stream()
                .map(dto -> Country.builder()
                        .countryCode(dto.getCountryCode())
                        .name(dto.getName())
                        .build())
                .toList();

        countryRepository.saveAll(countryList);
    }

    private void getHolidaysByHolidayYear() {
        List<Country> countries = countryRepository.findAll();

        List<Country> initialCountries = countries.stream()
                .filter(country -> List.of(INITIAL_COUNTRIES).contains(country.getCountryCode()))
                .toList();


        for(int holidayYear = START_YEAR; holidayYear <= END_YEAR; holidayYear++) {
            for(Country c : initialCountries) {
                    getHolidayByCountryAndHolidayYear(c.getCountryCode(), holidayYear);
            }
        }
    }

    public void getHolidayByCountryAndHolidayYear(String countryCode, int holidayYear) {
        if(holidayRepository.existsByCountryCodeAndHolidayYear(countryCode, holidayYear)) {
            return;
        }

        List<HolidayDto> holidays = nagerApiService.getHolidays(countryCode, holidayYear);
        List<Holiday> holidayList = holidays.stream()
                .map(dto -> dtoToEntity(dto, holidayYear))
                .toList();

        holidayRepository.saveAll(holidayList);
    }

    private Holiday dtoToEntity(HolidayDto dto, int holidayYear) {
        Holiday holiday = Holiday.builder()
                .date(dto.getDateAsLocalDate())
                .localName(dto.getLocalName())
                .name(dto.getName())
                .countryCode(dto.getCountryCode())
                .fixed(dto.isFixed())
                .global(dto.isGlobal())
                .counties(dto.getCounties() != null ? String.join(",", dto.getCounties()) : null)
                .launchYear(dto.getLaunchYear())
                .holidayYear(holidayYear)
                .types(dto.getTypes() != null ? new HashSet<>(dto.getTypes()) : new HashSet<>())
                .build();

        return holiday;
    }
}
