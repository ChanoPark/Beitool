package com.beitool.beitool.api.repository;

import com.beitool.beitool.domain.board.Announcement;
import com.beitool.beitool.domain.board.BoardDomain;
import com.beitool.beitool.domain.Member;
import com.beitool.beitool.domain.Store;
import com.beitool.beitool.domain.board.Free;
import com.beitool.beitool.domain.board.ToDoList;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * 각종 게시판과 관련된 서비스를 제공하기 위해 DB와 상호작용하는 Repository
 * 1.게시글 목록 조회
 * 2.전체 게시글 개수 조회(페이징)
 * 3.게시글 조회
 *    3-1.게시글 조회
 *    3-2.ToDoList 조회 (제목과 게시글이 분리되어 있지 않기 때문에)
 * 4.게시글 작성
 * 5.게시글 삭제
 * 6.글쓴이 찾기
 * 7.게시글 조회(for dirty checking - 준영속 상태 엔티티)
 *    7-1.공지사항 조회
 *    7-2.자유게시판 조회
 *    7-3.ToDoList 조회
 * @author Chanos
 * @since 2022-04-29
 */
@Repository
@Transactional
public class BoardRepository<T extends BoardDomain> {

    @PersistenceContext
    private EntityManager em;

    /*게시글 목록 조회*/
    public List<BoardDomain> readBoard(Store store, String boardType, Integer page) {
        List<BoardDomain> findPosts = em.createQuery("select b from BoardDomain b" +
                " where b.store = :store and b.dtype = :boardType", BoardDomain.class)
                .setParameter("store", store)
                .setParameter("boardType", boardType)
                .setFirstResult(page*10)
                .setMaxResults(10)
                .getResultList();
        return findPosts;
    }

    /*전체 게시글 개수 조회(페이징)*/
    public Long countPost(Store store, String boardType) {
        return (Long) em.createQuery("select count(b) from BoardDomain b " +
                        "where b.store = :store and b.dtype = :boardType")
                .setParameter("store", store)
                .setParameter("boardType", boardType)
                .getSingleResult();
    }


    /***--게시글 조회--***/
    /*게시글 조회*/
    public Object readPost(Long id, Object board) throws NoResultException {
        return em.createQuery("select b from BoardDomain b " +
                        "where type(b) IN (:board) and b.id = :id")
                .setParameter("board", board.getClass())
                .setParameter("id", id)
                .getSingleResult();
    }

    /*ToDoList 조회*/
    public List<BoardDomain> readToDoListPost(Store store, Object board) {
        return em.createQuery("select b from BoardDomain b where type(b) IN (:board) and b.store = :store")
                .setParameter("board", board.getClass())
                .setParameter("store", store)
                .getResultList();
    }

    /*게시글 작성*/
    public Long createPost(T newPost) {
        em.persist(newPost);
        em.flush();
        return newPost.getId();
    }

    /*게시글 삭제*/
    public void deletePost(Long id, String boardType) throws NoResultException {
        em.createQuery("delete from BoardDomain b where b.dtype = :boardType and b.id=:id")
                .setParameter("boardType", boardType)
                .setParameter("id", id)
                .executeUpdate();
    }

    /*글쓴이 찾기*/
    public Member findAuthor(Long id) {
        return (Member) em.createQuery("select b.member from BoardDomain b where b.id=:id")
                .setParameter("id", id)
                .getSingleResult();
    }
    /***--게시글 조회--***/
    /*공지사항 조회*/
    public Announcement findAnnouncementPost(Long id) {
        return em.find(Announcement.class, id);
    }
    /*자유게시판 조회*/
    public Free findFreePost(Long id) {
        return em.find(Free.class, id);
    }
    /*ToDoList 조회*/
    public ToDoList findToDoListPost(Long id) {return em.find(ToDoList.class, id);}
}
