package com.beitool.beitool.api.dto.board;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 공지사항, 자유게시판을 사용하기 위한 RequestDTO
 * @author Chanos
 * @since 2022-05-09
 */
@Data
public class BoardRequestDto {

    @ApiModelProperty(value="게시판 종류", example="Free", required = true)
    private String boardType;

    @ApiModelProperty(value="제목", example="게시글 제목", required = true)
    private String title;

    @ApiModelProperty(value="내용", example="게시글 내용", required = true)
    private String content;

    @ApiModelProperty(value="페이지", example="1", required = true)
    private Integer page;

    @ApiModelProperty(value="게시글 번호", example="게시글 제목")
    private Long id;
}