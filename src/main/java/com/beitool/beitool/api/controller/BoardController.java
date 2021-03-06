package com.beitool.beitool.api.controller;

import com.beitool.beitool.api.dto.board.*;
import com.beitool.beitool.api.repository.BelongRepository;
import com.beitool.beitool.api.repository.MemberRepository;
import com.beitool.beitool.api.service.AmazonS3Service;
import com.beitool.beitool.api.service.BoardServiceImpl;
import com.beitool.beitool.api.service.MemberKakaoApiService;
import com.beitool.beitool.domain.*;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 게시판을 사용하기 위한 컨트롤러
 * 1.게시글 목록 조회
 *    1-1.게시글 목록 조회
 *    1-2.ToDoList 목록 조회
 *    1-3.재고관리 목록 조회
 * 2.게시글 작성
 *    2-1.공지사항 작성
 *    2-2.자유게시판 작성
 *    2-3.ToDoList 작성
 *    2-4.재고관리 작성
 * 3.게시글 조회
 *    3-1.공지시항 조회
 *    3-2.자유게시판 조회
 *    3-3.재고관리 조회
 * 4.게시글 삭제
 *    4-1.게시글 삭제
 *    4-2.재고관리 게시글 삭제
 * 5.게시글 수정
 *    5-1.공지사항 수정
 *    5-2.자유게시판 수정
 *    5-3.ToDoList 수정
 *    5-4.재고관리 수정
 * 6.기타
 *    6-1.ToDoList 업무 완료 표시
 *
 * @author Chanos
 * @since 2022-05-20
 */

@Api(tags = "게시판")
@Slf4j
@RequiredArgsConstructor
@RestController
public class BoardController {
    private final BoardServiceImpl boardServiceImpl;
    private final MemberRepository memberRepository;
    private final MemberKakaoApiService memberKakaoApiService;
    private final BelongRepository belongRepository;
    private final AmazonS3Service amazonS3Service;
    private final HttpServletRequest request;

    /***--1.게시글 목록 조회--***/
    /*1-1.게시글 목록 조회*/
    @Operation(summary="게시글 목록 조회", description = "자유 게시판, 공지사항 게시글 목록 조회")
    @PostMapping("/board/read/")
    public BoardResponseDto readBoard(@RequestBody BoardRequestDto boardRequestDto) {
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        Long memberId = memberKakaoApiService.getMemberInfoFromAccessToken(accessToken);
        Member member = memberRepository.findOne(memberId);
        String boardType = boardRequestDto.getBoardType();
        Integer page = boardRequestDto.getPage()-1; // 0~9, 10~19 와 같이 게시글을 표현해서 0부터 시작해야됌.

        return boardServiceImpl.readBoard(member, boardType, page);
    }

    /*1-2.ToDoList 목록 조회*/
    @Operation(summary="ToDoList 목록 조회", description = "자유게시판:Free / 공지사항:Announcement / 투두리스트:ToDo / 재고관리: Stock")
    @PostMapping("/board/todo/read/")
    public ToDoListResponseDto readToDoListBoard(@RequestBody BoardRequestDto boardRequestDto) {
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        Long memberId = memberKakaoApiService.getMemberInfoFromAccessToken(accessToken);
        Member member = memberRepository.findOne(memberId);
        return boardServiceImpl.readToDoList(member, member.getActiveStore());
    }

    /*1-3.재고관리 목록 조회*/
    @Operation(summary = "재고관리 목록 조회")
    @PostMapping("/board/stock/read/")
    public StockReadResponseDto readStockListBoard() {
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        Long memberId = memberKakaoApiService.getMemberInfoFromAccessToken(accessToken);
        Store store = memberRepository.findOne(memberId).getActiveStore();
        return boardServiceImpl.readStockListBoard(store);
    }

    /***--2.게시글 작성--***/
    /*2-1.공지사항 작성*/
    @Operation(summary = "공지사항 작성")
    @PostMapping("/board/announcement/create/post/")
    public PostDetailResponseDto createAnnouncementPost(@RequestBody BoardRequestDto boardRequestDto) {
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        Long memberId = memberKakaoApiService.getMemberInfoFromAccessToken(accessToken);
        Member member = memberRepository.findOne(memberId);

        Belong belongInfo = belongRepository.findBelongInfo(member, member.getActiveStore());//활성화된 사업장소속정보
        
        if (belongInfo.getPosition().equals(MemberPosition.President)) { //사장만 공지사항 작성 가능
            return boardServiceImpl.createAnnouncementPost(member, belongInfo, boardRequestDto);
        }
        else {
            log.warn("**공지사항 작성 실패 - 사장만 공지사항 작성 가능");
            return new PostDetailResponseDto("Failed");
        }
    }

