package com.beitool.beitool.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 스케줄 업데이트를 위한 Request DTO
 *
 * @author Chanos
 * @since 2022-05-26
 */
@Data
public class ScheduleUpdateRequestDto {

    @ApiModelProperty(value="엑세스 토큰", example="nLAmq1Oc", required = true)
    private String accessToken;

    @ApiModelProperty(value="업무 대상(직원) 번호", example="5")
    private Long employee;

    @ApiModelProperty(value="게시글 번호", example="2")
    private Long id;

    @ApiModelProperty(value="근로 날짜", example="2022-06-12")
    @JsonDeserialize(using= LocalDateDeserializer.class)
    @JsonSerialize(using= LocalDateSerializer.class)
    private LocalDate workDay;

    @ApiModelProperty(value="출근 시간", example="2022-06-12 01:22")
    @JsonDeserialize(using= LocalDateTimeDeserializer.class)
    @JsonSerialize(using= LocalDateTimeSerializer.class)
    @JsonFormat(pattern="yyyy-MM-dd HH:mm")
    private LocalDateTime workStartTime;

    @ApiModelProperty(value="퇴근 시간", example="2022-06-12 03:50")
    @JsonDeserialize(using=LocalDateTimeDeserializer.class)
    @JsonSerialize(using=LocalDateTimeSerializer.class)
    @JsonFormat(pattern="yyyy-MM-dd HH:mm")
    private LocalDateTime workEndTime;
}
