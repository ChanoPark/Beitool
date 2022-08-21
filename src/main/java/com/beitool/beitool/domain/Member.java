package com.beitool.beitool.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.Collection;

/**
 * 회원 엔티티
 * @author Chanos
 * @since 2022-03-27
 */
@Getter @Table(name="member")
@NoArgsConstructor
@Entity
public class Member {

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

//    private String password; //refresh Token + REST KEY

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    public void setActiveStore(Store store) {this.activeStore = store;}

}