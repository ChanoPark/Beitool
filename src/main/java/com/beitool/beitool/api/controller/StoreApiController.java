package com.beitool.beitool.api.controller;

import com.beitool.beitool.api.repository.MemberRepository;
import com.beitool.beitool.api.service.MemberKakaoApiService;
import com.beitool.beitool.api.service.MemberService;
import com.beitool.beitool.api.service.StoreService;
import com.beitool.beitool.domain.Member;
import com.beitool.beitool.domain.Store;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.NoResultException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;

/**
 * 2022-04-10 사업장과 관련된 요청을 처리하는 컨트롤러
 * 예상되는 기능: 사업장 생성, 가입, 사업장 정보 수정, (여러 개의 사업장 처리? 구분? 정도 할 수도 있지 않을까?)
 * Implemented by Chanos
 */
@RestController
@RequiredArgsConstructor
public class StoreApiController {
    private final MemberService memberService;
    private final StoreService storeService;
    private final MemberRepository memberRepository;
    private final MemberKakaoApiService memberKakaoApiService;

    /*사업장 생성(+사장 직급 업데이트)*/
    @PostMapping("/store/create/")
    public CreateAndJoinStoreResponse createStore(@RequestBody CreateStoreRequest createStoreRequest) {
        System.out.println("***사업장 생성 createStoreRequest : " + createStoreRequest);

        CreateAndJoinStoreResponse createStoreResponse = new CreateAndJoinStoreResponse();
        createStoreResponse.setMessage("Join failed");
        createStoreResponse.setScreen("PlaceRegister");

        //회원 직급 등록(사장)
        try {
            Long memberId = memberKakaoApiService.getMemberInfoFromAccessToken(createStoreRequest.getAccessToken());
            Member findMember = memberRepository.findOne(memberId);
//            memberService.setPosition(memberId, createStoreRequest.getStatus());

            //사업장 생성
            Store store = storeService.createStore(createStoreRequest.placeName,
                    createStoreRequest.address, createStoreRequest.detailAddr);

            //생성된 사업장에 사장 소속시키기
            LocalDate joinDate = storeService.joinStore(findMember, store, createStoreRequest.getPlaceName());

            //ResponseDTO에 정보 삽입(try-catch문으로 인해 생성자에서 바로 삽입을 못함->설계를 잘하면 한번에 할 수 있지 않을까?)
            createStoreResponse.setBelongInfo(memberId, store.getId(), joinDate, "Successful join", "MainScreen");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return createStoreResponse;
    }

    /*사업장 가입(+직원 직급 업데이트)*/
    @PostMapping("/store/join/")
    public CreateAndJoinStoreResponse joinStore(@RequestBody JoinStoreRequest joinStoreRequest) {
        System.out.println("***사업장 가입 request:" + joinStoreRequest);

        CreateAndJoinStoreResponse createStoreResponse = new CreateAndJoinStoreResponse();

        try {
            //회원 직급 등록(직원)
            Long memberId = memberKakaoApiService.getMemberInfoFromAccessToken(joinStoreRequest.getAccessToken());
            Member findMember = memberRepository.findOne(memberId);
//            memberService.setPosition(memberId, joinStoreRequest.getStatus());

            //사업장 가입
            LocalDate currentTime = LocalDate.now(ZoneId.of("Asia/Seoul"));
            Long storeId = storeService.joinStore(findMember,joinStoreRequest.getInviteCode(), currentTime, joinStoreRequest.getUserName());
            createStoreResponse.setBelongInfo(memberId, storeId, currentTime, "Successful join", "MainScreen");

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (NoResultException e) { //올바르지 않은 사업장 코드
            createStoreResponse.setMessage("Join failed");
            createStoreResponse.setScreen("PlaceJoin"); // 다시 가입페이지로 이동
        }
        return createStoreResponse;
    }

    /*-----DTO-----*/
    /*사업장 생성을 위한 Request DTO, ResponseDTO*/
    @Data
    static class CreateStoreRequest {
        private String accessToken;
        private String status;
        private String placeName;
        private String address;
        private String detailAddr;
    }
    /*사업장 생성, 가입 Response DTO*/
    @Data @Setter
    @AllArgsConstructor @NoArgsConstructor
    static class CreateAndJoinStoreResponse {
        private Long memberId;
        private Long storeId;
        private String message;
        private String screen;

        @JsonDeserialize(using= LocalDateDeserializer.class)
        @JsonSerialize(using= LocalDateSerializer.class)
        private LocalDate belongDate;

        /*try-catch문으로 인해 생성자에서 받을 수 없으므로 정보를 삽입하는 메소드 사용*/
        public void setBelongInfo(Long memberId, Long storeId, LocalDate belongDate, String message, String screen) {
            this.memberId = memberId;
            this.storeId = storeId;
            this.belongDate = belongDate;
            this.message = message;
            this.screen = screen;
        }
    }

    /*직원의 사업장 가입을 위한 Request DTO*/
    @Data
    static class JoinStoreRequest {
        private String accessToken;
        private String status;
        private String userName;
        private int inviteCode;
    }
}