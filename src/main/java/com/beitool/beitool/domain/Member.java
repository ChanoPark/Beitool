package com.beitool.beitool.domain;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter @Table(name="member")
public class Member {

    @Id @GeneratedValue
    @Column(name="member_id")
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private MemberPosition position;


}