    /*2-2.자유게시판 작성*/
    @Operation(summary = "자유게시판 작성")
    @PostMapping("/board/free/create/post/")
    public PostDetailResponseDto createFreePost(@RequestBody BoardRequestDto boardRequestDto) {
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        Long memberId = memberKakaoApiService.getMemberInfoFromAccessToken(accessToken);
        Member member = memberRepository.findOne(memberId);

        Belong belongInfo = belongRepository.findBelongInfo(member, member.getActiveStore());

        return boardServiceImpl.createFreePost(member, belongInfo, boardRequestDto);
    }

    /*2-3.ToDoList 작성*/
    @Operation(summary = "ToDoList 작성")
    @PostMapping("/board/todo/create/post/")
    public ToDoListResponseDto createToDoPost(@RequestBody ToDoListRequestDto toDoListRequestDto) {
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        Long memberId = memberKakaoApiService.getMemberInfoFromAccessToken(accessToken);
        Member author = memberRepository.findOne(memberId);
        Belong belongInfo = belongRepository.findBelongInfo(author, author.getActiveStore());
        //사장만 업무를 지시할 수 있다.
        if (belongInfo.getPosition().equals(MemberPosition.President)) {
            return boardServiceImpl.createToDoPost(author, belongInfo, toDoListRequestDto);
        }
        else {
            log.warn("**ToDoList 작성 실패 - 사장만 업무 지시 가능");
            return new ToDoListResponseDto("Failed");
        }
    }

    /*2-4.재고관리 작성*/
    @Operation(summary = "재고관리 작성", description = "S3에 업로드 된 사진의 이름과 경로를 포함해야 함.")
    @PostMapping("/board/stock/create/post/")
    public StockResponseDto createStockPost(@RequestBody StockRequestDto stockRequestDto) {
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        Long memberId = memberKakaoApiService.getMemberInfoFromAccessToken(accessToken);
        Member member = memberRepository.findOne(memberId);
        return boardServiceImpl.createStockPost(stockRequestDto, member);
    }

    /***--3.게시글 조회--***/
    /*3-1.공지사항 조회*/
    @Operation(summary = "공지사항 조회")
    @PostMapping("/board/announcement/post/read/")
    public PostDetailResponseDto readAnnouncementPost(@RequestBody BoardRequestDto boardRequestDto) {
        Long postId = boardRequestDto.getId();
        return boardServiceImpl.readAnnouncementPost(postId);
    }

    /*3-2.자유게시판 조회*/
    @Operation(summary = "자유 게시판 조회")
    @PostMapping("/board/free/post/read/")
    public PostDetailResponseDto readFreePost(@RequestParam("id") Long id) {
        return boardServiceImpl.readFreePost(id);
    }

    /*3-3.재고관리 조회*/
    @Operation(summary = "재고 관리 조회")
    @PostMapping("/board/stock/post/read/")
    public StockResponseDto readStockPost(@RequestParam("id") Long id) {
        return boardServiceImpl.readStockPost(id);
    }

    /***--4.게시글 삭제--***/
    /*4-1.게시글 삭제*/
    @Operation(summary = "게시글 삭제", description = "재고관리 제외, 게시판 종류 입력이 필요함.")
    @PostMapping("/board/post/delete/")
    public BoardResponseDto deletePost(@RequestBody BoardRequestDto boardRequestDto) {
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        Long memberId = memberKakaoApiService.getMemberInfoFromAccessToken(accessToken);
        Member member = memberRepository.findOne(memberId);

        boolean result = boardServiceImpl.deletePost(member, boardRequestDto.getId(), boardRequestDto.getBoardType());
        return new BoardResponseDto(result);
    }

