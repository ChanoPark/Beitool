package com.beitool.beitool.api.dto.board;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 재고관리 게시판의 파일 수정을 위한 ReqeustDTO
 * @author Chanos
 * @since 2022-05-20
 */

@Data
public class StockFileRequestDto {

    @ApiModelProperty(value="게시글 번호(수정 시 사용)", example="1")
    private Long id;

    @ApiModelProperty(value="수정 전 사진 이름", example="기존 사진 이름(20220501~)", required = true)
    private String oldFileName;

    @ApiModelProperty(value="수정 할 사진 이름", example="새로운 사진 이름(20220601~)", required = true)
    private String newFileName;

    @ApiModelProperty(value="수정할 사진 경로", example="새로운 사진 경로(/stock~)", required = true)
    private String newFilePath;
}
