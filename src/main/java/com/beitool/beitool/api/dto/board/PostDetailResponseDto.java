package com.beitool.beitool.api.dto.board;

import com.beitool.beitool.domain.Member;
import com.beitool.beitool.domain.Store;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 게시글 작성, 수정 이후 작성된 게시글 정보를 반환하는 클래스
 * @author Chanos
 * @since 2022-04-29
 */
@Data
@AllArgsConstructor
public class PostDetailResponseDto {

    public PostDetailResponseDto(String message) {
        this.message = message;
    }

    String title;
    String content;

    Long id;

    String author;

    @JsonDeserialize(using= LocalDateTimeDeserializer.class)
    @JsonSerialize(using= LocalDateTimeSerializer.class)
    private LocalDateTime createdTime;

    String message;
}
