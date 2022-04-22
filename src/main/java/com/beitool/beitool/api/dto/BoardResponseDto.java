package com.beitool.beitool.api.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;

import javax.persistence.Column;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 게시판을 사용하기 위한 ResponseDto
 * @author Chanos
 * @since 2022-04-22
 */
@Data
public class BoardResponseDto {
    private String message; //결과를 알려주기 위한 message

    //게시글 수정된 결과, 조회된 게시글
    private String title;
    private String content;
    @JsonDeserialize(using= LocalDateTimeDeserializer.class)
    @JsonSerialize(using= LocalDateTimeSerializer.class)
    private LocalDateTime createdTime;

    //게시판에서 조회된 게시글
    private Map<String, String> posts; // 제목, 게시글

    //게시글에서 조회된 게시글들 작성된 시간
    @JsonDeserialize(using= LocalDateTimeDeserializer.class)
    @JsonSerialize(using= LocalDateTimeSerializer.class)
    private List<LocalDateTime> createdTimes;
}
