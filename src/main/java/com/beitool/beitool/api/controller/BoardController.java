package com.beitool.beitool.api.controller;

import com.beitool.beitool.api.dto.board.*;
import com.beitool.beitool.api.repository.BelongWorkInfoRepository;
import com.beitool.beitool.api.repository.MemberRepository;
import com.beitool.beitool.api.repository.StoreRepository;
import com.beitool.beitool.api.service.BoardServiceImpl;
import com.beitool.beitool.api.service.MemberKakaoApiService;
import com.beitool.beitool.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 게시판을 사용하기 위한 컨트롤러
 * 1.게시글 목록 조회
 *    1-1.게시글 목록 조회
 *    1-2.ToDoList 목록 조회
 * 2.게시글 작성
 *    2-1.공지사항 작성
 *    2-2.자유게시판 작성
 *    2-3.ToDoList 작성
 * 3.게시글 조회
 *    3-1.공지시항 조회
 *    3-2.자유게시판 조회
 * 4.게시글 삭제
 * 5.게시글 수정
 *    5-1.공지사항 수정
 *    5-2.자유게시판 수정
 *    5-3.ToDoList 수정
 * 6.기타
 *    6-1.ToDoList 업무 완료 표시
 * @author Chanos
 * @since 2022-05-06
 */
@RestController
@RequiredArgsConstructor
public class BoardController {
    private final BoardServiceImpl boardServiceImpl;
    private final MemberRepository memberRepository;
    private final MemberKakaoApiService memberKakaoApiService;
    private final BelongWorkInfoRepository belongWorkInfoRepository;

    /***--게시글 목록 조회--***/
    /*게시글 목록 조회*/
    @PostMapping("/board/read/")
    public BoardResponseDto readBoard(@RequestBody BoardRequestDto boardRequestDto) {
        Long memberId = memberKakaoApiService.getMemberInfoFromAccessToken(boardRequestDto.getAccessToken());
        Member member = memberRepository.findOne(memberId);
        String boardType = boardRequestDto.getBoardType();
        Integer page = boardRequestDto.getPage();

        return boardServiceImpl.readBoard(member, boardType, page);
    }

    /*ToDoList 목록 조회*/
    @PostMapping("/board/todo/read/")
    public ToDoListResponseDto readToDoListBoard(@RequestBody BoardRequestDto boardRequestDto) {
        Long memberId = memberKakaoApiService.getMemberInfoFromAccessToken(boardRequestDto.getAccessToken());
        Member member = memberRepository.findOne(memberId);

        return boardServiceImpl.readToDoList(member.getActiveStore());
    }

    /***--게시글 작성--***/
    /*공지사항 작성*/
    @PostMapping("/board/announcement/create/post/")
    public PostDetailResponseDto createAnnouncementPost(@RequestBody BoardRequestDto boardRequestDto) {
        Long memberId = memberKakaoApiService.getMemberInfoFromAccessToken(boardRequestDto.getAccessToken());
        Member member = memberRepository.findOne(memberId);

        Belong belongInfo = belongWorkInfoRepository.findBelongInfo(member, member.getActiveStore());//활성화된 사업장소속정보
        
        if (belongInfo.getPosition().equals(MemberPosition.President)) //사장만 공지사항 작성 가능
            return boardServiceImpl.createAnnouncementPost(member, belongInfo, boardRequestDto);
        else
            return new PostDetailResponseDto("Failed");
    }

    /*자유게시판 작성*/
    @PostMapping("/board/free/create/post/")
    public PostDetailResponseDto createFreePost(@RequestBody BoardRequestDto boardRequestDto) {
        Long memberId = memberKakaoApiService.getMemberInfoFromAccessToken(boardRequestDto.getAccessToken());
        Member member = memberRepository.findOne(memberId);

        Belong belongInfo = belongWorkInfoRepository.findBelongInfo(member, member.getActiveStore());

        return boardServiceImpl.createFreePost(member, belongInfo, boardRequestDto);
    }

    /*ToDoList 작성*/
    @PostMapping("/board/todo/create/post/")
    public ToDoListResponseDto createToDoPost(@RequestBody ToDoListRequestDto toDoListRequestDto) {
        Long memberId = memberKakaoApiService.getMemberInfoFromAccessToken(toDoListRequestDto.getAccessToken());
        Member author = memberRepository.findOne(memberId);
        Belong belongInfo = belongWorkInfoRepository.findBelongInfo(author, author.getActiveStore());

        //사장만 업무를 지시할 수 있다.
        if (belongInfo.getPosition().equals(MemberPosition.President))
            return boardServiceImpl.createToDoList(author,belongInfo, toDoListRequestDto);
        else
            return new ToDoListResponseDto("Failed");
    }

    /***--게시글 조회--***/
    /*공지사항 조회*/
    @PostMapping("/board/announcement/post/read/")
    public PostDetailResponseDto readAnnouncementPost(@RequestBody BoardRequestDto boardRequestDto) {
        Long postId = boardRequestDto.getId();
        return boardServiceImpl.readAnnouncementPost(postId);
    }

    /*자유게시판 조회*/
    @PostMapping("/board/free/post/read/")
    public PostDetailResponseDto readFreePost(@RequestBody BoardRequestDto boardRequestDto) {
        Long postId = boardRequestDto.getId();
        return boardServiceImpl.readFreePost(postId);
    }

    /***--게시글 삭제--***/
    @PostMapping("/board/post/delete/")
    public BoardResponseDto deletePost(@RequestBody BoardRequestDto boardRequestDto) {
        Long memberId = memberKakaoApiService.getMemberInfoFromAccessToken(boardRequestDto.getAccessToken());
        Member member = memberRepository.findOne(memberId);
        return boardServiceImpl.deletePost(member, boardRequestDto.getId(), boardRequestDto.getBoardType());
    }

    /***--게시글 수정--***/
    /*공지사항 수정*/
    @PostMapping("/board/announcement/post/update/")
    public PostDetailResponseDto updateAnnouncementPost(@RequestBody BoardRequestDto boardRequestDto) {
        Long memberId = memberKakaoApiService.getMemberInfoFromAccessToken(boardRequestDto.getAccessToken());
        Member member = memberRepository.findOne(memberId);
        return boardServiceImpl.updateAnnouncementPost(member, boardRequestDto);
    }

    /*자유게시판 수정*/
    @PostMapping("/board/free/post/update/")
    public PostDetailResponseDto updateFreePost(@RequestBody BoardRequestDto boardRequestDto) {
        Long memberId = memberKakaoApiService.getMemberInfoFromAccessToken(boardRequestDto.getAccessToken());
        Member member = memberRepository.findOne(memberId);
        return boardServiceImpl.updateFreePost(member, boardRequestDto);
    }

    /*ToDoList 수정*/
    @PostMapping("/board/todo/post/update/")
    public ToDoListResponseDto updateToDoListPost(@RequestBody ToDoListRequestDto toDoListRequestDto) {
        Long memberId = memberKakaoApiService.getMemberInfoFromAccessToken(toDoListRequestDto.getAccessToken());
        Member member = memberRepository.findOne(memberId);

        return boardServiceImpl.updateToDoListPost(member, toDoListRequestDto);
    }

    /*ToDoList 업무 완료 표시*/
    @PostMapping("/board/todo/clear/")
    public PostDetailResponseDto clearJob(@RequestBody Map<String, Long> params) {
        return boardServiceImpl.clearJob(params.get("id"));
    }
}