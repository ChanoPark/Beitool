package com.beitool.beitool.api.service;

import com.beitool.beitool.api.dto.board.BoardRequestDto;
import com.beitool.beitool.api.dto.board.BoardResponseDto;
import com.beitool.beitool.api.dto.board.PostDetailResponseDto;
import com.beitool.beitool.api.repository.BoardRepository;
import com.beitool.beitool.domain.*;
import com.beitool.beitool.domain.board.Announcement;
import com.beitool.beitool.domain.board.BoardDomain;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.jni.Local;
import org.springframework.stereotype.Service;

import javax.persistence.NoResultException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * BoardService를 상속받아 CURD를 제공하는 구현체
 * 1.게시글 목록 조회
 * 2.게시글 작성
 * @author Chanos
 * @since 2022-04-24
 */
@Service
@RequiredArgsConstructor
public class BoardServiceImpl {
    private final BoardRepository boardRepository;

    /*게시글 목록 조회*/
    public BoardResponseDto readBoard(Member member, String boardType) {
        Store store = member.getActiveStore();

        BoardResponseDto boardResponseDto = new BoardResponseDto();
        List<BoardDomain> posts = boardRepository.readBoard(store, boardType); //BoardDomain List가 날라옴.

        for (BoardDomain post : posts) {
            boardResponseDto.setPosts(post);
        }

        return boardResponseDto;
    }


    /*공지사항 작성*/
    public PostDetailResponseDto createAnnouncementPost(Member member, Belong belongInfo, BoardRequestDto boardRequestDto) {
        Store store = member.getActiveStore();
        String authorName = belongInfo.getName();
        String content = boardRequestDto.getContent();
        String title = boardRequestDto.getTitle();
        LocalDateTime createdTime = LocalDateTime.now();
        Announcement announcement = new Announcement(authorName, content, createdTime, member, store, title);
        Long postId = boardRepository.createPost(announcement);

        return new PostDetailResponseDto(title, content, postId, authorName, createdTime, "Success");
    }

    /*게시글 삭제*/
    public BoardResponseDto deletePost(Long id, String boardType) {
        boardRepository.deletePost(id, boardType);
        return new BoardResponseDto("성공적으로 삭제되었습니다.");
        //*******없는거 삭제해도 성공적으로 삭제되었다고 뜸
        //없는 게시글 삭제 요청이 들어올수가있나 근데?
    }

    /*공지사항 수정*/
    public PostDetailResponseDto updateAnnouncementPost(Member member, BoardRequestDto boardRequestDto) {
        Long postId = boardRequestDto.getId();

        Member author = boardRepository.findAuthor(boardRequestDto.getId());

        if(author.equals(member)) { //글쓴사람과 같지 않으면 수정할 수 없음.
            Announcement findPost = boardRepository.findAnnouncementPost(postId);
            findPost.updatePost(boardRequestDto.getTitle(), boardRequestDto.getContent());
            LocalDateTime modifiedTime = LocalDateTime.now();
            return new PostDetailResponseDto(findPost.getTitle(), findPost.getContent(),
                    postId, findPost.getAuthorName(), modifiedTime, "Success");
        } else {
            return new PostDetailResponseDto("Failed");
        }
    }

    /*공지사항 게시글 조회*/
    public PostDetailResponseDto readAnnouncementPost(Long postId) {
        Announcement findPost = new Announcement();
        try {
            findPost = (Announcement) boardRepository.readPost(postId, findPost);
            LocalDateTime createdTime = LocalDateTime.now();
            return new PostDetailResponseDto(findPost.getTitle(), findPost.getContent(),
                    postId, findPost.getAuthorName(), createdTime, "Success");
        } catch (NoResultException e) {
            return new PostDetailResponseDto("Failed");
        }
    }
}