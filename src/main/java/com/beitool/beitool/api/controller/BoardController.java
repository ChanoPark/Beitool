package com.beitool.beitool.api.controller;

import com.beitool.beitool.api.dto.board.BoardRequestDto;
import com.beitool.beitool.api.dto.board.BoardResponseDto;
import com.beitool.beitool.api.dto.board.PostDetailResponseDto;
import com.beitool.beitool.api.repository.BelongWorkInfoRepository;
import com.beitool.beitool.api.repository.MemberRepository;
import com.beitool.beitool.api.service.BoardServiceImpl;
import com.beitool.beitool.api.service.MemberKakaoApiService;
import com.beitool.beitool.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


/**
 * 게시판을 사용하기 위한 컨트롤러
 * 1.게시판 접속
 * 2.게시글 작성
 *    2-1.공지사항 작성
 * 3.게시글 조회
 *    3-1.공지시항 조회
 * 4.게시글 삭제
 * 5.게시글 수정
 *    5-1.공지사항 수정
 * @author Chanos
 * @since 2022-04-29
 */
@RestController
@RequiredArgsConstructor
public class BoardController {
    private final BoardServiceImpl boardServiceImpl;
    private final MemberRepository memberRepository;
    private final MemberKakaoApiService memberKakaoApiService;
    private final BelongWorkInfoRepository belongWorkInfoRepository;

    /***--게시판 접속--***/
    @PostMapping("/board/read/")
    public BoardResponseDto readBoard(@RequestBody BoardRequestDto boardRequestDto) {
        Long memberId = memberKakaoApiService.getMemberInfoFromAccessToken(boardRequestDto.getAccessToken());
        Member member = memberRepository.findOne(memberId);
        String boardType = boardRequestDto.getBoardType();

        return boardServiceImpl.readBoard(member, boardType);
    }

    /***--게시글 작성--***/
    /*공지사항 작성*/
    @PostMapping("/board/announcement/create/post/")
    public PostDetailResponseDto createPost(@RequestBody BoardRequestDto boardRequestDto) {
        Long memberId = memberKakaoApiService.getMemberInfoFromAccessToken(boardRequestDto.getAccessToken());
        Member member = memberRepository.findOne(memberId);

        Belong belongInfo = belongWorkInfoRepository.findBelongInfo(member, member.getActiveStore());//활성화된 사업장소속정보
        
        if (belongInfo.getPosition().equals(MemberPosition.President)) //사장만 공지사항 작성 가능
            return boardServiceImpl.createAnnouncementPost(member, belongInfo, boardRequestDto);
        else
            return new PostDetailResponseDto("Failed");
    }

    /***--게시글 삭제--***/
    @PostMapping("/board/delete/post/")
    public BoardResponseDto deletePost(@RequestBody BoardRequestDto boardRequestDto) {
        return boardServiceImpl.deletePost(boardRequestDto.getId(), boardRequestDto.getBoardType());
    }

    /***--게시글 수정--***/
    /*공지사항 수정*/
    @PostMapping("/board/announcement/update/post/")
    public PostDetailResponseDto updatePost(@RequestBody BoardRequestDto boardRequestDto) {
        Long memberId = memberKakaoApiService.getMemberInfoFromAccessToken(boardRequestDto.getAccessToken());
        Member member = memberRepository.findOne(memberId);
        return boardServiceImpl.updateAnnouncementPost(member, boardRequestDto);
    }

    /***--게시글 조회--***/
    /*공지사항 조회*/
    @PostMapping("/board/announcement/read/post/")
    public PostDetailResponseDto readPost(@RequestBody BoardRequestDto boardRequestDto) {
        Long id = boardRequestDto.getId();
        return boardServiceImpl.readAnnouncementPost(id);
    }
}