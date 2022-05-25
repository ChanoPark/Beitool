package com.beitool.beitool.api.service;

import com.beitool.beitool.api.dto.board.*;
import com.beitool.beitool.api.repository.BelongWorkInfoRepository;
import com.beitool.beitool.api.repository.BoardRepository;
import com.beitool.beitool.api.repository.MemberRepository;
import com.beitool.beitool.domain.*;
import com.beitool.beitool.domain.board.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.jni.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * BoardService를 상속받아 CURD를 제공하는 구현체
 * 1.게시글 목록 조회
 *    1-1.부모 클래스인 BoardDomain 조회
 *    1-2.ToDoList 게시글 목록 조회
 *    1-3.재고관리 게시글 목록 조회
 * 2.게시글 작성
 *    2-1.공지사항 작성
 *    2-2.자유게시판 작성
 *    2-3.ToDoList 작성
 *    2-4.재고관리 작성
 * 3.게시글 조회
 *    3-1.공지시항 게시글 조회
 *    3-2.자유게시판 게시글 조회
 *    3-3.재고관리 게시글 조회
 * 4.게시글 삭제
 * 5.게시글 수정
 *    5-1.공지사항 수정
 *    5-2.자유게시판 수정
 *    5-3.ToDoList 수정
 *    5-4.재고관리 정보 수정
 *    5-5.재고관리 사진 수정
 * 6.기타
 *    6-1.ToDoList 업무 완료 표시
 * @author Chanos
 * @since 2022-05-20
 */
@Service
@RequiredArgsConstructor
public class BoardServiceImpl {
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final BelongWorkInfoRepository belongWorkInfoRepository;

    /***--게시글 목록 조회--***/
    /*부모 클래스인 BoardDomain 조회*/
    public BoardResponseDto readBoard(Member member, String boardType, Integer page) {
        Store store = member.getActiveStore();

        BoardResponseDto boardResponseDto = new BoardResponseDto();
        List<BoardDomain> posts = boardRepository.readBoard(store, boardType, page); //BoardDomain List가 날라옴.

        for (BoardDomain post : posts) {
            boardResponseDto.setPosts(post);
        }

        Long countPost = boardRepository.countPost(store, boardType); //게시글 개수
        Long totalPage = countPost/10 +1; //1페이지부터 하기 위함
        boardResponseDto.changePageToArray(totalPage);
        boardResponseDto.setMessage("Success");
        return boardResponseDto;
    }

    /*ToDoList 게시글 조회*/
    public ToDoListResponseDto readToDoList(Store store) {
        ToDoListResponseDto toDoListResponseDto = new ToDoListResponseDto();
        try {
            List<ToDoList> toDoLists = boardRepository.readToDoListPost(store);
            for (ToDoList findPost : toDoLists) {
                Long id = findPost.getId();
                String title = findPost.getTitle();
                boolean isClear = findPost.isClear();
                LocalDate jobDate = findPost.getJobDate();

                Member employee = findPost.getEmployee();
                String employeeName = belongWorkInfoRepository.findBelongInfo(employee, store).getName();
                toDoListResponseDto.addPost(id, title, employeeName, isClear, jobDate);
            }
            toDoListResponseDto.setMessage("Success");

            //마감일 기준으로 정렬(오름차순)
            Collections.sort(toDoLists, new ToDoListComparator());

        } catch (NoResultException e) {
            return new ToDoListResponseDto("Failed");
        }

        return toDoListResponseDto;
    }

    /*재고관리 게시글 목록 조회*/
    public StockReadResponseDto readStockListBoard(Store store) {
        StockReadResponseDto stockReadResponseDto = new StockReadResponseDto();
        List<Stock> stockPosts = boardRepository.readStockPost(store);
        for (Stock post : stockPosts) {
            Long id = post.getId();
            String title = post.getTitle();
            String description = post.getDescription();
            LocalDateTime expirationTime = post.getExpirationTime();
            LocalDateTime modifyTime = post.getModifiedTime();
            String filePath = post.getProductFilePath();
            stockReadResponseDto.setStock(id, title, description, expirationTime, modifyTime, filePath);
        }
        stockReadResponseDto.setMessage("Success");
        return stockReadResponseDto;
    }

    /***--게시글 작성--***/
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

    /*자유게시판 작성*/
    public PostDetailResponseDto createFreePost(Member member, Belong belongInfo, BoardRequestDto boardRequestDto) {
        Store store = member.getActiveStore();
        String authorName = belongInfo.getName();
        String content = boardRequestDto.getContent();
        String title = boardRequestDto.getTitle();
        LocalDateTime createdTime = LocalDateTime.now();
        Free free = new Free(authorName, content, createdTime, member, store, title);
        Long postId = boardRepository.createPost(free);

        return new PostDetailResponseDto(title, content, postId, authorName, createdTime, "Success");
    }

