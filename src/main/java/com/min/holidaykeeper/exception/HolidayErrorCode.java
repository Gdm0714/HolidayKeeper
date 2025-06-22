package com.min.holidaykeeper.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum HolidayErrorCode implements ErrorCode {
    HOLIDAY_NOT_FOUND(HttpStatus.NOT_FOUND, "H001", "공휴일 정보를 찾을 수 없습니다"),
    EXTERNAL_API_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "E001", "외부 API 연동 중 오류가 발생했습니다"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S001", "서버 내부 오류가 발생했습니다");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
