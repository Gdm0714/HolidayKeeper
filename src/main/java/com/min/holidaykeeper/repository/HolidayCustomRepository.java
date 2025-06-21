package com.min.holidaykeeper.repository;

import com.min.holidaykeeper.dto.request.HolidayRequest;
import com.min.holidaykeeper.entity.Holiday;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HolidayCustomRepository {
    Page<Holiday> searchHolidays(HolidayRequest request, Pageable pageable);
}
