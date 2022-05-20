package com.beitool.beitool.api.dto.board;

import lombok.Data;

/**
 * 재고관리 게시글 삭제를 위한 RequestDTO
 * @author Chanos
 * @since 2022-05-20
 */
@Data
public class StockDeleteRequestDto {
    private String accessToken;
    private String fileName;
    private Long id;
}
