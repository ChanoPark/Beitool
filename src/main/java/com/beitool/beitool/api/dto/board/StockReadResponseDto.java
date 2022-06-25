package com.beitool.beitool.api.dto.board;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class StockReadResponseDto {
    public StockReadResponseDto() {
        this.stocks = new ArrayList<>();
        this.message = "Failed";
    }

    public StockReadResponseDto(String message) {
        this.message = message;
    }

    @ApiModelProperty(value="재고관리 게시글 정보", example="게시글 번호, 제목, 내용, 유통기한, 수정시간, 사진경로 포함됨.")
    private List<StockList> stocks;

    @ApiModelProperty(value="결과 메시지", example="Success & Fail")
    private String message;

    public void setStock(Long id, String title, String description, LocalDateTime expirationTime,
                         LocalDateTime modifyTime, String filePath) {
        this.stocks.add(new StockList(id, title, description, expirationTime, modifyTime, filePath));
    }

    @Data
    static class StockList {
        public StockList(Long id, String title, String description, LocalDateTime expirationTime,
                         LocalDateTime modifyTime, String filePath) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.expirationTime = expirationTime;
            this.modifyTime = modifyTime;
            this.filePath = filePath;
        }

        private Long id;
        private String title;
        private String description;

        @JsonDeserialize(using= LocalDateTimeDeserializer.class)
        @JsonSerialize(using= LocalDateTimeSerializer.class)
        private LocalDateTime expirationTime;

        @JsonDeserialize(using= LocalDateTimeDeserializer.class)
        @JsonSerialize(using= LocalDateTimeSerializer.class)
        private LocalDateTime modifyTime;

        private String filePath;
    }
}
