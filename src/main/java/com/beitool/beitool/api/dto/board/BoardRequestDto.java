package com.beitool.beitool.api.dto.board;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 공지사항, 자유게시판을 사용하기 위한 RequestDTO
 * @author Chanos
 * @since 2022-05-09
 */
@Data
@Schema(description = "보드컨트롤러DTO")
public class BoardRequestDto {
    @Schema(description = "엑세스 토큰")
    private String accessToken;
    @Schema(description = "게시판 종류")
    private String boardType;

    //게시글 생성, 수정에 사용될 제목과 내용, 시간은 현재 시간.
    private String title;
    private String content;

    private Integer page; //조회할 페이지
    
    private Long id; //게시글 번호
}