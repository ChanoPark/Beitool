package com.beitool.beitool.api.dto.board;

import com.beitool.beitool.domain.board.BoardDomain;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * 게시판을 사용하기 위한 ResponseDto
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

    public void setPosts(BoardDomain post) {
        this.posts.add(post);
    }
}
