package com.beitool.beitool.api.repository;

import com.beitool.beitool.domain.Store;
import com.beitool.beitool.domain.WorkSchedule;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 근무 기록을 위한 Repository
 *
 * 1.근무 시프트 작성
 * 2.이전 근무 조회(Validation)
 * 4.배정된 근무 조회
 * 5.근무 예정 직원 찾기
 * 5.배정된 근무 삭제
 * 6.예정된 근무 조회(캘린더 수정)
 * 7.모든 직원의 일정 기간 근무 기록 조회(캘린더 조회)
 *
 * @author Chanos
 * @since 2022-06-22
 */
@Repository
public class WorkScheduleRepository {

    @PersistenceContext
    private EntityManager em;

    /*1.근무 시프트 작성*/
    public void createSchedule(WorkSchedule workSchedule) {
        em.persist(workSchedule);
    }

    /*2.이전 근무 조회(Validation)*/
    public List<WorkSchedule> getWorkSchedule(LocalDate workDay) throws NoResultException {
        return em.createQuery("select w from WorkSchedule w where w.workDay=:workDay", WorkSchedule.class)
                .setParameter("workDay", workDay)
                .getResultList();
    }

    /*3.배정된 근무 조회*/
    public List<WorkSchedule> readScheduleFuture(Store store, LocalDate day) {
        return em.createQuery("select w from WorkSchedule w where " +
                        "w.workDay=:workDay and w.store=:store order by w.workStartTime ASC", WorkSchedule.class)
                .setParameter("workDay", day)
                .setParameter("store", store)
                .getResultList();
    }

    /*4.근무 예정 직원 찾기*/
    public WorkSchedule findEmployee(Long postId) {
        return em.find(WorkSchedule.class, postId);
    }

    /*5.배정된 근무 삭제*/
    public void deleteSchedule(Long postId) {
        em.createQuery("delete from WorkSchedule w where w.id=:postId")
                .setParameter("postId", postId)
                .executeUpdate();
    }

    /*6.예정된 근무 조회(근무 시프트 수정)*/
    public WorkSchedule findSchedule(Long postId) {
        return em.find(WorkSchedule.class, postId);
    }


    /*7.모든 직원의 일정 기간 근무 기록 조회(캘린더 조회)*/
    public List<WorkSchedule> findAllWorkFuturePeriod(Store store, LocalDateTime firstDay, LocalDateTime lastDay){
        return em.createQuery("select s from WorkSchedule s where s.store=:store " +
                        "and s.workStartTime between :firstDay AND :lastDay", WorkSchedule.class)
                .setParameter("store", store)
                .setParameter("firstDay", firstDay)
                .setParameter("lastDay", lastDay)
                .getResultList();
    }
}