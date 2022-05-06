package com.beitool.beitool.api.dto.board;

import com.beitool.beitool.domain.board.BoardDomain;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 게시판을 사용하기 위한 ResponseDto
 * @author Chanos
 * @since 2022-04-28
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

    public void setPosts(BoardDomain post) {
        this.posts.add(post);
    }
}
