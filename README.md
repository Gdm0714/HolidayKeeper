# Holiday Keeper 

전 세계 공휴일 데이터를 저장, 조회, 관리하는 REST API 서비스

### 목표
- [x] 연도별, 국가별 공휴일 조회
- [x] 공휴일 재동기화
- [x] 공휴일 생성
- [x] 공휴일 삭제

## 🛠 기술 스택

- Java 21
- Spring Boot 3.4.0
- Spring Data JPA (Hibernate)
- H2 Database (In-Memory)
- OpenAPI 3.0 (Swagger UI)
- JUnit 5

## 🚀 시작하기

### 설치 및 실행

1. **프로젝트 클론**
```bash
git clone https://github.com/Gdm0714/holiday-keeper.git
cd holiday-keeper
```

2. **빌드**
```bash
./gradlew clean build
```

3. **실행**
```bash
./gradlew bootRun
```

4. **접속 확인**
- 애플리케이션: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- H2 Console: http://localhost:8080/h2-console

## 📖 REST API 명세

### 1. 공휴일 검색 (Search Holidays)

| 항목 | 내용 |
|------|------|
| **URL** | `GET /api/holidays` |
| **설명** | 연도별·국가별 필터 기반 공휴일 조회 |
| **인증** | 불필요 |

**요청 파라미터**

| 파라미터 | 타입 | 필수 | 설명 | 예시 |
|----------|------|------|------|------|
| holidayYear | Integer | N | 연도 | 2025 |
| countryCode | String | N | 국가 코드 (ISO 3166-1 alpha-2) | KR |
| from | LocalDate | N | 시작일 | 2025-01-01 |
| to | LocalDate | N | 종료일 | 2025-12-31 |
| type | String | N | 공휴일 타입 | Public |
| page | Integer | N | 페이지 번호 (0부터 시작) | 0 |
| size | Integer | N | 페이지 크기 (기본값: 20) | 20 |
| sort | String | N | 정렬 기준 | date,asc |

**응답 예시**
```json
{
  "content": [
    {
      "id": 1,
      "date": "2025-01-01",
      "localName": "신정",
      "name": "New Year's Day",
      "countryCode": "KR",
      "fixed": false,
      "global": true,
      "counties": null,
      "launchYear": null,
      "types": ["Public"],
      "holidayYear": 2025
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20,
    "sort": {
      "sorted": true,
      "ascending": true
    }
  },
  "totalElements": 15,
  "totalPages": 1,
  "first": true,
  "last": true
}
```

### 2. 공휴일 재동기화 (Refresh Holidays)

| 항목 | 내용 |
|------|------|
| **URL** | `POST /api/holidays/refresh/{countryCode}/{year}` |
| **설명** | 특정 연도·국가 데이터를 재호출하여 Upsert |
| **인증** | 불필요 |

**경로 파라미터**

| 파라미터 | 타입 | 필수 | 설명 | 예시 |
|----------|------|------|------|------|
| countryCode | String | Y | 국가 코드 | KR |
| year | Integer | Y | 연도 | 2025 |

**응답 예시**
```json
{
  "status": 200,
  "message": "KR 2025년 공휴일 데이터가 갱신되었습니다."
}
```

### 3. 공휴일 삭제 (Delete Holidays)

| 항목 | 내용 |
|------|------|
| **URL** | `DELETE /api/holidays/{countryCode}/{year}` |
| **설명** | 특정 연도·국가의 공휴일 레코드 전체 삭제 |
| **인증** | 불필요 |

**경로 파라미터**

| 파라미터 | 타입 | 필수 | 설명 | 예시 |
|----------|------|------|------|------|
| countryCode | String | Y | 국가 코드 | KR |
| year | Integer | Y | 연도 | 2025 |

**응답 예시**
```json
{
  "status": 200,
  "message": "KR 2025년 공휴일 데이터가 삭제되었습니다."
}
```

## 테스트

### 테스트 실행
```bash
# 전체 테스트 실행
./gradlew clean test
```

### 테스트 결과
![img.png](img.png)

## 💾 데이터베이스 구조
![img_1.png](img_1.png)

#### Countries 테이블
| 컬럼명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| country_code | VARCHAR(2) | PK | 국가 코드 |
| name | VARCHAR(100) | NOT NULL | 국가명 |

#### Holiday 테이블
| 컬럼명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 공휴일 ID |
| date | DATE | NOT NULL | 날짜 |
| local_name | VARCHAR(200) | NOT NULL | 현지 이름 |
| name | VARCHAR(200) | NOT NULL | 영문 이름 |
| country_code | VARCHAR(2) | NOT NULL, FK | 국가 코드 |
| fixed | BOOLEAN | NOT NULL | 고정 여부 |
| global | BOOLEAN | NOT NULL | 전국 공휴일 여부 |
| counties | VARCHAR(500) | | 적용 지역 |
| launch_year | INT | | 시작 연도 |
| holiday_year | INT | NOT NULL | 연도 |

#### Holiday_Types 테이블
| 컬럼명 | 타입 | 제약조건 | 설명     |
|--------|------|----------|--------|
| holiday_id | BIGINT | FK | 공휴일 ID |
| type | VARCHAR(50) | | 공휴일 타입 |

### 인덱스
- `idx_holidays_year_country` on Holiday (holiday_year, country_code)

## Swagger UI

### Swagger UI 실행
- URL:http://localhost:8080/swagger-ui.html

### H2 Console 접속 정보
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:holidaydb`
- Username: `dongmin`
- Password: `1234`