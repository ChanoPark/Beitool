package com.beitool.beitool.api.repository;

import com.beitool.beitool.domain.Belong;
import com.beitool.beitool.domain.MemberPosition;
import com.beitool.beitool.domain.Store;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * 2022-04-10
 * 사업장과 관련된 정보를 DB와 상호작용하기 위한 클래스
 * 1.사업장 생성
 * 2.사업장 단건 조회
 * 3.사업장 코드로 사업장 조회
 * 4.가입 대기 직원 목록 조회
 * @author Chanos
 * @since 2022-06-08
 */
@Repository
public class StoreRepository {
    @PersistenceContext
    private EntityManager em;

    /*1.사업장 생성*/
    public void createStore(Store store) {
        em.persist(store);
    }

    /*2.사업장 단건 조회*/
    public Store findOne(Long id) {
        return em.find(Store.class, id); // 단건조회는 이렇게 넣는다.
    }

    /*3.사업장 코드로 조회*/
    public Store findStoreByCode(int inviteCode) throws NoResultException {
        return em.createQuery("select s from Store s where s.inviteCode = :inviteCode", Store.class)
                .setParameter("inviteCode", inviteCode) // :name은 파라미터를 바인딩하는것
                .getSingleResult();
    }

    /*4.가입 대기 직원 목록 조회*/
    public List<Belong> findWaitEmployee(Store store) {
        return em.createQuery("select b from Belong b where b.store=:store and b.position=:position", Belong.class)
                .setParameter("store", store)
                .setParameter("position", MemberPosition.Waiting)
                .getResultList();
    }
}
