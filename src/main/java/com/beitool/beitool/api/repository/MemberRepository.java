package com.beitool.beitool.api.repository;

import com.beitool.beitool.api.exception.InvalidRefreshTokenException;
import com.beitool.beitool.domain.Member;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * 2022-03-28
 * 회원과 관련된 정보를 DB와 상호작용하기 위한 클래스
 * 1.회원 생성
 * 2.회원 정보 조회(using id)
 * 3.회원 정보 조회(using refresh token)
 * 4.리프레시 토큰 업데이트
 * ----------이하 예정---------
 * 5.회원 정보 수정
 * 6.회원 탈퇴
 * Implemented by Chanos
 */
@Repository
public class MemberRepository {

    @PersistenceContext
    private EntityManager em;

    /*회원 정보 저장*/
    public void save(Member member) {
        em.persist(member);
    }

    /*회원 정보 조회*/
    public Member findOne(Long id) {
        return em.find(Member.class, id); // 단건조회는 이렇게 넣는다.
    }

    /*리프레시 토큰으로 회원 조회*/ //후에 단건 조회로 변경할 필요가 있다.
    public Member findByRefreshToken(String refreshToken) {
        List<Member> findMembers = em.createQuery("select m from Member m where m.refreshToken = :refreshToken", Member.class)
                .setParameter("refreshToken", refreshToken)
                .getResultList();

        if (findMembers.isEmpty()) {
            //만약, 리프레시 토큰으로 조회되는 회원이 없다면(잘못된 토큰이거나, 등록되지 않은 토큰이라면)
            System.out.println("****예외");
            new InvalidRefreshTokenException("유효하지 않은 리프레시 토큰입니다.");
            return new Member(-1L, "InvalidUser");
        }
        return findMembers.get(0); //한명만 조회될꺼니까
    }

//    public List<Member> findByName(String name) {
//        return em.createQuery("select m from Member m where m.name = :name", Member.class)
//                .setParameter("name", name) // :name은 파라미터를 바인딩하는것
//                .getResultList();
//    }

    @Transactional //리프레시 토큰 업데이트
    public void updateRefreshToken(Member member, String newRefreshToken) {
        member.setRefreshToken(newRefreshToken);
    }
}