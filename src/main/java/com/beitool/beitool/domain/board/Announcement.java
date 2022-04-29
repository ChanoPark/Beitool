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
 * 공지사항 게시판 도메인 - BoardDomain 상속 받는 클래스
 * 1.게시글 번호
 * 2.작성자 이름
 * 3.내용
 * 4.작성 날짜
 * @author chanos
 * @since 2022-04-26
 */
@Entity @DiscriminatorValue("Announcement")
@Getter @Table(name="announcement_board")
@NoArgsConstructor
public class Announcement extends BoardDomain {

    public Announcement(String authorName, String content, LocalDateTime createdDate,
                        Member member, Store store, String title) {
        super(member, store, title, createdDate);
        this.authorName = authorName;
        this.content = content;
    }

    public void updatePost(String title, String content, LocalDateTime modifiedTime) {
        super.updatePost(title, modifiedTime);
        this.content = content;
    }

    @Id @GeneratedValue
    @Column(name="announce_post_id")
    private Long id;

    @Column(name="author_name")
    private String authorName;

    private String content;

}
