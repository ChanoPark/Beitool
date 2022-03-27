package com.beitool.beitool.api.repository;

import com.beitool.beitool.domain.Member;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class MemberRepository {

    @PersistenceContext
    private EntityManager em;

    /*회원 정보 저장*/
    public void save(Member member) {
        em.persist(member);
    }

}
