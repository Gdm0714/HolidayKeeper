package com.min.holidaykeeper.controller;

import com.min.holidaykeeper.entity.Holiday;
import com.min.holidaykeeper.service.HolidayService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HolidayController.class)
class HolidayControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HolidayService holidayService;

    @Test
    void searchHolidays_shouldReturnPagedResults() throws Exception {
        Holiday holiday = Holiday.builder()
                .id(1L)
                .date(LocalDate.of(2025, 1, 1))
                .localName("신정")
                .name("New Year's Day")
                .countryCode("KR")
                .holidayYear(2025)
                .fixed(true)
                .global(true)
                .build();

        Page<Holiday> holidayPage = new PageImpl<>(List.of(holiday), PageRequest.of(0, 20), 1);

        when(holidayService.searchHolidays(any(), any(), any(), any(), any(), any()))
                .thenReturn(holidayPage);

        mockMvc.perform(get("/api/holidays")
                        .param("holidayYear", "2025")
                        .param("countryCode", "KR")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("New Year's Day"))
                .andExpect(jsonPath("$.content[0].countryCode").value("KR"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void refreshHolidays_shouldReturn200() throws Exception {
        String countryCode = "KR";
        int year = 2025;

        doNothing().when(holidayService).refreshHolidaysByCountryAndYear(countryCode, year);

        mockMvc.perform(post("/api/holidays/refresh/{countryCode}/{year}", countryCode, year)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(holidayService).refreshHolidaysByCountryAndYear(countryCode, year);
    }

    @Test
    void deleteHolidays_shouldReturn200() throws Exception {
        String countryCode = "KR";
        int year = 2025;

        doNothing().when(holidayService).deleteHolidaysByCountryAndYear(countryCode, year);

        mockMvc.perform(delete("/api/holidays/{countryCode}/{year}", countryCode, year)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(holidayService).deleteHolidaysByCountryAndYear(countryCode, year);
    }
}