    /*ToDoList 작성*/
    public ToDoListResponseDto createToDoPost(Member author, Belong belongInfo, ToDoListRequestDto toDoListRequestDto) {
        Store store = author.getActiveStore();
        String authorName = belongInfo.getName();
        String title = toDoListRequestDto.getTitle();
        LocalDateTime createdTime = LocalDateTime.now(); //업무 지시 시간
        LocalDate jobDate = toDoListRequestDto.getJobDate(); // 업무 기한
        Member employee = memberRepository.findOne(toDoListRequestDto.getEmployee()); //지시 대상 조회

        //조회된 직원이 없으면 실패 반환
        if (employee == null)
            return new ToDoListResponseDto("Failed");

        ToDoList toDoList = new ToDoList(authorName, createdTime, author, store, title, jobDate, employee);

        boardRepository.createPost(toDoList);
        return readToDoList(store);
    }

    /*재고관리 작성*/
    public StockResponseDto createStockPost(StockRequestDto stockRequestDto, Member member) {
        String authorName = belongWorkInfoRepository.findBelongInfo(member, member.getActiveStore()).getName(); //작성자
        String productName = stockRequestDto.getProductName(); //상품명(부모 테이블의 제목에 들어감)
        Integer quantity = stockRequestDto.getQuantity(); //개수
        String description = stockRequestDto.getDescription(); //설명
        LocalDateTime expirationTime = stockRequestDto.getExpirationTime(); //유통기한
        LocalDateTime createdDate = LocalDateTime.now(); //작성일

        String productFileName = stockRequestDto.getProductFileName(); //파일명
        String productFilePath = stockRequestDto.getProductFilePath(); //파일경로(URL)

        //객체 생성
        Stock stock = new Stock(authorName, createdDate, member, member.getActiveStore(), description,
                productName, quantity, expirationTime, createdDate,productFileName, productFilePath);

        //DB저장
        Long postId = boardRepository.createPost(stock);

        return new StockResponseDto("Success", postId, authorName, productName,quantity, description,
                expirationTime, createdDate, productFilePath, productFileName);
    }


    /***--게시글 조회--***/
    /*공지사항 게시글 조회*/
    public PostDetailResponseDto readAnnouncementPost(Long postId) {
        Announcement findPost = new Announcement();
        try {
            findPost = (Announcement) boardRepository.readPost(postId, findPost);
            LocalDateTime createdTime = findPost.getCreatedTime();
            return new PostDetailResponseDto(findPost.getTitle(), findPost.getContent(),
                    postId, findPost.getAuthorName(), createdTime, "Success");
        } catch (NoResultException e) {
            return new PostDetailResponseDto("Failed");
        }
    }

    /*자유게시판 게시글 조회*/
    public PostDetailResponseDto readFreePost(Long postId) {
        Free findPost = new Free();
        try {
            findPost = (Free) boardRepository.readPost(postId, findPost);
            LocalDateTime createdTime = findPost.getCreatedTime();
            return new PostDetailResponseDto(findPost.getTitle(), findPost.getContent(),
                    postId, findPost.getAuthorName(), createdTime, "Success");
        } catch (NoResultException e) {
            return new PostDetailResponseDto("Failed");
        }
    }

    /*재고관리 게시글 조회*/
    public StockResponseDto readStockPost(Long postId) {
        Stock findPost = new Stock();
        try {
            findPost = (Stock) boardRepository.readPost(postId, findPost);
            Long id = findPost.getId();
            String authorName = findPost.getAuthorName();
            String productName = findPost.getTitle();
            Integer quantity = findPost.getQuantity();
            String description = findPost.getDescription();
            LocalDateTime expirationTime = findPost.getExpirationTime();
            LocalDateTime createdDate = findPost.getCreatedTime();
            String productFilePath = findPost.getProductFilePath();
            String productFileName = findPost.getProductFileName();
            return new StockResponseDto("Success", id, authorName, productName, quantity,
                                            description, expirationTime, createdDate, productFilePath, productFileName);
        } catch (NoResultException e) {
            return new StockResponseDto("Failed");
        }
    }

