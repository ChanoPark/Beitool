package com.beitool.beitool.api.dto.board;

import com.beitool.beitool.domain.board.BoardDomain;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * 공지사항, 자유게시판을 사용하기 위한 ResponseDto
 * @author Chanos
 * @since 2022-05-09
 */
@Data
public class BoardResponseDto {
    public BoardResponseDto() {
        posts = new ArrayList<BoardDomain>();
    }
    public BoardResponseDto(String message) {
        this.message=message;
    }

    @ApiModelProperty(value="결과 메시지", example="Success & Fail")
    private String message;

    @ApiModelProperty(value="게시글 정보(제목, 게시글)", example="title: ~ / content: ~")
    private List<BoardDomain> posts;

    @ApiModelProperty(value="전체 페이지 수", example="5")
    private Long totalPage;

    @ApiModelProperty(value="전체 페이지를 배열로 나타낸 것(프론트 요구사항)", example="[1,2,3,4,5]")
    private Vector<Long> totalPageArray;

    public void changePageToArray(Long page) {
        this.totalPageArray = new Vector<Long>();
        for (Long i=1L ; i<=page; i++) {
            this.totalPageArray.add(i);
        }
    }

    public void setPosts(BoardDomain post) {
        this.posts.add(post);
    }
}
