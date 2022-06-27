package com.beitool.beitool.api.dto.board;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 재고관리 게시글 삭제를 위한 RequestDTO
 * @author Chanos
 * @since 2022-05-20
 */
@Data
public class StockDeleteRequestDto {

    @ApiModelProperty(value="사진 이름", example="202201319~", required = true)
    private String productFileName;

    @ApiModelProperty(value="게시글 번호", example="1", required = true)
    private Long id;
}
