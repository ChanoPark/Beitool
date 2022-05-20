package com.beitool.beitool.api.dto.board;

import lombok.Data;

/**
 * 재고관리 게시판의 파일 수정을 위한 ReqeustDTO
 * @author Chanos
 * @since 2022-05-20
 */

@Data
public class StockFileRequestDto {
    private Long id; //게시글 id
    private String oldFileName;
    private String newFileName;
    private String newFilePath;
}
