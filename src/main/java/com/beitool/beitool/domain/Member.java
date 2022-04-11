package com.beitool.beitool.domain;

import lombok.Getter;

import javax.persistence.*;

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

    @Column(name="member_name")
    private String name;

    @Column(name="refresh_token")
    private String refreshToken;

    @Enumerated(EnumType.STRING)
    private MemberPosition position;

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void setPosition(MemberPosition position) {
        this.position = position;
    }
}