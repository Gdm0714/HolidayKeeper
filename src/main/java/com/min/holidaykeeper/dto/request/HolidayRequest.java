package com.min.holidaykeeper.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
public class HolidayRequest {

    @Schema(description = "연도", example = "2025")
    private Integer holidayYear;

    @Schema(description = "국가 코드", example = "KR")
    private String countryCode;

    @Schema(description = "시작일", example = "2025-01-01")
    private LocalDate from;

    @Schema(description = "종료일", example = "2025-12-31")
    private LocalDate to;

    @Schema(description = "공휴일 타입", example = "Public")
    private String type;
}
