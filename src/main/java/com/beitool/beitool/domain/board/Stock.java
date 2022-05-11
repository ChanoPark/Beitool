package com.beitool.beitool.domain.board;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 재고관리 게시판을 위한 도메인 (파일 업로드)
 * @author Chanos
 * @since 2022-05-11
 */
@Entity
@DiscriminatorValue("Stock")
@Getter
@Table(name="stock_board")
@NoArgsConstructor
public class Stock {
    @Id
    @GeneratedValue
    @Column(name="stock_post_id")
    private Long id;

    @Column(name="author_name")
    private String authorName;

    @Column(name="product_name")
    private String productName; //상품명

    private Integer quantity; //상품개수
    private String description; //특이사항

    @Column(name="modify_date")
    @JsonDeserialize(using= LocalDateTimeDeserializer.class)
    @JsonSerialize(using= LocalDateTimeSerializer.class)
    private LocalDateTime modifyDate; //수정일

    @Column(name="expiration_date")
    @JsonDeserialize(using= LocalDateTimeDeserializer.class)
    @JsonSerialize(using= LocalDateTimeSerializer.class)
    private LocalDateTime expirationDate; //유통기한

    //사진 업로드를 위한 정보 (실제 사진을 DB에 저장하지 않고, 사진에 대한 정보만 저장한다.)
    private String fileName;
    private String filePath;
    private Long fileSize;

    private String content;
}