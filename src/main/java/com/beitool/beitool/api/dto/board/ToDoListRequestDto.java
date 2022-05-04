package com.beitool.beitool.api.dto.board;

import com.beitool.beitool.domain.Member;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Data;

import java.time.LocalDate;

/**
 * ToDoList의 정보를 받기 위한 Request DTO
 * @author Chanos
 * @since 2022-05-04
 */
@Data
public class ToDoListRequestDto {
    private String accessToken;
    private String boardType;

    private String title;
    private String content;

    @JsonDeserialize(using= LocalDateDeserializer.class)
    @JsonSerialize(using= LocalDateSerializer.class)
    private LocalDate jobDate; //업무 기한

    private Long employee; // 지시 대상

    private Long id; //게시글 번호
}
