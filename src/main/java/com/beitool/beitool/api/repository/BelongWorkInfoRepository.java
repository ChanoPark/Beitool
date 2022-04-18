package com.beitool.beitool.api.repository;

import com.beitool.beitool.domain.Belong;
import com.beitool.beitool.domain.Member;
import com.beitool.beitool.domain.Store;
import com.beitool.beitool.domain.WorkInfo;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 2022-04-11 회원-사업장 간 소속 및 근로정보를 위한 클래스
 * 1.소속 정보 생성(사업장 가입)
 * 2.출근 정보 생성(출근)
 * 3.소속 정보 조회(로그인 시 근로정보를 보고 신규회원인지 구분)
 * 4.근로 정보 수정(퇴근)
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

    /*출근 정보 생성*/
    public void createWorkInfo(WorkInfo workInfo) {
        em.persist(workInfo);}

    /*소속 정보 조회*/
    public int findMemberAtBelong(Member member) {
        List<Belong> findMembers = em.createQuery("select b from Belong b where b.member = :member", Belong.class)
                .setParameter("member", member)
                .getResultList();
        return findMembers.size();
    }
//
//    /*근로정보 조회*/
//    public WorkInfo findWorkInfo(Member member, Store store) {
//        return em.createQuery("select w from WorkInfo w" +
//                                " where w.member = :member and w.store = :store and w.workEndTime is null", WorkInfo.class)
//                .setParameter("member", member)
//                .setParameter("store", store)
//                .getSingleResult();
//    }

    /*퇴근(근로정보 수정)*/
    public WorkInfo findWorkInfo(Member member, Store store, LocalDateTime currentTime) { //dateTime 이거 type 맞춰야될듯
        return em.createQuery("update WorkInfo w set w.workEndTime = :currentTime" +
                        " where w.member = :member and w.store = :store and w.workEndTime is null", WorkInfo.class)
                .setParameter("currentTime", currentTime)
                .setParameter("member", member)
                .setParameter("store", store)
                .getSingleResult();

    }
}
