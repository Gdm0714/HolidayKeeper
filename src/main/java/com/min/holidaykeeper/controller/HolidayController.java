package com.min.holidaykeeper.controller;

import com.min.holidaykeeper.dto.request.HolidayRequest;
import com.min.holidaykeeper.dto.response.HolidayResponse;
import com.min.holidaykeeper.service.HolidayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/holidays")
@RequiredArgsConstructor
@Tag(name = "Holiday API", description = "공휴일 관리 API")
public class HolidayController {
    private final HolidayService holidayService;

    @GetMapping
    @Operation(summary = "2. 검색 - 연도, 국가, from-to기간, 공휴일 타입 필터 기반 공휴일 조회")
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

    @PostMapping("/refresh/{countryCode}/{year}")
    @Operation(summary = "3. 재동기화(Refresh) - 특정 연도, 국가 데이터를 재호출하여 Upsert")
    public ResponseEntity<String> refreshHolidays(
            @Parameter(description = "국가 코드", example = "KR") @PathVariable String countryCode,
            @Parameter(description = "연도", example = "2025") @PathVariable int year) {

        holidayService.refreshHolidaysByCountryAndYear(countryCode, year);

        return ResponseEntity.ok(String.format("%s %d년 공휴일 데이터를 재동기화했습니다.", countryCode, year));
    }

    @DeleteMapping("/{countryCode}/{year}")
    @Operation(summary = "4. 삭제 - 특정 연도, 국가의 공휴일 레코드 전체 삭제")
    public ResponseEntity<String> deleteHolidays(
            @Parameter(description = "국가 코드", example = "KR") @PathVariable String countryCode,
            @Parameter(description = "연도", example = "2025") @PathVariable int year) {

        holidayService.deleteHolidaysByCountryAndYear(countryCode, year);

        return ResponseEntity.ok(String.format("%s %d년 공휴일 데이터를 삭제했습니다.", countryCode, year));
    }
}