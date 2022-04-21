package com.beitool.beitool.api.controller;

import com.beitool.beitool.api.dto.StoreAddressResponseDto;
import com.beitool.beitool.api.repository.BelongWorkInfoRepository;
import com.beitool.beitool.api.repository.MemberRepository;
import com.beitool.beitool.api.service.MemberKakaoApiService;
import com.beitool.beitool.api.service.StoreService;
import com.beitool.beitool.domain.Belong;
import com.beitool.beitool.domain.Member;
import com.beitool.beitool.domain.MemberPosition;
import com.beitool.beitool.domain.Store;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 2022-04-10 사업장과 관련된 요청을 처리하는 컨트롤러
 * 1.사업장 생성
 * 2.사업장 가입
 * 3.지도에 들어왔을 때, 사업장 위도,경도값 반환
 * 4.메인 화면(사업장 이름 반환, 나중에 일정도 반환할 것으로 예상)
 * 5.사업장 변경(소속되어 있는 사업장 정보 반환)
 * 예상되는 기능: 사업장 생성, 가입, 사업장 정보 수정, (여러 개의 사업장 처리? 구분? 정도 할 수도 있지 않을까?)
 * Implemented by Chanos
 */
@RestController
@RequiredArgsConstructor
public class StoreApiController {
    private final StoreService storeService;
    private final MemberRepository memberRepository;
    private final MemberKakaoApiService memberKakaoApiService;
    private final BelongWorkInfoRepository belongWorkInfoRepository;

