package com.beitool.beitool.api.dto.board;

import com.beitool.beitool.domain.board.BoardDomain;
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

    private String message; //결과를 알려주기 위한 message
    private List<BoardDomain> posts; // 제목, 게시글

    private Long totalPage; //전체 페이지 수(프론트에게 알려주기 위함)
    private Vector<Long> totalPageArray; //전체 페이지를 배열로 만듬(프론트 요청사항)

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
