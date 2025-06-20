package com.min.holidaykeeper.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class HolidayDto {
    private String date;
    private String localName;
    private String name;
    private String countryCode;
    private boolean fixed;
    @JsonProperty("global")
    private boolean global;
    private List<String> counties;
    private Integer launchYear;
    private List<String> types;
    
    public LocalDate getDateAsLocalDate() {
        return date != null ? LocalDate.parse(date) : null;
    }
}
