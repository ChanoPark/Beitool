package com.beitool.beitool.api.repository;

import com.beitool.beitool.domain.Belong;
import com.beitool.beitool.domain.Member;
import com.beitool.beitool.domain.MemberPosition;
import com.beitool.beitool.domain.Store;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * 회원 - 사업장 간 소속 관계를 위한 Repository
 * 1.소속 정보 생성
 * 2.회원이 소속된 사업장의 개수 조회(로그인 시 근로정보를 보고 신규회원인지 구분)
 * 3.소속 정보 조회(회원과 사업장 정보를 통해 소속 정보 확인)
 * 4.회원의 모든 소속 정보 조회
 * 5.해당 사업장에 소속되어 있는 직원 목록 조회
 * 6.해당 사업장의 가입 대기 직원 목록 조회
 * 7.사업장 내 중복된 이름 존재 여부 검사
 * 8.가입 대기중인 직원 승인하기
 * @author Chanos
 * @since 2022-06-22
 */
@Repository
public class BelongRepository {

    @PersistenceContext
    private EntityManager em;

    /*1.소속 정보 생성*/
    public void createBelong(Belong belong) {
        em.persist(belong);
    }

    /**----조회----**/

    /*2.회원이 소속된 사업장의 개수 조회*/
    public int findBelongCount(Member member) {
        List<Belong> findBelongs = em.createQuery("select b from Belong b where b.member = :member", Belong.class)
                .setParameter("member", member)
                .getResultList();
        return findBelongs.size();
    }

    /*3.소속 정보 조회*/
    public Belong findBelongInfo(Member member, Store store) throws NoResultException {
        return em.createQuery("select b from Belong b where b.member = :member and b.store = :store", Belong.class)
                .setParameter("member", member)
                .setParameter("store", store)
                .getSingleResult();
    }

    /*4.회원의 모든 소속 정보 조회*/
    public List<Belong> allBelongInfo(Member member) {
        Store activeStore = member.getActiveStore();
        return em.createQuery("select b from Belong b" +
                        " where b.member = :member and b.store NOT IN :activeStoreId", Belong.class)
                .setParameter("member", member)
                .setParameter("activeStoreId", activeStore) //활성화된 사업장 제외하고 목록 조회
                .getResultList();
    }

    /*5.해당 사업장에 소속되어 있는 직원 목록 조회*/
    public List<Belong> getBelongEmployeeList(Store store) {
        return em.createQuery("select b from Belong b where b.store=:store and b.position=:position",Belong.class)
                .setParameter("store", store)
                .setParameter("position", MemberPosition.Employee)
                .getResultList();
    }

    /*6.해당 사업장의 가입 대기 직원 목록 조회*/
    public List<Belong> findWaitEmployee(Store store) {
        return em.createQuery("select b from Belong b where b.store=:store and b.position=:position", Belong.class)
                .setParameter("store", store)
                .setParameter("position", MemberPosition.Waiting)
                .getResultList();
    }

    /*7.사업장 내 중복된 이름 존재 여부 검사*/
    public void findName(Store store, String userName) throws NoResultException {
        em.createQuery("select b.name from Belong b where " +
                        "b.store =:store and b.name =:name and (b.position<>:position)", String.class)
                .setParameter("store", store)
                .setParameter("name", userName)
                .setParameter("position", MemberPosition.Waiting)
                .getSingleResult();
        //만약,겹치는 이름이 있으면 예외 발생
    }

    /*8.가입 대기중인 직원 승인하기*/
    public Belong allowNewEmployee(Member employee, Store store) {
        return em.createQuery("select b from Belong b where b.member=:employee and b.store=:store", Belong.class)
                .setParameter("employee", employee)
                .setParameter("store", store)
                .getSingleResult();
    }
}
