package com.min.holidaykeeper.api;

import com.min.holidaykeeper.dto.CountryDto;
import com.min.holidaykeeper.dto.HolidayDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@RequiredArgsConstructor
public class NagerApiService {
    private static final String BASE_URL = "https://date.nager.at/api/v3";
    private final RestTemplate restTemplate;

    public List<CountryDto> getCountries() {
        String url = BASE_URL + "/AvailableCountries";
        ResponseEntity<List<CountryDto>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<CountryDto>>() {
                }
        );
        return response.getBody();
    }

    public List<HolidayDto> getHolidays(String countryCode, int year) {
        String url = String.format("%s/PublicHolidays/%d/%s", BASE_URL, year, countryCode);

        ResponseEntity<List<HolidayDto>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<HolidayDto>>() {
                }
        );
        return response.getBody();
    }
}
