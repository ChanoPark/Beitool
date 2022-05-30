package com.beitool.beitool.api.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Data;
import org.springframework.lang.Nullable;

import java.time.LocalDate;

/**
 * 급여 계산기 Request를 위한 DTO
 * @author Chanos
 * @since 2022-05-30
 */
@Data
public class SalaryCalRequestDTO {
    private String accessToken;
    private String isMonthOrWeek; //주급인지 월급인지

    @Nullable
    private Integer countWeek; //주급이라면 몇주차인지

    @JsonDeserialize(using= LocalDateDeserializer.class)
    @JsonSerialize(using= LocalDateSerializer.class)
    private LocalDate requestTime; //몇월달껄 보고 싶은지
}
