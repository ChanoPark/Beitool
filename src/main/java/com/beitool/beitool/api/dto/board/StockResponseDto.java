package com.beitool.beitool.api.dto.board;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 재고관리 Response를 위한 DTO
 * @author Chanos
 * @since 2022-05-17
 */
@Data @AllArgsConstructor
public class StockResponseDto {
    public StockResponseDto(String message) {
        this.message = message;
    }
    private String message;
    private Long id;

    private String authorName;

    private String productName; //상품명
    private Integer quantity; //상품개수
    private String description; //특이사항

    @JsonDeserialize(using= LocalDateTimeDeserializer.class)
    @JsonSerialize(using= LocalDateTimeSerializer.class)
    private LocalDateTime expirationTime; //유통기한

    @JsonDeserialize(using= LocalDateTimeDeserializer.class)
    @JsonSerialize(using= LocalDateTimeSerializer.class)
    private LocalDateTime createdDate; //최종 수정 시간

    private String productFilePath; //파일 경로
}
