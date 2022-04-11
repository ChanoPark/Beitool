package com.beitool.beitool.api.repository;

import com.beitool.beitool.domain.Belong;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * 2022-04-11 회원-사업장 간 소속 및 근로정보를 위한 클래스
 * 1. 소속 정보 생성
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

}
