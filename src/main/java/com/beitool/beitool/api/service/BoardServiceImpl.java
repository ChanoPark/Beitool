package com.beitool.beitool.api.service;

import com.beitool.beitool.api.dto.board.*;
import com.beitool.beitool.api.repository.BelongRepository;
import com.beitool.beitool.api.repository.WorkInfoRepository;
import com.beitool.beitool.api.repository.BoardRepository;
import com.beitool.beitool.api.repository.MemberRepository;
import com.beitool.beitool.domain.*;
import com.beitool.beitool.domain.board.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@RequiredArgsConstructor
@Service
public class BoardServiceImpl {
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final BelongRepository belongRepository;

    /***--1.게시글 목록 조회--***/
    /*1-1.부모 클래스인 BoardDomain 조회*/
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

        log.info("**게시글 조회 성공, 사업장 번호:{}, 조회된 게시글 수:{}", store.getId(), countPost);
        return boardResponseDto;
    }

    /*1-2.ToDoList 게시글 조회*/
    public ToDoListResponseDto readToDoList(Member member, Store store) {
        ToDoListResponseDto toDoListResponseDto = new ToDoListResponseDto();
        Belong belong = belongRepository.findBelongInfo(member, store);

        List<ToDoList> toDoLists;

        try { //사장이면 전체 조회
            if (belong.getPosition() == MemberPosition.President) {
                toDoLists = boardRepository.readToDoListPost(store);
                log.info("ToDoList 조회(사장) 결과, 사업장 번호:{} / 게시글 개수:{}", store.getId(), toDoLists.size());
            } else { //직원이면 자기껏만 조회
                toDoLists = boardRepository.readToDoListPost(member, store);
                log.info("ToDoList 조회(직원) 결과, 사업장 번호:{} / 게시글 개수:{}", store.getId(), toDoLists.size());
            }
        } catch (NoResultException e) {
            log.warn("**ToDoList 조회 실패, 게시글이 존재하지 않습니다, 사업장 번호:{}", belong.getStore().getId());
            return new ToDoListResponseDto("Failed");
        }

        for (ToDoList findPost : toDoLists) {
            Long id = findPost.getId();
            String title = findPost.getTitle();
            boolean isClear = findPost.isClear();
            LocalDate jobDate = findPost.getJobDate();

            Member employee = findPost.getEmployee();
            String employeeName = belongRepository.findBelongInfo(employee, store).getName();
            toDoListResponseDto.addPost(id, title, employeeName, isClear, jobDate);
        }
        log.info("**ToDoList 게시글 조회 성공, 사업장 번호:{} / 게시글 개수:{}", belong.getStore().getId(), toDoLists.size());
        toDoListResponseDto.setMessage("Success");

        //마감일 기준으로 정렬(오름차순)
        Collections.sort(toDoLists, new ToDoListComparator());

        return toDoListResponseDto;
    }

    /*1-3.재고관리 게시글 목록 조회*/
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

        log.info("**재고 관리 게시글 조회 결과, 사업장 번호:{} / 게시글 개수:{}", store.getId(), stockPosts.size());
        return stockReadResponseDto;
    }

    /***--2.게시글 작성--***/
    /*2-1.공지사항 작성*/
    public PostDetailResponseDto createAnnouncementPost(Member member, Belong belongInfo, BoardRequestDto boardRequestDto) {
        Store store = member.getActiveStore();
        String authorName = belongInfo.getName();
        String content = boardRequestDto.getContent();
        String title = boardRequestDto.getTitle();
        LocalDateTime createdTime = LocalDateTime.now();
        Announcement announcement = new Announcement(authorName, content, createdTime, member, store, title);
        Long postId = boardRepository.createPost(announcement);

        log.info("**공지사항 작성 성공, 사업장번호:{} / 게시글번호:{}", store.getId(), postId);
        return new PostDetailResponseDto(title, content, postId, authorName, createdTime, "Success");
    }

    /*2-2.자유게시판 작성*/
    public PostDetailResponseDto createFreePost(Member member, Belong belongInfo, BoardRequestDto boardRequestDto) {
        Store store = member.getActiveStore();
        String authorName = belongInfo.getName();
        String content = boardRequestDto.getContent();
        String title = boardRequestDto.getTitle();
        LocalDateTime createdTime = LocalDateTime.now();
        Free free = new Free(authorName, content, createdTime, member, store, title);
        Long postId = boardRepository.createPost(free);

        log.info("**자유게시판 작성 성공, 사업장번호:{} / 게시글번호:{}", store.getId(), postId);
        return new PostDetailResponseDto(title, content, postId, authorName, createdTime, "Success");
    }

    /*2-3.ToDoList 작성*/
    public ToDoListResponseDto createToDoPost(Member author, Belong belongInfo, ToDoListRequestDto toDoListRequestDto) {
        Store store = author.getActiveStore();
        String authorName = belongInfo.getName();
        String title = toDoListRequestDto.getTitle();
        LocalDateTime createdTime = LocalDateTime.now(); //업무 지시 시간
        LocalDate jobDate = toDoListRequestDto.getJobDate(); // 업무 기한
        Member employee = memberRepository.findOne(toDoListRequestDto.getEmployee()); //지시 대상 조회

        //조회된 직원이 없으면 실패 반환
        if (employee == null) {
            log.warn("**ToDoList 작성 실패 - 잘못된 직원 번호, 사업장 번호:{} / 직원 번호:{}", store.getId(), employee.getId());
            return new ToDoListResponseDto("Failed");
        }

        ToDoList toDoList = new ToDoList(authorName, createdTime, author, store, title, jobDate, employee);

        boardRepository.createPost(toDoList);

        log.info("**ToDoList 작성 성공, 사업장번호:{} / 게시글번호:{}", store.getId(), toDoList.getId());
        return readToDoList(author, store);
    }

    /*2-4.재고관리 작성*/
    public StockResponseDto createStockPost(StockRequestDto stockRequestDto, Member member) {
        String authorName = belongRepository.findBelongInfo(member, member.getActiveStore()).getName(); //작성자
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

        log.info("**재고관리 작성 성공, 사업장 번호:{} / 게시글번호:{}", member.getActiveStore().getId(), postId);
        return new StockResponseDto("Success", postId, authorName, productName,quantity, description,
                expirationTime, createdDate, productFilePath, productFileName);
    }


    /***--3.게시글 조회--***/
    /*3-1.공지사항 게시글 조회*/
    public PostDetailResponseDto readAnnouncementPost(Long postId) {
        Announcement findPost = new Announcement();
        try {
            findPost = (Announcement) boardRepository.readPost(postId, findPost);
            LocalDateTime createdTime = findPost.getCreatedTime();

            log.info("**공지사항 조회 결과, 게시글번호:{}", postId);
            return new PostDetailResponseDto(findPost.getTitle(), findPost.getContent(),
                    postId, findPost.getAuthorName(), createdTime, "Success");
        } catch (NoResultException e) {
            log.warn("**공지사항 조회 실패 - 게시글 번호가 올바르지 않음, 게시글번호:{}", postId);
            return new PostDetailResponseDto("Failed");
        }
    }

    /*3-2.자유게시판 게시글 조회*/
    public PostDetailResponseDto readFreePost(Long postId) {
        Free findPost = new Free();
        try {
            findPost = (Free) boardRepository.readPost(postId, findPost);
            LocalDateTime createdTime = findPost.getCreatedTime();

            log.info("**자유게시판 조회 결과, 게시글번호:{}", postId);
            return new PostDetailResponseDto(findPost.getTitle(), findPost.getContent(),
                    postId, findPost.getAuthorName(), createdTime, "Success");
        } catch (NoResultException e) {
            log.warn("**자유게시판 조회 실패 - 게시글 번호가 올바르지 않음, 게시글번호:{}", postId);
            return new PostDetailResponseDto("Failed");
        }
    }

    /*3-3.재고관리 게시글 조회*/
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

            log.info("**재고관리 조회 결과, 게시글번호:{}", postId);
            return new StockResponseDto("Success", id, authorName, productName, quantity,
                                            description, expirationTime, createdDate, productFilePath, productFileName);
        } catch (NoResultException e) {
            log.warn("**재고관리 조회 실패 - 게시글 번호가 올바르지 않음, 게시글번호:{}", postId);
            return new StockResponseDto("Failed");
        }
    }

    /***--4.게시글 삭제--***/
    public boolean deletePost(Member member, Long id, String boardType) {
        Member author = boardRepository.findAuthor(id);

        //본인만 삭제할 수 있음
        if (author.equals(member)) {
            try {
                boardRepository.deletePost(id, boardType);
                log.info("**게시글 삭제 성공, 게시글 번호:{} / 게시판 종류:{}", id, boardType);
                return true;
            } catch (NoResultException e) {
                log.warn("**게시글 삭제 실패 - 존재하지 않는 게시글, 게시글 번호:{} / 게시판 종류:{}", id, boardType);
                return false;
            }
        } else {
            log.warn("**게시글 삭제 실패 - 본인만 삭제 가능, 게시글 번호:{} / 회원 번호:{} / 게시판 종류:{}", id, member.getId(),boardType);
            return false;
        }
        //*******없는거 삭제해도 성공적으로 삭제되었다고 뜸
        //없는 게시글 삭제 요청이 들어올수가있나 근데?
    }

    /***--5.게시글 수정--***/
    /*5-1.공지사항 수정*/
    @Transactional
    public PostDetailResponseDto updateAnnouncementPost(Member member, BoardRequestDto boardRequestDto) {
        Member author = boardRepository.findAuthor(boardRequestDto.getId());
        Long postId = boardRequestDto.getId();

        if(author.equals(member)) { //글쓴사람과 같지 않으면 수정할 수 없음.
            LocalDateTime modifiedTime = LocalDateTime.now();

            Announcement findPost = boardRepository.findAnnouncementPost(postId);
            findPost.updatePost(boardRequestDto.getTitle(), boardRequestDto.getContent(), modifiedTime);

            log.info("**공지사항 수정 성공, 게시글번호:{}", postId);
            return new PostDetailResponseDto(findPost.getTitle(), findPost.getContent(),
                    postId, findPost.getAuthorName(), modifiedTime, "Success");
        } else {
            log.warn("**공지사항 수정 실패 - 본인만 삭제가능, 회원번호:{} / 작성자번호:{} / 게시글번호:{}", member.getId(), author.getId(), postId);
            return new PostDetailResponseDto("Failed");
        }
    }

    /*5-2.자유게시판 수정*/
    @Transactional
    public PostDetailResponseDto updateFreePost(Member member, BoardRequestDto boardRequestDto) {
        Member author = boardRepository.findAuthor(boardRequestDto.getId());
        Long postId = boardRequestDto.getId();

        if(author.equals(member)) { //글쓴사람과 같지 않으면 수정할 수 없음.
            LocalDateTime modifiedTime = LocalDateTime.now();

            Free findPost = boardRepository.findFreePost(postId);
            findPost.updatePost(boardRequestDto.getTitle(), boardRequestDto.getContent(), modifiedTime);

            log.info("**자유게시판 수정 성공, 게시글번호:{}", postId);
            return new PostDetailResponseDto(findPost.getTitle(), findPost.getContent(),
                    postId, findPost.getAuthorName(), modifiedTime, "Success");
        } else {
            log.warn("**자유게시판 수정 실패 - 본인만 삭제가능, 회원번호:{} / 작성자번호:{} / 게시글번호:{}", member.getId(), author.getId(), postId);
            return new PostDetailResponseDto("Failed");
        }
    }

    /*5-3.ToDoList 수정*/
    @Transactional
    public ToDoListResponseDto updateToDoListPost(Member member, ToDoListRequestDto toDoListRequestDto) {
        Member author = boardRepository.findAuthor(toDoListRequestDto.getId());
        Long postId = toDoListRequestDto.getId();

        if(author.equals(member)) { //글쓴사람과 같지 않으면 수정할 수 없음.
            Store store = author.getActiveStore();
            Member employee = memberRepository.findOne(toDoListRequestDto.getEmployee());

            LocalDate jobDate = toDoListRequestDto.getJobDate();
            LocalDateTime modifiedTime = LocalDateTime.now();

            ToDoList findPost = boardRepository.findToDoListPost(postId);
            findPost.updatePost(toDoListRequestDto.getTitle(), employee, modifiedTime, jobDate);

            log.info("**자유게시판 수정 성공, 게시글번호:{}", postId);
            return readToDoList(author, store);
        } else {
            log.warn("**ToDoList 수정 실패 - 본인만 삭제가능, 회원번호:{} / 작성자번호:{} / 게시글번호:{}", member.getId(), author.getId(), postId);
            return new ToDoListResponseDto("Failed");
        }
    }

    /*5-4.재고관리 정보 수정*/
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
        String authorName = belongRepository.findBelongInfo(member, member.getActiveStore()).getName();

        findPost.updateStock(quantity, expirationTime, modifiedTime, authorName, description, productName);

        log.info("**재고관리 정보 수정 성공 - 회원번호:{} / 게시글번호:{}", member.getId(), postId);
        return new StockResponseDto("Success", postId, authorName, productName, quantity,
                description, expirationTime, modifiedTime, productFileName, productFilePath);
    }

    /*5-5.재고관리 파일 수정*/
    @Transactional
    public void updateStockFilePost(Long postId, String newFileName,String newFilePath) {
        Stock findPost = boardRepository.findStockPost(postId);
        findPost.updateFile(newFileName, newFilePath);
        log.info("**재고관리 파일 수정 결과 - 게시글번호:{} / 새로운 파일이름:{}", postId, newFileName);
    }
    /**--6.기타--**/
    /*6-1.ToDoList 업무 완료 표시*/
    @Transactional
    public PostDetailResponseDto clearJob(Long id) {
        ToDoList findPost = boardRepository.findToDoListPost(id);

        if(findPost == null) {
            log.info("**ToDoList 업무 완료 취소(체크박스)");
            return new PostDetailResponseDto("Failed");
        } else {
            findPost.clearJob();
            log.info("**ToDoList 업무 완료 표시(체크박스)");
            return new PostDetailResponseDto("Success");
        }
    }
}