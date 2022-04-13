package com.beitool.beitool.api.repository;

import com.beitool.beitool.domain.Belong;
import com.beitool.beitool.domain.Member;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * 2022-04-11 회원-사업장 간 소속 및 근로정보를 위한 클래스
 * 1. 소속 정보 생성
 * 2. 소속 정보 조회(로그인 시 활용)
 *
 * Implemented by Chanos
 */
@Repository
public class BelongWorkInfoRepository {

    @PersistenceContext
    private EntityManager em;

    /*소속 정보 생성*/
    public void createBelong(Belong belong) {
        em.persist(belong);
    }

    /*소속 정보 조회*/
    public int findMemberAtBelong(Member member) {
        List<Belong> findMembers = em.createQuery("select b from Belong b where b.member = :member", Belong.class)
                .setParameter("member", member)
                .getResultList();
        return findMembers.size();
    }
}
