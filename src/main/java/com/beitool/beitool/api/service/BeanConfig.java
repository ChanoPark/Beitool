package com.beitool.beitool.api.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
/** 2022-03-27 수동 빈 설정을 위한 클래스
 *  RestTemplate은 카카오 API와 통신에 사용되는 HTTP Method 사용을 위해 빈으로 등록한다.
 *
 * Implemented By Chanos
 */
@Configuration
public class BeanConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
