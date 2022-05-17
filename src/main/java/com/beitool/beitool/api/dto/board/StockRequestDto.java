package com.beitool.beitool.api.dto.board;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 재고관리 Request를 위한 DTO
 * @author Chanos
 * @since 2022-05-18
 */
@Data
public class StockRequestDto {
    private String accessToken;
    private Long id;

    private String authorName;

    private String productName; //상품명
    private Integer quantity; //상품개수
    private String description; //특이사항

    @JsonDeserialize(using= LocalDateTimeDeserializer.class)
    @JsonSerialize(using= LocalDateTimeSerializer.class)
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expirationTime; //유통기한, 시간 포맷 미정
    
    private String productFileName; //파일 이름 (/stock/~)
    private String productFilePath; //파일 경로
}
