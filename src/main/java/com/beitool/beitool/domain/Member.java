package com.beitool.beitool.domain;

import lombok.Getter;

import javax.persistence.*;

/**
 * 회원 엔티티
 * @author Chanos
 * @since 2022-03-27
 */
@Entity
@Getter @Table(name="member")
public class Member {

    public Member() { }

    public Member(Long id, String refreshToken) {
        this.id=id;
        this.refreshToken = refreshToken;
    }

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="active_store_id")
    private Store activeStore;

    @Id
    @Column(name="member_id")
    private Long id;

    @Column(name="refresh_token")
    private String refreshToken;

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    public void setActiveStore(Store store) {this.activeStore = store;}

}