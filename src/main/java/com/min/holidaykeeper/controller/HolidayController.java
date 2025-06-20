package com.min.holidaykeeper.controller;

import com.min.holidaykeeper.dto.request.HolidayRequest;
import com.min.holidaykeeper.dto.response.HolidayResponse;
import com.min.holidaykeeper.service.HolidayService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Holiday API", description = "공휴일 관리 API")
public class HolidayController {
    private final HolidayService holidayService;

    @GetMapping("/holidays")
    public ResponseEntity<Page<HolidayResponse>> searchCountries(@ParameterObject @ModelAttribute HolidayRequest request, @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        Page<HolidayResponse> holidays = holidayService.searchHolidays(
                request.getHolidayYear(),
                request.getCountryCode(),
                request.getFrom(),
                request.getTo(),
                request.getType(),
                pageable
        ).map(HolidayResponse::from);

        return ResponseEntity.ok(holidays);
    }
}