package com.beitool.beitool.api.dto.board;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

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

    @ApiModelProperty(value="게시글 정보", example="게시글 번호, 내용, 직원, 완료유무, 업무 날짜 포함")
    private List<PostInfo> posts;

    @ApiModelProperty(value="결과 메시지", example="Success & Fail")
    private String message;

    public void addPost(Long id, String content, String employeeName, boolean isClear, LocalDate jobDate) {
        PostInfo newPost = new PostInfo(id, content, employeeName, isClear, jobDate);
        posts.add(newPost);
    }

    @Data @AllArgsConstructor
    public class PostInfo {
        private Long id;
        private String title;
        private String employeeName;
        private boolean isClear;

        @JsonDeserialize(using= LocalDateDeserializer.class)
        @JsonSerialize(using= LocalDateSerializer.class)
        private LocalDate jobDate;
    }

}
