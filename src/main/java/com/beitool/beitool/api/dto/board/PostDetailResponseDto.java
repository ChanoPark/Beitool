package com.beitool.beitool.api.dto.board;

import com.beitool.beitool.domain.Member;
import com.beitool.beitool.domain.Store;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.annotations.ApiModelProperty;
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

    @ApiModelProperty(value="게시글 제목", example="게시글 제목")
    private String title;

    @ApiModelProperty(value="게시글 내용", example="게시글 내용")
    private String content;

    @ApiModelProperty(value="게시글 번호", example="1")
    private Long id;

    @ApiModelProperty(value="작성자 이름", example="이름1")
    private String author;

    @ApiModelProperty(value="작성 시간(날짜포함)", example="2022-05-01 11:00")
    @JsonDeserialize(using= LocalDateTimeDeserializer.class)
    @JsonSerialize(using= LocalDateTimeSerializer.class)
    private LocalDateTime createdTime;

    @ApiModelProperty(value="결과 메시지", example="Success & Fail")
    String message;
}
