package com.min.holidaykeeper.dto.response;

import com.min.holidaykeeper.entity.Holiday;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
public class HolidayResponse {
    private Long id;

    private LocalDate date;

    private String localName;

    private String name;

    private String countryCode;

    private boolean fixed;

    private boolean global;

    private String counties;

    private Integer launchYear;

    private Set<String> types;

    private int holidayYear;

    public static HolidayResponse from(Holiday holiday) {
        return HolidayResponse.builder()
                .id(holiday.getId())
                .date(holiday.getDate())
                .localName(holiday.getLocalName())
                .name(holiday.getName())
                .countryCode(holiday.getCountryCode())
                .fixed(holiday.isFixed())
                .global(holiday.isGlobal())
                .counties(holiday.getCounties())
                .launchYear(holiday.getLaunchYear())
                .types(holiday.getTypes())
                .holidayYear(holiday.getHolidayYear())
                .build();
    }
}
