package com.beitool.beitool.api.repository;

import com.beitool.beitool.domain.*;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 회원-사업장 간 소속 및 근로정보 데이터 접근을 위한 레포지토리
 *
 * 1.소속 정보 생성(사업장 가입)
 * 2.출근 정보 생성(출근)
 * 3.소속되어 있는 모든 사업장의 개수 조회(로그인 시 근로정보를 보고 신규회원인지 구분)
 * 4.소속 정보 조회(회원과 사업장 정보를 통해 소속 정보 확인)
 * 5.회원이 가입되어 있는 모든 소속정보 조회
 * 6.근로 정보 조회(지도 들어갔을 때 출근상태인지 확인)
 * 7.근로 정보 수정(퇴근)
 * 8.소속되어 있는 직원 목록 조회
 * 9.근무 시프트 작성
 * 10.이전 근무 조회(Validation)
 * 11.근무 기록 조회(해당 날짜의 근무기록)
 * 12.배정된 근무 조회
 * 13.근무 예정 직원 찾기
 * 14.배정된 근무 삭제
 * 15.예정된 근무 조회(캘린더 수정)
 * 16.일정 기간 근무 기록 조회(급여계산기)
 * 17.일정 기간 근무 예정 조회(급여계산기)
 * 18.모든 직원의 일정 기간 예정된 근무 조회(캘린더 조회)
 * 19.모든 직원의 일정 기간 근무 기록 조회(캘린더 조회)
 * 20.가입 대기 직원 목록 조회
 * 21.사업장 내 중복된 이름 존재 여부 검사
 * 22.가입 대기중인 직원 승인하기
 * @author Chanos
 * @since 2022-05-25
 */
@Repository
public class BelongWorkInfoRepository {

    @PersistenceContext
    private EntityManager em;

    /*1.소속 정보 생성*/
    public void createBelong(Belong belong) {
        em.persist(belong);
    }

    /*2.출근 정보 생성*/
    public void createWorkInfo(WorkInfo workInfo) {
        em.persist(workInfo);
    }

    /*3.소속되어 있는 모든 사업장의 개수 조회*/
    public int findBelongCount(Member member) {
        List<Belong> findBelongs = em.createQuery("select b from Belong b where b.member = :member", Belong.class)
                .setParameter("member", member)
                .getResultList();
        return findBelongs.size();
    }

    /*4.소속정보 조회*/
    public Belong findBelongInfo(Member member, Store store) throws NoResultException {
        return em.createQuery("select b from Belong b where b.member = :member and b.store = :store", Belong.class)
                .setParameter("member", member)
                .setParameter("store", store)
                .getSingleResult();
    }

    /*5.회원이 가입되어 있는 모든 소속정보 조회*/
    public List<Belong> allBelongInfo(Member member) {
        Store activeStore = member.getActiveStore();
        return em.createQuery("select b from Belong b" +
                        " where b.member = :member and b.store NOT IN :activeStoreId", Belong.class)
                .setParameter("member", member)
                .setParameter("activeStoreId", activeStore) //활성화된 사업장 제외하고 목록 조회
                .getResultList();
    }

    /*6.근로정보 조회*/
    public int findWorkInfo(Member member, Store store) {
        List<WorkInfo> findWorkings = em.createQuery("select w from WorkInfo w" +
                                " where w.member = :member and w.store = :store and w.workEndTime is null", WorkInfo.class)
                .setParameter("member", member)
                .setParameter("store", store)
                .getResultList();
        return findWorkings.size();
    }

    /*7.근로 정보 수정(퇴근)*/
    public void updateOffWork(Member member, Store store, LocalDateTime currentTime) {
        em.createQuery("update WorkInfo w set w.workEndTime = :currentTime" +
                        " where w.member = :member and w.store = :store and w.workEndTime is null") //update,delete쿼리는 대상만 가져오므로 클래스 명시X
                .setParameter("currentTime", currentTime)
                .setParameter("member", member)
                .setParameter("store", store)
                .executeUpdate();
    }

    /*8.소속되어 있는 직원 목록 조회*/
    public List<Belong> getBelongEmployeeList(Store store) {
        return em.createQuery("select b from Belong b where b.store=:store and b.position=:position",Belong.class)
                .setParameter("store", store)
                .setParameter("position", MemberPosition.Employee)
                .getResultList();
    }

    /*9.근무 시프트 작성*/
    public void createSchedule(WorkSchedule workSchedule) {
        em.persist(workSchedule);
    }

    /*10.이전 근무 조회(Validation)*/
    public List<WorkSchedule> getWorkSchedule(LocalDate workDay) throws NoResultException{
        return em.createQuery("select w from WorkSchedule w where w.workDay=:workDay", WorkSchedule.class)
                .setParameter("workDay", workDay)
                .getResultList();
    }

