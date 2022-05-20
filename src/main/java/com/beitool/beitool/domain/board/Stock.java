package com.beitool.beitool.domain.board;

import com.beitool.beitool.domain.Member;
import com.beitool.beitool.domain.Store;
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
public class Stock extends BoardDomain {

    public Stock(String authorName, LocalDateTime createdDate, Member member, Store store, String description,
                 String productName, Integer quantity, LocalDateTime expirationTime, LocalDateTime modifiedTime,
                 String productFileName, String productFilePath) {
        super(member, store, productName, createdDate);
        this.authorName = authorName;
        this.quantity = quantity;
        this.description = description;
        this.expirationTime = expirationTime;
        this.modifiedTime = modifiedTime; //최초 생성 시, 생성시간=수정시간
        this.productFileName = productFileName;
        this.productFilePath = productFilePath;
    }
    @Id
    @GeneratedValue
    @Column(name="stock_post_id")
    private Long id;

    @Column(name="author_name")
    private String authorName;

    private Integer quantity; //상품개수
    private String description; //특이사항

    @Column(name="modify_date")
    @JsonDeserialize(using= LocalDateTimeDeserializer.class)
    @JsonSerialize(using= LocalDateTimeSerializer.class)
    private LocalDateTime modifiedTime; //수정일

    @Column(name="expiration_date")
    @JsonDeserialize(using= LocalDateTimeDeserializer.class)
    @JsonSerialize(using= LocalDateTimeSerializer.class)
    private LocalDateTime expirationTime; //유통기한

    //사진 업로드를 위한 정보 (실제 사진을 DB에 저장하지 않고, 사진에 대한 정보만 저장한다.)
    private String productFileName;
    private String productFilePath;

    /*정보 수정*/
    public void updateStock(Integer quantity, LocalDateTime expirationTime, LocalDateTime modifiedTime, String authorName, String description, String productName) {
        this.quantity = quantity;
        this.expirationTime = expirationTime;
        this.authorName = authorName;
        this.description = description;
        super.updatePost(productName,modifiedTime);
    }

    /*파일 경로와 이름 수정*/
    public void updateFile(String newFileName, String newFilePath) {
        this.productFileName = newFileName;
        this.productFilePath = newFilePath;
    }
}