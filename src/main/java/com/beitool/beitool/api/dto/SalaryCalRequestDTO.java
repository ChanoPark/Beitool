package com.beitool.beitool.api.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import io.swagger.annotations.ApiModelProperty;
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

    @ApiModelProperty(value="엑세스 토큰", example="NvmzOQm13cCAq", required = true)
    private String accessToken;

    @ApiModelProperty(value="주급, 월급 선택", example="Week, Month", required = true)
    private String isMonthOrWeek;

    @ApiModelProperty(value="주급 - 주차 선택", example="2")
    @Nullable
    private Integer countWeek;

    @ApiModelProperty(value="월 선택", example="2022-06-01(01~30까지 상관X)")
    @JsonDeserialize(using= LocalDateDeserializer.class)
    @JsonSerialize(using= LocalDateSerializer.class)
    private LocalDate requestTime;
}
