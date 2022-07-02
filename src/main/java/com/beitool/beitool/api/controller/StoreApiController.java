package com.beitool.beitool.api.controller;

import com.beitool.beitool.api.dto.store.*;
import com.beitool.beitool.api.repository.BelongRepository;
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
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

/**
 * 사업장과 관련된 요청을 처리하는 컨트롤러
 * 1.사업장 생성
 * 2.사업장 가입
 * 3.지도에 들어왔을 때, 사업장 위도,경도값 반환
 * 4.메인 화면(사업장 이름 반환, 나중에 일정도 반환할 것으로 예상)
 * 5.사업장 변경(소속되어 있는 사업장 정보 반환)
 * 6.소속되어 있는 직원 목록 반환
 * 7.가게 환경 설정 접속(가게 코드 반환)
 * 8.직원 급여 변경
 * 9.출근 허용 거리 설정
 * 10.가입 대기 직원 목록 확인
 * 11.가입 대기 직원 승인 & 삭제
 *
 * @author Chanos
 * @since 2022-06-09
 */

@Api(tags="사업장")
@Slf4j
@RequiredArgsConstructor
@RestController
public class StoreApiController {
    private final StoreService storeService;
    private final MemberRepository memberRepository;
    private final MemberKakaoApiService memberKakaoApiService;
    private final BelongRepository belongRepository;
    private final HttpServletRequest request;

    /*1.사업장 생성(+사장 직급 업데이트)*/
    @Operation(summary = "사업장 생성", description = "사업장 생성 + 회원 '사장' 직급 결정->사장만 접근해야 함.")
    @PostMapping("/store/create/")
    public CreateAndJoinStoreResponse createStore(@RequestBody CreateStoreRequest createStoreRequest) {
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        CreateAndJoinStoreResponse createStoreResponse = new CreateAndJoinStoreResponse();

        //회원 직급 등록(사장)
        Long memberId = memberKakaoApiService.getMemberInfoFromAccessToken(accessToken);
        Member member = memberRepository.findOne(memberId);

        //사업장 생성
        Store store = storeService.createStore(createStoreRequest.placeName,
                createStoreRequest.address, createStoreRequest.detailAddr);

        //생성된 사업장에 사장 소속시키기(직급도 여기서 세팅)
        LocalDate joinDate = storeService.joinStore(member, store, createStoreRequest.getPlaceName());

        //ResponseDTO에 정보 삽입(try-catch문으로 인해 생성자에서 바로 삽입을 못함->설계를 잘하면 한번에 할 수 있지 않을까?)
        createStoreResponse.setBelongInfo(memberId, store.getId(), joinDate, "Success", "MainScreen");

        log.info("**사업장 생성 성공, 사업장번호:{}",store.getId());
        return createStoreResponse;
    }

    /*2.사업장 가입(+직원 직급 업데이트)*/
    @Operation(summary = "사업장 가입", description = "사업장 가입 + 회원 '직원' 직급 결정->직원만 접근해야 함.")
    @PostMapping("/store/join/")
    public CreateAndJoinStoreResponse joinStore(@RequestBody JoinStoreRequest joinStoreRequest) {
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        CreateAndJoinStoreResponse createStoreResponse = new CreateAndJoinStoreResponse();
        Long memberId = memberKakaoApiService.getMemberInfoFromAccessToken(accessToken);

        try {
            Member findMember = memberRepository.findOne(memberId);

            //사업장 가입
            LocalDate currentTime = LocalDate.now(ZoneId.of("Asia/Seoul"));
            Long storeId = storeService.joinStore(findMember, joinStoreRequest.getInviteCode(), currentTime, joinStoreRequest.getUserName());

            //이름이 중복된 경우
            if (storeId < 0) {
                createStoreResponse.setMessage("Duplicated name");
                createStoreResponse.setScreen("PlaceJoin");
                log.warn("**사업장 가입 실패 - 중복된 이름 사용");
                return createStoreResponse;
            }

            createStoreResponse.setBelongInfo(memberId, storeId, currentTime, "Success", "MainScreen");
        } catch (NoResultException e) { //올바르지 않은 사업장 코드
            createStoreResponse.setMessage("Failed");
            createStoreResponse.setScreen("PlaceJoin"); // 다시 가입페이지로 이동
            log.warn("**사업장 가입 실패 - 유효하지 않은 사업장 코드");
            return createStoreResponse;
        }
        log.info("**사업장 가입 성공, 회원번호:{} / 사업장번호:{}", memberId, createStoreResponse.getStoreId());
        return createStoreResponse;
    }
    
