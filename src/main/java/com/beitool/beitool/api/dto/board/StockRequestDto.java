package com.beitool.beitool.api.dto.board;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 재고관리 Request를 위한 DTO
 * @author Chanos
 * @since 2022-05-18
 */
@Data
public class StockRequestDto {
    @ApiModelProperty(value="엑세스 토큰", example="FNqk1m3ka", required = true)
    private String accessToken;

    @ApiModelProperty(value="게시글 번호(수정 시 사용)", example="3")
    private Long id;

    @ApiModelProperty(value="작성자 이름", example="이름4", required = true)
    private String authorName;

    @ApiModelProperty(value="재고 이름", example="재고 이름", required = true)
    private String productName;
    
    @ApiModelProperty(value="재고 개수", example="5", required = true)
    private Integer quantity;
    
    @ApiModelProperty(value="특이 사항", example="특이사항 기재", required = true)
    private String description;

    @ApiModelProperty(value="유통 기한", example="2022-03-10 10:11", required = true)
    @JsonDeserialize(using= LocalDateTimeDeserializer.class)
    @JsonSerialize(using= LocalDateTimeSerializer.class)
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expirationTime;

    @ApiModelProperty(value="업로드된 파일 이름", example="사진 이름(202206~)", required = true)
    private String productFileName;

    @ApiModelProperty(value="업로드된 파일 경로", example="사진 경로(stock/~)", required = true)
    private String productFilePath;
}
