package com.beitool.beitool.domain;

import lombok.Getter;

import javax.persistence.*;

/**
 * 2022-03-27 회원 엔티티
 * Implemented by Chanos
 */
@Entity
@Getter @Table(name="member")
public class Member {

    public Member() { }

    public Member(Long id, String refreshToken) {
        this.id=id;
        this.refreshToken = refreshToken;
    }

    @Id
    @Column(name="member_id")
    private Long id;

    @Column(name="refresh_token")
    private String refreshToken;

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

}