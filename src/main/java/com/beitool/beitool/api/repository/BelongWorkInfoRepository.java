package com.beitool.beitool.api.repository;

import com.beitool.beitool.domain.*;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 2022-04-11 회원-사업장 간 소속 및 근로정보를 위한 클래스
 * 1.소속 정보 생성(사업장 가입)
 * 2.출근 정보 생성(출근)
 * 3.소속되어 있는 모든 사업장의 개수 조회(로그인 시 근로정보를 보고 신규회원인지 구분)
 * 4.소속 정보 조회(회원과 사업장 정보를 통해 소속 정보 확인)
 * 5.회원이 가입되어 있는 모든 소속정보 조회
 * 6.근로 정보 조회(지도 들어갔을 때 출근상태인지 확인)
 * 7.근로 정보 수정(퇴근)
 * 8.소속되어 있는 직원 목록 조회
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

    /*소속되어 있는 모든 사업장의 개수 조회*/
    public int findBelongCount(Member member) {
        List<Belong> findBelongs = em.createQuery("select b from Belong b where b.member = :member", Belong.class)
                .setParameter("member", member)
                .getResultList();
        return findBelongs.size();
    }

    /*소속정보 조회*/
    public Belong findBelongInfo(Member member, Store store) throws NoResultException {
        return em.createQuery("select b from Belong b where b.member = :member and b.store = :store", Belong.class)
                .setParameter("member", member)
                .setParameter("store", store)
                .getSingleResult();
    }

    /*회원이 가입되어 있는 모든 소속정보 조회*/
    public List<Belong> allBelongInfo(Member member) {
        Store activeStore = member.getActiveStore();
        return em.createQuery("select b from Belong b" +
                        " where b.member = :member and b.store NOT IN :activeStoreId", Belong.class)
                .setParameter("member", member)
                .setParameter("activeStoreId", activeStore) //활성화된 사업장 제외하고 목록 조회
                .getResultList();
    }

    /*근로정보 조회*/
    public int findWorkInfo(Member member, Store store) {
        List<WorkInfo> findWorkings = em.createQuery("select w from WorkInfo w" +
                                " where w.member = :member and w.store = :store and w.workEndTime is null", WorkInfo.class)
                .setParameter("member", member)
                .setParameter("store", store)
                .getResultList();
        return findWorkings.size();
    }

    /*근로 정보 수정(퇴근)*/
    public void updateOffWork(Member member, Store store, LocalDateTime currentTime) {
        em.createQuery("update WorkInfo w set w.workEndTime = :currentTime" +
                        " where w.member = :member and w.store = :store and w.workEndTime is null") //update,delete쿼리는 대상만 가져오므로 클래스 명시X
                .setParameter("currentTime", currentTime)
                .setParameter("member", member)
                .setParameter("store", store)
                .executeUpdate();
    }

    /*소속되어 있는 직원 목록 조회*/
    public List<Belong> getBelongEmployeeList(Store store) {
        return em.createQuery("select b from Belong b where b.store=:store and b.position=:position")
                .setParameter("store", store)
                .setParameter("position", MemberPosition.Employee)
                .getResultList();
    }
}