    /*11.근무 기록 조회(해당 날짜의 근무기록)*/
    public List<WorkInfo> readScheduleHistory(Store store, LocalDate day) {
        return em.createQuery("select w from WorkInfo w where " +
                        "w.workDay=:workDay and w.store=:store order by w.workStartTime ASC", WorkInfo.class)
                .setParameter("workDay", day)
                .setParameter("store", store)
                .getResultList();
    }

    /*12.배정된 근무 조회*/
    public List<WorkSchedule> readScheduleFuture(Store store, LocalDate day) {
        return em.createQuery("select w from WorkSchedule w where " +
                        "w.workDay=:workDay and w.store=:store order by w.workStartTime ASC", WorkSchedule.class)
                .setParameter("workDay", day)
                .setParameter("store", store)
                .getResultList();
    }

    /*13.근무 예정 직원 찾기*/
    public WorkSchedule findEmployee(Long postId) {
        return em.find(WorkSchedule.class, postId);
    }

    /*14.배정된 근무 삭제*/
    public void deleteSchedule(Long postId) {
        em.createQuery("delete from WorkSchedule w where w.id=:postId")
                .setParameter("postId", postId)
                .executeUpdate();
    }

    /*15.예정된 근무 조회(근무 시프트 수정)*/
    public WorkSchedule findSchedule(Long postId) {
        return em.find(WorkSchedule.class, postId);
    }

    /*16.일정 기간 근무기록 조회*/
    public List<WorkInfo> findWorkHistoryPeriod(Member member, Store store, LocalDateTime firstDay, LocalDateTime lastDay){
        return em.createQuery("select b from WorkInfo b where b.store=:store and b.member=:member " +
                "and b.workStartTime between :firstDay AND :lastDay", WorkInfo.class)
                .setParameter("member", member)
                .setParameter("store", store)
                .setParameter("firstDay", firstDay)
                .setParameter("lastDay", lastDay)
                .getResultList();
    }

    /*17.일정 기간 근무예정 조회*/
    public List<WorkSchedule> findWorkFuturePeriod(Member employee, Store store, LocalDateTime firstDay, LocalDateTime lastDay){
        return em.createQuery("select s from WorkSchedule s where s.store=:store and s.employee=:employee " +
                        "and s.workStartTime between :firstDay AND :lastDay", WorkSchedule.class)
                .setParameter("employee", employee)
                .setParameter("store", store)
                .setParameter("firstDay", firstDay)
                .setParameter("lastDay", lastDay)
                .getResultList();
    }

    /*18.모든 직원의 일정 기간 예정된 근무 조회(캘린더 조회)*/
    public List<WorkInfo> findAllWorkHistoryPeriod(Store store, LocalDateTime firstDay, LocalDateTime lastDay){
        return em.createQuery("select b from WorkInfo b where b.store=:store " +
                        "and b.workStartTime between :firstDay AND :lastDay", WorkInfo.class)
                .setParameter("store", store)
                .setParameter("firstDay", firstDay)
                .setParameter("lastDay", lastDay)
                .getResultList();
    }



    /*19.모든 직원의 일정 기간 근무 기록 조회(캘린더 조회)*/
    public List<WorkSchedule> findAllWorkFuturePeriod(Store store, LocalDateTime firstDay, LocalDateTime lastDay){
        return em.createQuery("select s from WorkSchedule s where s.store=:store " +
                        "and s.workStartTime between :firstDay AND :lastDay", WorkSchedule.class)
                .setParameter("store", store)
                .setParameter("firstDay", firstDay)
                .setParameter("lastDay", lastDay)
                .getResultList();
    }

    /*20.가입 대기 직원 목록 조회*/
    public List<Belong> findWaitEmployee(Store store) {
        return em.createQuery("select b from Belong b where b.store=:store and b.position=:position", Belong.class)
                .setParameter("store", store)
                .setParameter("position", MemberPosition.Waiting)
                .getResultList();
    }

    /*21.사업장 내 중복된 이름 존재 여부 검사*/
    public void findName(Store store, String userName) throws NoResultException {
        String findName = em.createQuery("select b.name from Belong b where " +
                        "b.store =:store and b.name =:name and (b.position<>:position)", String.class)
                .setParameter("store", store)
                .setParameter("name", userName)
                .setParameter("position", MemberPosition.Waiting)
                .getSingleResult();

        //만약, 겹치는 일름이 있으면 예외 발생
    }

    /*22.가입 대기중인 직원 승인하기*/
    public Belong allowNewEmployee(Member employee, Store store) {
        return em.createQuery("select b from Belong b where b.member=:employee and b.store=:store", Belong.class)
                .setParameter("employee", employee)
                .setParameter("store", store)
                .getSingleResult();
    }
}