package com.beitool.beitool.api.dto.board;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.annotations.ApiModelProperty;
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
    
    @ApiModelProperty(value="결과 메시지", example="Success & Fail")
    private String message;
    
    @ApiModelProperty(value="게시글 번호", example="8")
    private Long id;

    @ApiModelProperty(value="작성자 이름", example="이름5")
    private String authorName;

    @ApiModelProperty(value="재고 이름", example="재고 이름4")
    private String productName;
    
    @ApiModelProperty(value="재고 개수", example="4")
    private Integer quantity;

    @ApiModelProperty(value="특이 사항", example="특이 사항2")
    private String description;

    @ApiModelProperty(value="유통 기한", example="2022-06-25 10:22")
    @JsonDeserialize(using= LocalDateTimeDeserializer.class)
    @JsonSerialize(using= LocalDateTimeSerializer.class)
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expirationTime;

    @ApiModelProperty(value="수정된 시간", example="2022-06-26 10:46")
    @JsonDeserialize(using= LocalDateTimeDeserializer.class)
    @JsonSerialize(using= LocalDateTimeSerializer.class)
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;

    @ApiModelProperty(value="업로드된 파일 경로", example="파일 경로(/stock~)")
    private String productFilePath;

    @ApiModelProperty(value="업로드된 파일 이름", example="파일 이름(202204~)")
    private String productFileName;
}
