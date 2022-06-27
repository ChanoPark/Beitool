package com.beitool.beitool.api.dto.board;

import com.beitool.beitool.domain.Member;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * ToDoList의 정보를 받기 위한 Request DTO
 * @author Chanos
 * @since 2022-05-04
 */
@Data
public class ToDoListRequestDto {

    @ApiModelProperty(value="지시 내용", example="분리수거 하기.", required = true)
    private String title;

    @ApiModelProperty(value="업무 기한", example="2022-06-21", required = true)
    @JsonDeserialize(using= LocalDateDeserializer.class)
    @JsonSerialize(using= LocalDateSerializer.class)
    private LocalDate jobDate;

    @ApiModelProperty(value="직원의 ID", example="5", required = true)
    private Long employee;

    @ApiModelProperty(value="게시글 번호 (수정 시 사용)", example="4")
    private Long id;
}
