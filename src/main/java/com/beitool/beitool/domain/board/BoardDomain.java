package com.beitool.beitool.domain.board;

import com.beitool.beitool.domain.Member;
import com.beitool.beitool.domain.Store;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * 게시판 공통 도메인을 묶은 클래스, 해당 클래스를 상속받아 구체적인 도메인을 작성한다.
 * 게시판에 들어가 게시글 목록을 조회할 때 해당 클래스를 사용하고, 게시글을 클릭하여 자세한 정보를 볼 때 조인을 한다.
 * 상속 관계 매핑 전략 - JOINED 사용.
 * 1.게시글 번호
 * 2.작성자
 * 3.사업장
 * 4.제목
 * @author Chanos
 * @since 2022-04-26
 */
@Entity
@Inheritance(strategy= InheritanceType.JOINED)
@DiscriminatorColumn
@Table(name="board") @Getter
@NoArgsConstructor
public abstract class BoardDomain {

    //추상클래스의 생성자는 자식 클래스가 생성될 때 호출된다.
    public BoardDomain(Member member, Store store, String title) {
        this.member = member;
        this.store = store;
        this.title = title;
        this.isModified = false;
    }

    //제목 수정
    public void updatePost(String title) {
        this.title = title;
        this.isModified = true;
    }

    //자식 클래스 찾기(게시판종류 판별)
    public static Object findBoardType(String boardType) throws ClassNotFoundException {
        String reflection = "com.beitool.beitool.domain.board." + boardType;
        return Class.forName(reflection);
    }

    @Id @GeneratedValue
    @Column(name="post_id")
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="author_id")
    @JsonIgnore
    private Member member;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="store_id")
    @JsonIgnore
    private Store store;

    private String title;

    @Column(name="is_modified")
    private Boolean isModified;

    @Column(insertable = false, updatable = false)
    private String dtype;
}