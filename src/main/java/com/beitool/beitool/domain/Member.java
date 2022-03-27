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

    public Member(Long id) {
        this.id=id;
    }

    @Id
    @Column(name="member_id")
    private Long id;

    @Column(name="name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name="position")
    private MemberPosition position;

}