    /***--게시글 삭제--***/
    public String deletePost(Member member, Long id, String boardType) {
        Member author = boardRepository.findAuthor(id);

        //본인만 삭제할 수 있음
        if (author.equals(member)) {
            try {
                boardRepository.deletePost(id, boardType);
                return "Success";
            } catch (NoResultException e) {
                return "Failed";
            }
        } else {
            return "Failed";
        }
        //*******없는거 삭제해도 성공적으로 삭제되었다고 뜸
        //없는 게시글 삭제 요청이 들어올수가있나 근데?
    }

    /***--게시글 수정--***/
    /*공지사항 수정*/
    @Transactional
    public PostDetailResponseDto updateAnnouncementPost(Member member, BoardRequestDto boardRequestDto) {
        Member author = boardRepository.findAuthor(boardRequestDto.getId());

        if(author.equals(member)) { //글쓴사람과 같지 않으면 수정할 수 없음.
            Long postId = boardRequestDto.getId();
            LocalDateTime modifiedTime = LocalDateTime.now();

            Announcement findPost = boardRepository.findAnnouncementPost(postId);
            findPost.updatePost(boardRequestDto.getTitle(), boardRequestDto.getContent(), modifiedTime);

            return new PostDetailResponseDto(findPost.getTitle(), findPost.getContent(),
                    postId, findPost.getAuthorName(), modifiedTime, "Success");
        } else {
            return new PostDetailResponseDto("Failed");
        }
    }

    /*자유게시판 수정*/
    @Transactional
    public PostDetailResponseDto updateFreePost(Member member, BoardRequestDto boardRequestDto) {
        Member author = boardRepository.findAuthor(boardRequestDto.getId());

        if(author.equals(member)) { //글쓴사람과 같지 않으면 수정할 수 없음.
            Long postId = boardRequestDto.getId();
            LocalDateTime modifiedTime = LocalDateTime.now();

            Free findPost = boardRepository.findFreePost(postId);
            findPost.updatePost(boardRequestDto.getTitle(), boardRequestDto.getContent(), modifiedTime);

            return new PostDetailResponseDto(findPost.getTitle(), findPost.getContent(),
                    postId, findPost.getAuthorName(), modifiedTime, "Success");
        } else {
            return new PostDetailResponseDto("Failed");
        }
    }

    /*ToDoList 수정*/
    @Transactional
    public ToDoListResponseDto updateToDoListPost(Member member, ToDoListRequestDto toDoListRequestDto) {
        Member author = boardRepository.findAuthor(toDoListRequestDto.getId());

        if(author.equals(member)) { //글쓴사람과 같지 않으면 수정할 수 없음.
            Long postId = toDoListRequestDto.getId();
            Store store = author.getActiveStore();
            Member employee = memberRepository.findOne(toDoListRequestDto.getEmployee());

            LocalDate jobDate = toDoListRequestDto.getJobDate();
            LocalDateTime modifiedTime = LocalDateTime.now();

            ToDoList findPost = boardRepository.findToDoListPost(postId);
            findPost.updatePost(toDoListRequestDto.getTitle(), employee, modifiedTime, jobDate);

            return readToDoList(store);
        } else {
            return new ToDoListResponseDto("Failed");
        }
    }

    /*재고관리 정보 수정*/
    @Transactional
    public StockResponseDto updateStockPost(Member member, StockRequestDto stockRequestDto) {
        Long postId = stockRequestDto.getId();
        Stock findPost = boardRepository.findStockPost(postId);

        Integer quantity = findPost.getQuantity();
        LocalDateTime expirationTime = findPost.getExpirationTime();

        LocalDateTime modifiedTime = LocalDateTime.now();

        String description = stockRequestDto.getDescription();
        String productName = stockRequestDto.getProductName();

        String productFileName = stockRequestDto.getProductFileName();
        String productFilePath = stockRequestDto.getProductFilePath();
        String authorName = belongWorkInfoRepository.findBelongInfo(member, member.getActiveStore()).getName();

        findPost.updateStock(quantity, expirationTime, modifiedTime, authorName, description, productName);

        return new StockResponseDto("Success", postId, authorName, productName, quantity,
                description, expirationTime, modifiedTime, productFileName, productFilePath);
    }

    /*재고관리 파일 수정*/
    @Transactional
    public void updateStockFilePost(Long postId, String newFileName,String newFilePath) {
        Stock findPost = boardRepository.findStockPost(postId);
        findPost.updateFile(newFileName, newFilePath);
    }

    /*ToDoList 업무 완료 표시*/
    @Transactional
    public PostDetailResponseDto clearJob(Long id) {
        ToDoList findPost = boardRepository.findToDoListPost(id);

        if(findPost == null) {
            return new PostDetailResponseDto("Failed");
        } else {
            findPost.clearJob();
            return new PostDetailResponseDto("Success");
        }
    }


}