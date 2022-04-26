package com.beitool.beitool.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 공지사항 게시판 도메인 - BoardDomain 상속 받는 클래스
 * 1.게시글 번호
 * 2.작성자 이름
 * 3.내용
 * 4.작성 날짜
 * @author chanos
 * @since 2022-04-26
 */
@Entity @DiscriminatorValue("announcement")
@Getter @Table(name="announcement_board")
@NoArgsConstructor
public class AnnouncementBoard extends BoardDomain {

    public AnnouncementBoard(String authorName, String content, LocalDateTime createdDate,
                             Member member, Store store, String title) {
        super(member, store, title);
        this.authorName = authorName;
        this.content = content;
        this.createdDate = createdDate;
    }

    @Id @GeneratedValue
    @Column(name="announce_post_id")
    private Long id;

    @Column(name="author_name")
    private String authorName;

    private String content;

    @Column(name="create_date")
    @JsonDeserialize(using= LocalDateTimeDeserializer.class)
    @JsonSerialize(using= LocalDateTimeSerializer.class)
    private LocalDateTime createdDate;
}
