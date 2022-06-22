package com.beitool.beitool.api.repository;

import com.beitool.beitool.domain.*;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 회원-사업장 간 근로 정보를 위한 Repository
 * 이미 근무한 기록과 관련한 데이터 처리
 *
 * 1.출근 정보 생성(출근)
 * 2.근로 정보 조회(지도 들어갔을 때 출근상태인지 확인)
 * 3.근로 정보 수정(퇴근)
 * 4.근무 기록 조회(해당 날짜의 근무기록)
 * 5.일정 기간 근무 기록 조회(급여계산기)
 * 6.일정 기간 근무 예정 조회(급여계산기)
 * 7.모든 직원의 일정 기간 예정된 근무 조회(캘린더 조회)
 * @author Chanos
 * @since 2022-05-25
 */
@Repository
public class WorkInfoRepository {

    @PersistenceContext
    private EntityManager em;

    /*1.출근 정보 생성*/
    public void createWorkInfo(WorkInfo workInfo) {
        em.persist(workInfo);
    }

    /*2.근로정보 조회*/
    public int findWorkInfo(Member member, Store store) {
        List<WorkInfo> findWorkings = em.createQuery("select w from WorkInfo w" +
                                " where w.member = :member and w.store = :store and w.workEndTime is null", WorkInfo.class)
                .setParameter("member", member)
                .setParameter("store", store)
                .getResultList();
        return findWorkings.size();
    }

    /*3.근로 정보 수정(퇴근)*/
    public void updateOffWork(Member member, Store store, LocalDateTime currentTime) {
        em.createQuery("update WorkInfo w set w.workEndTime = :currentTime" +
                        " where w.member = :member and w.store = :store and w.workEndTime is null") //update,delete쿼리는 대상만 가져오므로 클래스 명시X
                .setParameter("currentTime", currentTime)
                .setParameter("member", member)
                .setParameter("store", store)
                .executeUpdate();
    }

    /*4.근무 기록 조회(해당 날짜의 근무기록)*/
    public List<WorkInfo> readScheduleHistory(Store store, LocalDate day) {
        return em.createQuery("select w from WorkInfo w where " +
                        "w.workDay=:workDay and w.store=:store order by w.workStartTime ASC", WorkInfo.class)
                .setParameter("workDay", day)
                .setParameter("store", store)
                .getResultList();
    }

    /*5.일정 기간 근무기록 조회*/
    public List<WorkInfo> findWorkHistoryPeriod(Member member, Store store, LocalDateTime firstDay, LocalDateTime lastDay){
        return em.createQuery("select b from WorkInfo b where b.store=:store and b.member=:member " +
                "and b.workStartTime between :firstDay AND :lastDay", WorkInfo.class)
                .setParameter("member", member)
                .setParameter("store", store)
                .setParameter("firstDay", firstDay)
                .setParameter("lastDay", lastDay)
                .getResultList();
    }

    /*6.모든 직원의 일정 기간 예정된 근무 조회(캘린더 조회)*/
    public List<WorkInfo> findAllWorkHistoryPeriod(Store store, LocalDateTime firstDay, LocalDateTime lastDay){
        return em.createQuery("select b from WorkInfo b where b.store=:store " +
                        "and b.workStartTime between :firstDay AND :lastDay", WorkInfo.class)
                .setParameter("store", store)
                .setParameter("firstDay", firstDay)
                .setParameter("lastDay", lastDay)
                .getResultList();
    }
}