    /*4-2.재고관리 게시글 삭제*/
    @Operation(summary = "재고관리 게시글 삭제", description = "S3에 업로드된 사진을 먼저 삭제해야 함.")
    @PostMapping("/board/stock/post/delete/")
    public StockReadResponseDto deleteStockPost(@RequestBody StockDeleteRequestDto stockDeleteRequestDto) {
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        //게시글 삭제 과정
        Long memberId = memberKakaoApiService.getMemberInfoFromAccessToken(accessToken);
        Member member = memberRepository.findOne(memberId);
        Long id = stockDeleteRequestDto.getId();
        boolean deleteResult = boardServiceImpl.deletePost(member, id, "Stock");

        //사진 삭제 과정
        if (deleteResult) {
            amazonS3Service.deleteFile(stockDeleteRequestDto.getProductFileName());
            return new StockReadResponseDto("Success");
        } else {
            log.info("**재고관리 게시글 삭제 실패 - 파일이 삭제되지 않음, 파일이름:{}", stockDeleteRequestDto.getProductFileName());
            return new StockReadResponseDto("Failed");
        }
    }

    /***--5.게시글 수정--***/
    /*5-1.공지사항 수정*/
    @Operation(summary = "공지사항 수정")
    @PostMapping("/board/announcement/post/update/")
    public PostDetailResponseDto updateAnnouncementPost(@RequestBody BoardRequestDto boardRequestDto) {
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        Long memberId = memberKakaoApiService.getMemberInfoFromAccessToken(accessToken);
        Member member = memberRepository.findOne(memberId);
        return boardServiceImpl.updateAnnouncementPost(member, boardRequestDto);
    }

    /*5-2.자유게시판 수정*/
    @Operation(summary = "자유게시판 수정")
    @PostMapping("/board/free/post/update/")
    public PostDetailResponseDto updateFreePost(@RequestBody BoardRequestDto boardRequestDto) {
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        Long memberId = memberKakaoApiService.getMemberInfoFromAccessToken(accessToken);
        Member member = memberRepository.findOne(memberId);
        return boardServiceImpl.updateFreePost(member, boardRequestDto);
    }

    /*5-3.ToDoList 수정*/
    @Operation(summary = "ToDoList 수정")
    @PostMapping("/board/todo/post/update/")
    public ToDoListResponseDto updateToDoListPost(@RequestBody ToDoListRequestDto toDoListRequestDto) {
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        Long memberId = memberKakaoApiService.getMemberInfoFromAccessToken(accessToken);
        Member member = memberRepository.findOne(memberId);

        return boardServiceImpl.updateToDoListPost(member, toDoListRequestDto);
    }

    /*5-4.재고관리 게시글 수정*/
    @Operation(summary = "재고관리 게시글 수정", description = "재고의 정보를 수정")
    @PostMapping("/board/stock/post/update/")
    public StockResponseDto updateStockPost(@RequestBody StockRequestDto stockRequestDto) {
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        Long memberId = memberKakaoApiService.getMemberInfoFromAccessToken(accessToken);
        Member member = memberRepository.findOne(memberId);
        return boardServiceImpl.updateStockPost(member, stockRequestDto);
    }

    /*5-5.재고관리 파일 수정 -> 새로운 파일 업로드 후 정보를 받아 기존꺼 삭제, DB수정*/
    @Operation(summary = "재고관리 사진 수정", description = "해당 게시글의 사진을 수정")
    @PostMapping("/board/stock/post/update/file/")
    public ResponseEntity updateStockFile(@RequestBody StockFileRequestDto stockFileRequestDto) {
        amazonS3Service.deleteFile(stockFileRequestDto.getOldFileName()); //기존 파일 삭제
        Long postId = stockFileRequestDto.getId();
        String newFileName = stockFileRequestDto.getNewFileName();
        String newFilePath = stockFileRequestDto.getNewFilePath();
        boardServiceImpl.updateStockFilePost(postId, newFileName, newFilePath);

        log.info("**재고관리 파일 수정 성공, 기존파일이름:{} / 새로운파일이름:{}", stockFileRequestDto.getOldFileName(), newFileName);
        return new ResponseEntity("Success", HttpStatus.OK);
    }

    /***--6.기타--***/
    /*6-1.ToDoList 업무 완료 표시*/
    @Operation(summary = "ToDoList 업무 완료 유무", description = "체크박스 눌렀을 때")
    @PostMapping("/board/todo/clear/")
    public PostDetailResponseDto clearJob(@RequestParam("id") Long id) {
        return boardServiceImpl.clearJob(id);
    }
}