    /*3.지도에 들어왔을 때, 사업장 위도,경도값 반환*/
    @Operation(summary = "사업장 위치 반환", description = "출퇴근을 위해 지도에 들어갔을 때, 활성화된 사업장의 위/경도 반환")
    @PostMapping("/store/map/")
    public StoreAddressResponseDto getStoreAddressAndAllowDistance() {
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        StoreAddressResponseDto storeAddressAndAllowDistance = storeService.getStoreAddressAndAllowDistance(accessToken);
        log.info("**사업장 위치 반환 성공, 위도:{} / 경도:{}", storeAddressAndAllowDistance.getLatitude(), storeAddressAndAllowDistance.getLongitude());
        return storeAddressAndAllowDistance;
    }

    /*4.메인 화면(사업장 이름)*/
    @Operation(summary = "메인 화면 접속", description = "활성화된 사업장의 이름, 회원의 직급 반환")
    @PostMapping("/store/main/")
    public GetActiveStoreInfo getActiveStoreInfo() {
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        Long memberId = memberKakaoApiService.getMemberInfoFromAccessToken(accessToken);
        Member member = memberRepository.findOne(memberId);

        MemberPosition position = belongRepository.findBelongInfo(member, member.getActiveStore()).getPosition();

        log.info("**메인 화면 조회 성공, 사업장 이름:{} / 직급:{}", member.getActiveStore().getName(), position);
        return new GetActiveStoreInfo(member.getActiveStore().getName(), position);
    }

    /*5.사업장 변경(소속되어 있는 사업장 정보 반환)*/
    @Operation(summary = "소속된 사업장 리스트 반환", description = "사업장 변경을 위한 소속된 사업장 리스트 반환")
    @PostMapping("/store/belonginfo/")
    public GetBelongStoreInfoResponse getBelongStoreInfo() {
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        Long memberId = memberKakaoApiService.getMemberInfoFromAccessToken(accessToken);
        Member member = memberRepository.findOne(memberId);

        return storeService.getBelongStoreInfo(member);
    }

    /*6.소속되어 있는 직원 목록 반환*/
    @Operation(summary = "사업장에 소속된 직원 목록 반환")
    @PostMapping("/store/belong/employee/")
    public BelongEmployeeListResponseDto getBelongEmployeeList() {
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        Long memberId = memberKakaoApiService.getMemberInfoFromAccessToken(accessToken);
        Member member = memberRepository.findOne(memberId);
        return storeService.getBelongEmployeeList(member.getActiveStore().getId());
    }

    /*7.사업장 환경 설정 접속(가게 코드 반환)*/
    @Operation(summary = "사업장 환경 설정", description = "사업장 코드 반환")
    @PostMapping("/store/config/")
    public GetConfigInfoResponse getConfigInfo() {
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        Long memberId = memberKakaoApiService.getMemberInfoFromAccessToken(accessToken);
        Store store = memberRepository.findOne(memberId).getActiveStore();

        log.info("사업장 환경 설정 정보 - 사업장코드:{} / 출근허용거리:{}", store.getInviteCode(), store.getAllowDistance());
        return new GetConfigInfoResponse(store.getInviteCode(), store.getAllowDistance());
    }

