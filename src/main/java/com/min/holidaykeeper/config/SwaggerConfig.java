package com.min.holidaykeeper.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("Holiday Keeper API 문서입니다.")
                        .version("1.0.0")
                        .description("전 세계 공휴일 데이터를 저장·조회·관리하는 MINI Service REST API 문서입니다!"));
    }

}
