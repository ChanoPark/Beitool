package com.beitool.beitool.api.dto.board;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * ToDoList의 Response를 전달하기 위한 DTO
 * 보여지는 게시글의 형태와 결과가 다르기 때문에 따로 작성한다.
 * @author Chanos
 * @since 2022-05-04
 */
@Data
public class ToDoListResponseDto {
    List<PostInfo> posts;

    public class PostInfo{
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