    /*8.직원 급여 변경*/
    @Operation(summary = "직원 급여 변경")
    @PostMapping("/store/config/salary/")
    public ResponseEntity setEmployeeSalary(@RequestBody SetEmployeeSalaryRequest employeeSalaryRequest) {
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        Long memberId = memberKakaoApiService.getMemberInfoFromAccessToken(accessToken);
        Store store = memberRepository.findOne(memberId).getActiveStore();
        Long employeeId = employeeSalaryRequest.getEmployeeId();
        Integer newSalary = employeeSalaryRequest.getNewSalary();

        return storeService.setEmployeeSalary(store, employeeId, newSalary);
    }

    /*9.출근 허용 거리 설정*/
    @Operation(summary = "사업장 출근 허용 거리 설정")
    @PostMapping("/store/config/distance/")
    public ResponseEntity setStoreAllowDistance(@RequestBody SetStoreAllowDistanceRequest storeDistanceRequest) {
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        Long memberId = memberKakaoApiService.getMemberInfoFromAccessToken(accessToken);
        Member member = memberRepository.findOne(memberId);
        Store store = member.getActiveStore();

        storeService.setStoreAllowDistance(store, storeDistanceRequest.getAllowDistance());

        log.info("**출근 허용 거리 설정 완료, 사업장 이름:{} / 허용 거리:{}", store.getName(), store.getAllowDistance());
        return new ResponseEntity("Success", HttpStatus.OK);
    }

    /*10.가입 대기 직원 목록 확인*/
    @Operation(summary = "사업장에 가입 대기중인 직원 목록")
    @PostMapping("/store/wait/employee/")
    public GetWaitEmployeeResponse getWaitEmployee() {
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        Long memberId = memberKakaoApiService.getMemberInfoFromAccessToken(accessToken);
        Store store = memberRepository.findOne(memberId).getActiveStore();

        return storeService.getWaitEmployee(store);
    }

    /*11.가입 대기 직원 승인 & 삭제*/
    @Operation(summary = "가입 대기 직원 승인&삭제", description = "사장만 접근 가능.")
    @PostMapping("/store/wait/employee/allow/")
    public ResponseEntity allowNewEmployee(@RequestBody NewEmployeeRequest newEmployeeRequest) {
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        Long memberId = memberKakaoApiService.getMemberInfoFromAccessToken(accessToken);
        Member member = memberRepository.findOne(memberId);
        MemberPosition position = belongRepository.findBelongInfo(member, member.getActiveStore()).getPosition();

        if (position != MemberPosition.President) {
            log.warn("**가입 대기 직원 승인&삭제 실패 - 사장만 승인&삭제 가능.");
            return new ResponseEntity("Failed", HttpStatus.BAD_REQUEST);
        }

        Store store = member.getActiveStore();
        List<Long> employeeIdList = newEmployeeRequest.getEmployeeIdList();

        return storeService.allowNewEmployee(store, employeeIdList, newEmployeeRequest.getRequest());
    }

    /*---------------DTO-----------------*/
    /*사업장 생성을 위한 Request DTO, ResponseDTO*/
    @Data
    static class CreateStoreRequest {
        private String status;
        private String placeName;
        private String address;
        private String detailAddr;
    }
    /*사업장 생성, 가입 Response DTO*/
    @Data
    @AllArgsConstructor
    static class CreateAndJoinStoreResponse {
        private Long memberId;
        private Long storeId;
        private String message;
        private String screen;

        @JsonDeserialize(using= LocalDateDeserializer.class)
        @JsonSerialize(using= LocalDateSerializer.class)
        private LocalDate belongDate;

        //NoArgsConstructor
        public CreateAndJoinStoreResponse() {
            this.message = "Failed";
            this.screen = "PlaceRegister";
        }

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
        private Long employeeId;
        private Integer newSalary;
    }

    /*사업장 출근 허용 가능 거리 설정을 위한 Request DTO*/
    @Data
    static class SetStoreAllowDistanceRequest {
        private Integer allowDistance;
    }

    /*새로운 직원 승인하기 위한 Request DTO*/
    @Data
    static class NewEmployeeRequest {
        private List<Long> employeeIdList;
        private String request; //승인 or 삭제
    }
}