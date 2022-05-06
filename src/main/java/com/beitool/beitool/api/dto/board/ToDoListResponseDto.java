package com.beitool.beitool.api.dto.board;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * ToDoList의 Response를 전달하기 위한 DTO
 * 보여지는 게시글의 형태와 결과가 다르기 때문에 따로 작성한다.
 * @author Chanos
 * @since 2022-05-04
 */
@Data
public class ToDoListResponseDto {
    public ToDoListResponseDto() { this.posts = new ArrayList<>(); }
    public ToDoListResponseDto(String message) {
        this.message = message;
    }
    List<PostInfo> posts;
    private String message;

    public void addPost(Long id, String title, String content, String employeeName, boolean isClear, LocalDate jobDate) {
        PostInfo newPost = new PostInfo(id, title, content, employeeName, isClear, jobDate);
        posts.add(newPost);
    }

    @Data @AllArgsConstructor
    public class PostInfo {
        private Long id;
        private String title;
        private String content;
        private String employeeName;
        private boolean isClear;

        @JsonDeserialize(using= LocalDateDeserializer.class)
        @JsonSerialize(using= LocalDateSerializer.class)
        private LocalDate jobDate;
    }

}
