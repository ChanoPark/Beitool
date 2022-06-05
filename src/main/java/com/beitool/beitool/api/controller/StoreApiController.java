package com.beitool.beitool.api.controller;

import com.beitool.beitool.api.dto.store.*;
import com.beitool.beitool.api.repository.BelongWorkInfoRepository;
import com.beitool.beitool.api.repository.MemberRepository;
import com.beitool.beitool.api.service.MemberKakaoApiService;
import com.beitool.beitool.api.service.StoreService;
import com.beitool.beitool.domain.Member;
import com.beitool.beitool.domain.MemberPosition;
import com.beitool.beitool.domain.Store;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.NoResultException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;

/**
 * 2022-04-10 사업장과 관련된 요청을 처리하는 컨트롤러
 * 1.사업장 생성
 * 2.사업장 가입
 * 3.지도에 들어왔을 때, 사업장 위도,경도값 반환
 * 4.메인 화면(사업장 이름 반환, 나중에 일정도 반환할 것으로 예상)
 * 5.사업장 변경(소속되어 있는 사업장 정보 반환)
 * 6.소속되어 있는 직원 목록 반환
 * 7.가게 환경 설정 접속(가게 코드 반환)
 * 8.직원 급여 변경
 * 예상되는 기능: 사업장 정보 수정 등
 * Implemented by Chanos
 */
@RestController
@RequiredArgsConstructor
public class StoreApiController {
    private final StoreService storeService;
    private final MemberRepository memberRepository;
    private final MemberKakaoApiService memberKakaoApiService;
    private final BelongWorkInfoRepository belongWorkInfoRepository;

    /*1.사업장 생성(+사장 직급 업데이트)*/
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

        //생성된 사업장에 사장 소속시키기(직급도 여기서 세팅)
        LocalDate joinDate = storeService.joinStore(member, store, createStoreRequest.getPlaceName());

        //ResponseDTO에 정보 삽입(try-catch문으로 인해 생성자에서 바로 삽입을 못함->설계를 잘하면 한번에 할 수 있지 않을까?)
        createStoreResponse.setBelongInfo(memberId, store.getId(), joinDate, "Success", "MainScreen");

        return createStoreResponse;
    }

    /*2.사업장 가입(+직원 직급 업데이트)*/
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
    /*3.지도에 들어왔을 때, 사업장 위도,경도값 반환*/
    @PostMapping("/store/map/")
    public StoreAddressResponseDto getStoreAddressAndAllowDistance(@RequestBody Map<String, String> param) {
        String accessToken = param.get("accessToken");
        StoreAddressResponseDto storeAddressAndAllowDistance = storeService.getStoreAddressAndAllowDistance(accessToken);
        return storeAddressAndAllowDistance;
    }

    /*4.메인 화면(사업장 이름)*/
    @PostMapping("/store/main/")
    public GetActiveStoreInfo getActiveStoreInfo(@RequestBody Map<String, String> param) {
        String accessToken = param.get("accessToken");
        Long memberId = memberKakaoApiService.getMemberInfoFromAccessToken(accessToken);
        Member member = memberRepository.findOne(memberId);

        MemberPosition position = belongWorkInfoRepository.findBelongInfo(member, member.getActiveStore()).getPosition();

        return new GetActiveStoreInfo(member.getActiveStore().getName(), position);
    }

    /*5.사업장 변경(소속되어 있는 사업장 정보 반환)*/
    @PostMapping("/store/belonginfo/")
    public GetBelongStoreInfoResponse getBelongStoreInfo(@RequestBody Map<String, String> param) {
        String accessToken = param.get("accessToken");
        Long memberId = memberKakaoApiService.getMemberInfoFromAccessToken(accessToken);
        Member member = memberRepository.findOne(memberId);

        return storeService.getBelongStoreInfo(member);
    }

    /*6.소속되어 있는 직원 목록 반환*/
    @PostMapping("/store/belong/employee/")
    public BelongEmployeeListResponseDto getBelongEmployeeList(@RequestBody Map<String, String> params) {
        Long memberId = memberKakaoApiService.getMemberInfoFromAccessToken(params.get("accessToken"));
        Member member = memberRepository.findOne(memberId);
        return storeService.getBelongEmployeeList(member.getActiveStore().getId());
    }

    /*7.가게 환경 설정 접속(가게 코드 반환)*/
    @PostMapping("/store/config/")
    public GetConfigInfoResponse getConfigInfo(@RequestBody Map<String, String> param) {
        Long memberId = memberKakaoApiService.getMemberInfoFromAccessToken(param.get("accessToken"));
        Member member = memberRepository.findOne(memberId);
        Store store = member.getActiveStore();

        return new GetConfigInfoResponse(store.getInviteCode(), store.getAllowDistance());
    }

    /*8.직원 급여 변경*/
    @PostMapping("/store/config/salary/")
    public ResponseEntity setEmployeeSalary(@RequestBody SetEmployeeSalaryRequest employeeSalaryRequest) {
        Long memberId = memberKakaoApiService.getMemberInfoFromAccessToken(employeeSalaryRequest.getAccessToken());
        Store store = memberRepository.findOne(memberId).getActiveStore();
        Long employeeId = employeeSalaryRequest.getEmployeeId();
        Integer newSalary = employeeSalaryRequest.getNewSalary();

        return storeService.setEmployeeSalary(store, employeeId, newSalary);
    }

    /*---------------DTO-----------------*/
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
        private MemberPosition position;
    }

    /*사업장 환경설정 접속시 받는 Response*/
    @Data @AllArgsConstructor
    static class GetConfigInfoResponse {
        private Integer inviteCode;
        private Integer allowDistance;
    }

    /*직원 급여 변경을 위한 Request DTO*/
    @Data
    static class SetEmployeeSalaryRequest {
        private String accessToken;
        private Long employeeId;
        private Integer newSalary;
    }
}