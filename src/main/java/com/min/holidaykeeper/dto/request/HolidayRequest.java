package com.min.holidaykeeper.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
public class HolidayRequest {

    private Integer holidayYear;

    private String countryCode;

    private LocalDate from;

    private LocalDate to;

    private String type;
}
