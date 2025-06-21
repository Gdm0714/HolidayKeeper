package com.min.holidaykeeper.scheduler;

import com.min.holidaykeeper.entity.Country;
import com.min.holidaykeeper.repository.CountryRepository;
import com.min.holidaykeeper.service.HolidayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Year;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class HolidayBatchScheduler {

    private final HolidayService holidayService;
    private final CountryRepository countryRepository;

    @Scheduled(cron = "0 0 1 2 1 *")
    public void syncHolidaysForCurrentAndPreviousYear() {

        int curYear = Year.now().getValue();
        int preYear = curYear - 1;

        List<Country> countries = countryRepository.findAll();

        for (Country country : countries) {
            holidayService.refreshHolidaysByCountryAndYear(country.getCountryCode(), curYear);
            holidayService.refreshHolidaysByCountryAndYear(country.getCountryCode(), preYear);
        }
    }
}
