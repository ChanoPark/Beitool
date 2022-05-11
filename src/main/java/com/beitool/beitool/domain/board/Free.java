package com.beitool.beitool.domain.board;

import com.beitool.beitool.domain.Member;
import com.beitool.beitool.domain.Store;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 자유 게시판 엔티티 - 공지사항과 거의 유사하지만 테이블 크기가 커지는 것을 방지하기 위해 분리함. 이에 따라 새로운 객체로 관리
 * @author Chanos
 * @since 2022-04-30
 */
@Entity @DiscriminatorValue("Free")
@Getter @Table(name="free_board")
@NoArgsConstructor
public class Free extends BoardDomain{

    public Free(String authorName, String content, LocalDateTime createdDate,
                        Member member, Store store, String title) {
        super(member, store, title, createdDate);
        this.authorName = authorName;
        this.content = content;
    }

    public void updatePost(String title, String content, LocalDateTime modifiedTime) {
        super.updatePost(title, modifiedTime);
        this.content = content;
    }

    @Id
    @GeneratedValue
    @Column(name="free_post_id")
    private Long id;

    @Column(name="author_name")
    private String authorName;

    private String content;
}
