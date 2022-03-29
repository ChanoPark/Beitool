package com.beitool.beitool.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;

@Entity
@Getter @Table(name="member")
public class Member {

    public Member() {

    }

    public Member(Long id, String refreshToken) {
        this.id=id;
        this.refreshToken = refreshToken;
    }

    @Id
    @Column(name="member_id")
    private Long id;

    private String name;
    private String refreshToken;


    @Enumerated(EnumType.STRING)
    private MemberPosition position;

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}