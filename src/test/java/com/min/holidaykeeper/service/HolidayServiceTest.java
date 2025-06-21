package com.min.holidaykeeper.service;

import com.min.holidaykeeper.api.NagerApiService;
import com.min.holidaykeeper.dto.HolidayDto;
import com.min.holidaykeeper.entity.Country;
import com.min.holidaykeeper.entity.Holiday;
import com.min.holidaykeeper.repository.HolidayRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HolidayServiceTest {
    
    @Mock
    private HolidayRepository holidayRepository;

    @Mock
    private NagerApiService nagerApiService;
    
    @InjectMocks
    private HolidayService holidayService;
    
    private Country testCountry;

    private HolidayDto holidayDto;
    
    @BeforeEach
    void setUp() {
        testCountry = Country.builder()
                .countryCode("KR")
                .name("South Korea")
                .build();
        
        holidayDto = new HolidayDto();
        holidayDto.setDate(LocalDate.of(2025, 1, 1).toString());
        holidayDto.setLocalName("신정");
        holidayDto.setName("New Year's Day");
        holidayDto.setCountryCode("KR");
        holidayDto.setFixed(true);
        holidayDto.setGlobal(true);
    }
    
    @Test
    void loadHolidaysForCountryAndYear_shouldSaveHolidays() {
        String countryCode = "KR";
        int holidayYear = 2025;
        List<HolidayDto> holidayDtos = List.of(holidayDto);
        
        when(holidayRepository.existsByCountryCodeAndHolidayYear(countryCode, holidayYear)).thenReturn(false);
        when(nagerApiService.getHolidays(countryCode, holidayYear)).thenReturn(holidayDtos);
        
        holidayService.getHolidayByCountryAndHolidayYear(countryCode, holidayYear);
        
        verify(holidayRepository).saveAll(anyList());
    }
    
    @Test
    void searchHolidays_shouldReturnPagedResults() {
        Pageable pageable = PageRequest.of(0, 10);
        Holiday holiday = Holiday.builder()
                .id(1L)
                .date(LocalDate.of(2025, 1, 1))
                .localName("신정")
                .name("New Year's Day")
                .countryCode("KR")
                .holidayYear(2025)
                .build();
        
        Page<Holiday> holidayPage = new PageImpl<>(List.of(holiday));
        
        when(holidayRepository.searchHolidays(any(), any()))
                .thenReturn(holidayPage);
        
        Page<Holiday> result = holidayService.searchHolidays(2025, "KR", null, null, null, pageable);
        
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("New Year's Day");
    }
    
    @Test
    void refreshHolidaysForCountryAndYear_shouldDeleteAndReload() {
        String countryCode = "KR";
        int holidayYear = 2025;
        List<HolidayDto> holidayDtos = List.of(holidayDto);
        
        when(nagerApiService.getHolidays(countryCode, holidayYear)).thenReturn(holidayDtos);
        
        holidayService.refreshHolidaysByCountryAndYear(countryCode, holidayYear);
        
        verify(holidayRepository).deleteByCountryCodeAndYear(countryCode, holidayYear);
        verify(holidayRepository).saveAll(anyList());
    }
}
