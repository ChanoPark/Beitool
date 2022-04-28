package com.beitool.beitool.api.service;

import com.beitool.beitool.api.dto.BoardRequestDto;
import com.beitool.beitool.api.dto.BoardResponseDto;
import com.beitool.beitool.domain.Member;

import java.util.Map;

/**
 * 게시판의 역할과 기능을 분리하기 위한 인터페이스
 * 기본적으로 게시판은 CRUD를 지원하고, 엑세스 토큰을 받아 활성화된 사업장을 기준으로 데이터를 처리한다.
 *
 * createPost(게시글 생성): 엑세스토큰 + 제목 + 내용
 * updatePost(게시글 수정): 엑세스토큰 + 제목 + 내용
 * readBoard(게시글 목록 조회) : 엑세스토큰 -> URL 파라미터로 게시판 이름(dtype과 매핑)을 받아서 컨트롤러에서 서비스로 맞춰서 뿌려주는 역할
 * readPost(게시글 상세 조회)  : 엑세스토큰 -> URL 파라미터로 게시글 번호를 받아서 게시글 조회
 * deletePost(게시글 삭제): 엑세스토큰 -> URL 파라미터로 게시글 번호를 받아서 게시글 삭제
 *
 * @author Chanos
 * @since 2022-04-24
 */
public interface BoardService {
    BoardResponseDto createPost(String accessToken, String title, String content); //게시글 작성
    BoardResponseDto updatePost(String accessToken, String title, String content); //게시글 수정
    BoardResponseDto readBoard(Member member, String accessToken); //게시판 조회
    BoardResponseDto readPost(Long postId, String accessToken); //게시글 조회
    BoardResponseDto deletePost(Long postId, String accessToken); //게시글 삭제
}