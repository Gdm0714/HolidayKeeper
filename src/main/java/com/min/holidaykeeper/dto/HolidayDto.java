package com.min.holidaykeeper.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class HolidayDto {
    private LocalDate date;
    private String localName;
    private String name;
    private String countryCode;
    private boolean fixed;
    @JsonProperty("global")
    private boolean global;
    private String counties;
    private Integer launchYear;
    private List<String> types;
}
