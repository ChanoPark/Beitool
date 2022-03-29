package com.beitool.beitool.api.repository;

import com.beitool.beitool.domain.Member;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

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
//        em.find(Member.class, id);
        return em.find(Member.class, id); // 단건조회는 이렇게 넣는다.
    }

    public List<Member> findByName(String name) {
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name) // :name은 파라미터를 바인딩하는것
                .getResultList();
    }

    @Transactional //리프레시 토큰 업데이트
    public void updateRefreshToken(Member member, String newRefreshToken) {
        member.setRefreshToken(newRefreshToken);
    }

}
