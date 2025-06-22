package com.min.holidaykeeper.api;

import com.min.holidaykeeper.dto.CountryDto;
import com.min.holidaykeeper.dto.HolidayDto;
import com.min.holidaykeeper.exception.BusinessException;
import com.min.holidaykeeper.exception.HolidayErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NagerApiService {
    private static final String BASE_URL = "https://date.nager.at/api/v3";
    private final RestTemplate restTemplate;

    public List<CountryDto> getCountries() {
        String url = BASE_URL + "/AvailableCountries";
        try {
            ResponseEntity<List<CountryDto>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<CountryDto>>() {
                    }
            );
            log.info("국가 목록을 {}개 가져왔습니다.", response.getBody() != null ? response.getBody().size() : 0);
            return response.getBody();
        } catch (RestClientException e) {
            log.error("Nager API에서 국가 목록을 가져오는 데 실패했습니다", e);
            throw new BusinessException(HolidayErrorCode.EXTERNAL_API_ERROR, 
                    "국가 목록을 가져오는데 실패했습니다: " + e.getMessage());
        }
    }

    public List<HolidayDto> getHolidays(String countryCode, int year) {
        String url = String.format("%s/PublicHolidays/%d/%s", BASE_URL, year, countryCode);
        try {
            ResponseEntity<List<HolidayDto>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<HolidayDto>>() {
                    }
            );
            log.info("{} {}년 공휴일 정보를 {}개 가져왔습니다.", countryCode, year, response.getBody() != null ? response.getBody().size() : 0);
            return response.getBody();
        } catch (RestClientException e) {
            log.error("{} {}년 공휴일 정보를 가져오는 데 실패했습니다", countryCode, year, e);
            throw new BusinessException(HolidayErrorCode.EXTERNAL_API_ERROR,
                    String.format("%s %d년 공휴일 정보를 가져오는데 실패했습니다: %s", countryCode, year, e.getMessage()));
        }
    }
}