    /*사업장 생성(+사장 직급 업데이트)*/
    @PostMapping("/store/create/")
    public CreateAndJoinStoreResponse createStore(@RequestBody CreateStoreRequest createStoreRequest) {
        System.out.println("***사업장 생성 createStoreRequest : " + createStoreRequest);

        CreateAndJoinStoreResponse createStoreResponse = new CreateAndJoinStoreResponse();
        createStoreResponse.setMessage("Failed");
        createStoreResponse.setScreen("PlaceRegister");

        //회원 직급 등록(사장)
        Long memberId = memberKakaoApiService.getMemberInfoFromAccessToken(createStoreRequest.getAccessToken());
        Member member = memberRepository.findOne(memberId);
//            memberService.setPosition(memberId, createStoreRequest.getStatus());

        //사업장 생성
        Store store = storeService.createStore(createStoreRequest.placeName,
                createStoreRequest.address, createStoreRequest.detailAddr);

        //생성된 사업장에 사장 소속시키기
        LocalDate joinDate = storeService.joinStore(member, store, createStoreRequest.getPlaceName());

        //ResponseDTO에 정보 삽입(try-catch문으로 인해 생성자에서 바로 삽입을 못함->설계를 잘하면 한번에 할 수 있지 않을까?)
        createStoreResponse.setBelongInfo(memberId, store.getId(), joinDate, "Success", "MainScreen");

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

            //사업장 가입
            LocalDate currentTime = LocalDate.now(ZoneId.of("Asia/Seoul"));
            Long storeId = storeService.joinStore(findMember, joinStoreRequest.getInviteCode(), currentTime, joinStoreRequest.getUserName());
            createStoreResponse.setBelongInfo(memberId, storeId, currentTime, "Success", "MainScreen");
        } catch (NoResultException e) { //올바르지 않은 사업장 코드
            createStoreResponse.setMessage("Failed");
            createStoreResponse.setScreen("PlaceJoin"); // 다시 가입페이지로 이동
        }
        return createStoreResponse;
    }
    /*지도에 들어왔을 때, 사업장 위도,경도값 반환*/
    @PostMapping("/store/map/")
    public StoreAddressResponseDto getStoreAddressAndAllowDistance(@RequestBody Map<String, String> param) {
        String accessToken = param.get("accessToken");
        StoreAddressResponseDto storeAddressAndAllowDistance = storeService.getStoreAddressAndAllowDistance(accessToken);
        return storeAddressAndAllowDistance;
    }

    /*메인 화면(사업장 이름)*/
    @PostMapping("/store/main/")
    public GetActiveStoreInfo getActiveStoreInfo(@RequestBody Map<String, String> param) {
        String accessToken = param.get("accessToken");
        Long memberId = memberKakaoApiService.getMemberInfoFromAccessToken(accessToken);
        Member member = memberRepository.findOne(memberId);

        return new GetActiveStoreInfo(member.getActiveStore().getName());
    }

    /*사업장 변경(소속되어 있는 사업장 정보 반환)*/
    @PostMapping("/store/belonginfo/")
    public GetBelongStoreInfoResponse getBelongStoreInfo(@RequestBody Map<String, String> param) {
        String accessToken = param.get("accessToken");
        Long memberId = memberKakaoApiService.getMemberInfoFromAccessToken(accessToken);
        Member member = memberRepository.findOne(memberId);

        GetBelongStoreInfoResponse getBelongStoreInfoResponse = new GetBelongStoreInfoResponse();

        //활성화된 사업장 소속 정보 조회
        Belong activeStoreBelongInfo = belongWorkInfoRepository.findBelongInfo(member, member.getActiveStore());
        //활성화된 사업장 소속 정보 Response에 저장
        getBelongStoreInfoResponse.setActiveStoreName(activeStoreBelongInfo.getName());
        getBelongStoreInfoResponse.setActiveStorePosition(activeStoreBelongInfo.getPosition());
                
        //소속되어 있는 모든 사업장 소속 정보
        List<Belong> belongs = belongWorkInfoRepository.allBelongInfo(member);

        for (Belong belong : belongs) {
            String belongStoreName = belong.getStore().getName(); //소속된 사업장 이름
            //취합된 소속된 사업장 정보를 BelongedStore 클래스에 모아서 객체 생성
            BelongedStore belongedStore = new BelongedStore(belong.getName(), belongStoreName, belong.getPosition());
            //HashMap에 소속된 사업장 정보 저장
            getBelongStoreInfoResponse.setBelongedStore(belongedStore);
        }
        System.out.println("***사업장 정보 반환 완료");
        return getBelongStoreInfoResponse;
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
    @Data
    @AllArgsConstructor @NoArgsConstructor
    static class CreateAndJoinStoreResponse {
        private Long memberId;
        private Long storeId;
        private String message;
        private String screen;

        @JsonDeserialize(using= LocalDateDeserializer.class)
        @JsonSerialize(using= LocalDateSerializer.class)
        private LocalDate belongDate;

        //try-catch문으로 인해 생성자에서 받을 수 없으므로 정보를 삽입하는 메소드 사용
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

    /*메인화면에서 사업장 이름을 출력하기 위한 Response DTO*/
    @Data @AllArgsConstructor
    static class GetActiveStoreInfo {
        private String storeName;
    }

    /*사업장 변경 시 데이터를 전달하기 위한 Response DTO*/
    @Data
    static class GetBelongStoreInfoResponse {
        private String activeStoreName;
        private MemberPosition activeStorePosition;
        private List<Map<String, BelongedStore>> belongedStore;

        public GetBelongStoreInfoResponse() {
            this.belongedStore = new ArrayList<>();
        }

        public void setBelongedStore(BelongedStore belongedStore) {
            Map<String, BelongedStore> belongedStoreMap = new HashMap<>();
            belongedStoreMap.put("belongedStore", belongedStore);
            this.belongedStore.add(belongedStoreMap); //리스트안에 맵을 감싸서 보냄
        }
    }
    /*소속된 사업장의 정보를 모으기 위한 클래스(GetBelongStoreInfoResponse에 포함됌)*/
    @Data @AllArgsConstructor
    static class BelongedStore {
        private String storeName;
        private String memberName;
        private MemberPosition memberPosition;
    }

}