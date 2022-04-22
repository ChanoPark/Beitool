package com.beitool.beitool.api.dto;

import lombok.Data;

/**
 * 게시판을 사용하기 위한 RequestDTO
 * @author Chanos
 * @since 2022-04-22
 */
@Data
public class BoardRequestDto {
    private String accessToken;

    //게시글 생성, 수정에 사용될 제목과 내용, 시간은 현재 시간.
    private String title;
    private String content;
}