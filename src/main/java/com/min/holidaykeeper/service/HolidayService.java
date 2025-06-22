package com.min.holidaykeeper.service;

import com.min.holidaykeeper.api.NagerApiService;
import com.min.holidaykeeper.dto.CountryDto;
import com.min.holidaykeeper.dto.HolidayDto;
import com.min.holidaykeeper.dto.request.HolidayRequest;
import com.min.holidaykeeper.entity.Country;
import com.min.holidaykeeper.entity.Holiday;
import com.min.holidaykeeper.exception.BusinessException;
import com.min.holidaykeeper.exception.HolidayErrorCode;
import com.min.holidaykeeper.repository.CountryRepository;
import com.min.holidaykeeper.repository.HolidayRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class HolidayService {
    private final CountryRepository countryRepository;
    private final HolidayRepository holidayRepository;
    private final NagerApiService nagerApiService;

    private static final int START_YEAR = 2020;
    private static final int END_YEAR = 2025;
    private static final String[] INITIAL_COUNTRIES = {"KR", "US", "JP", "CN", "DE", "FR", "GB", "CA", "AU", "BR"};

    // 1. 데이터 적재
    @PostConstruct
    public void initializeData() {
        CompletableFuture.runAsync(() -> {
            initializeCountries();
            getHolidaysByHolidayYear();
        });
    }

    // 2. 검색
    @Transactional(readOnly = true)
    public Page<Holiday> searchHolidays(Integer holidayYear, String countryCode,
                                        LocalDate from, LocalDate to,
                                        String type, Pageable pageable) {
        HolidayRequest request = new HolidayRequest();
        request.setHolidayYear(holidayYear);
        request.setCountryCode(countryCode);
        request.setFrom(from);
        request.setTo(to);
        request.setType(type);
        
        return holidayRepository.searchHolidays(request, pageable);
    }

    // 3. 재동기화(Refresh)
    public void refreshHolidaysByCountryAndYear(String countryCode, int holidayYear) {
        holidayRepository.deleteByCountryCodeAndYear(countryCode, holidayYear);

        List<HolidayDto> holidays = nagerApiService.getHolidays(countryCode, holidayYear);
        List<Holiday> holidayList = holidays.stream()
                .map(dto -> dtoToEntity(dto, holidayYear))
                .toList();
        holidayRepository.saveAll(holidayList);

        log.info("{} {}년 공휴일 데이터를 재동기화 했습니다.", countryCode, holidayYear);
    }

    // 4. 삭제
    public void deleteHolidaysByCountryAndYear(String countryCode, int year) {

        int deletedCount = holidayRepository.deleteByCountryCodeAndYear(countryCode, year);
        if (deletedCount == 0) {
            throw new BusinessException(HolidayErrorCode.HOLIDAY_NOT_FOUND,
                    String.format("%s %d년 공휴일 데이터가 존재하지 않습니다.", countryCode, year));
        }

        log.info("{} {}년 공휴일 데이터를 삭제 했습니다.", countryCode, year);
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


        for (int holidayYear = START_YEAR; holidayYear <= END_YEAR; holidayYear++) {
            for (Country c : initialCountries) {
                getHolidayByCountryAndHolidayYear(c.getCountryCode(), holidayYear);
            }
        }
    }

    public void getHolidayByCountryAndHolidayYear(String countryCode, int holidayYear) {
        if (holidayRepository.existsByCountryCodeAndHolidayYear(countryCode, holidayYear)) {
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
