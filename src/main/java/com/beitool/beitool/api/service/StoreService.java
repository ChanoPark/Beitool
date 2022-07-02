package com.beitool.beitool.api.service;

import com.beitool.beitool.api.dto.store.*;
import com.beitool.beitool.api.repository.BelongRepository;
import com.beitool.beitool.api.repository.WorkInfoRepository;
import com.beitool.beitool.api.repository.MemberRepository;
import com.beitool.beitool.api.repository.StoreRepository;
import com.beitool.beitool.domain.Belong;
import com.beitool.beitool.domain.Member;
import com.beitool.beitool.domain.MemberPosition;
import com.beitool.beitool.domain.Store;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.NoResultException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 사업장과 관련된 서비스를 제공하는 클래스
 *
 * 1.사업장 생성
 * 2.도로명 주소를 위/경도로 변환(사업장 주소를 위함)
 * 3.사업장 코드 생성(5자리 난수)
 * 4.사업장 생성과 동시에 사장이 소속
 * 5.직원이 사업장에 가입 -> 4번 메소드 오버로딩
 * 6.지도에 들어왔을 때 사업장 위도,경도,출퇴근허용거리 반환
 * 7.사업장 변경(소속되어 있는 사업장 정보 반환)
 * 8.소속되어 있는 직원 목록 반환
 * 9.직원 급여 변경
 * 10.사업장 출퇴근 허용 거리 변경
 * 11.가입 대기 직원 목록 조회
 * 12.가입 대기 직원 승인
 * 13.가입 대기 직원 삭제
 *
 * @author Chanos
 * @since 2022-06-08
 */

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class StoreService {
    private final RestTemplate restTemplate;
    private final MemberRepository memberRepository;
    private final MemberKakaoApiService memberKakaoApiService;
    private final StoreRepository storeRepository;
    private final WorkInfoRepository workInfoRepository;
    private final BelongRepository belongRepository;

    /*1.사업장 생성*/
    public Store createStore(String storeName, String address, String addressDetail) {
        //사업장 위도, 경도 추출
        Map<String, Double> axis = changeAxis(address);
        int code = createStoreCode();

        Store store = new Store(storeName, code, address, addressDetail, axis.get("latitude"), axis.get("longitude"));
        storeRepository.createStore(store);
        System.out.println("***생성된 사업장 번호:" + store.getId());
        log.info("**사업장 생성 성공, 생성된 사업장 번호:{} / 사업장 코드:{}", store.getId(), store.getInviteCode());

        return store;
    }

    /*2.카카오 로컬 API를 활용해 도로명 주소를 좌표(위도, 경도)로 변환*/
    public Map<String, Double> changeAxis(String address) {
        String appKey = "64ebcc55e9ce025378904a725743ba67"; // 카카오 REST API Key 값

        Map<String, Double> axis = new HashMap<>(); //리턴할 Map 선언

        //헤더
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "KakaoAK " + appKey);

        //Http 엔티티로 조합
        HttpEntity<String> entity = new HttpEntity<>(headers);

        //한글 주소를 보내기 위한 URI 빌드(인코딩)
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl("https://dapi.kakao.com/v2/local/search/address.json")
                .queryParam("query", address);

        //카카오에게 GET 요청
        ResponseEntity<String> response = restTemplate.exchange(
                builder.build().encode().toUri(),
                HttpMethod.GET,
                entity,
                String.class
        );

        String responseBody = response.getBody(); //카카오 로컬 API의 response

        try { //simple-json으로 json 배열 값 추출하기.
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(responseBody); //JSON 데이터 받기
            JSONArray documents = (JSONArray) jsonObject.get("documents"); //JSON 안에 있는 documents 배열 추출
            JSONObject addressInDocuments = (JSONObject) documents.get(0); //JSON 배열(documents)의 0번째 인덱스 address 추출
            System.out.println("***좌표(x,y):" + addressInDocuments.get("x") + ", " + addressInDocuments.get("y"));

            axis.put("longitude", Double.parseDouble(addressInDocuments.get("x").toString())); //address 안에 있는 x, y좌표
            axis.put("latitude", Double.parseDouble(addressInDocuments.get("y").toString())); //Object형이므로 Double 캐스팅

        } catch (org.json.simple.parser.ParseException e) {
            e.printStackTrace();
        }
        return axis;
    }

    /*3.사업장 코드 생성(난수 5자리)*/
    public int createStoreCode() {
        int code = -1;
        while(code < 0) {
            code = (int)(Math.random() * (99999 - 10000 + 1)) + 10000;
            try {
                storeRepository.findStoreByCode(code);
                code=-1;
                continue;
            }catch(NoResultException e) { //사업장 코드가 중복되지 않아 조회되지 않음.
                break;
            }
        }
        return code;
    }

    /*4.사장이 사업장을 생성함과 동시에 사업장에 소속되기 위한 메소드*/
    public LocalDate joinStore(Member member, Store store, String name) {
        LocalDate currentTime = LocalDate.now(ZoneId.of("Asia/Seoul"));
        Belong belong = new Belong(member, store, currentTime, MemberPosition.President ,name); //사업장 이름 = 가게 이름

        member.setActiveStore(store); //회원의 활성화된 사업장 디폴트 세팅

        belongRepository.createBelong(belong);
        return currentTime;
    }

    /*5.사업장에 직원이 가입하기 위한 메소드*/
    public Long joinStore(Member member, int inviteCode, LocalDate currentTime, String name) throws NoResultException {
        //사업장 코드로 사업장 조회
        Store store = storeRepository.findStoreByCode(inviteCode);

        //이름 중복 검사
        try {
            //NoResultException 이 안터지면 겹치는 이름이 있다는 것.
            belongRepository.findName(store, name);
            return -1L;
        } catch (NoResultException e) {
            Belong belong = new Belong(member, store, currentTime, MemberPosition.Waiting, name);

            member.setActiveStore(store); //회원의 사용중인 사업장 디폴트 세팅
            belongRepository.createBelong(belong);

            return store.getId();
        }
    }

    /*6.지도에 들어왔을 때 사업장 위도,경도,출퇴근허용거리 반환*/
    public StoreAddressResponseDto getStoreAddressAndAllowDistance(String accessToken) {
        String isWorking;

        Long memberId = memberKakaoApiService.getMemberInfoFromAccessToken(accessToken);
        Member findMember = memberRepository.findOne(memberId);

        Store findActiveStore = memberRepository.findOne(memberId).getActiveStore();
        Store findStore = storeRepository.findOne(findActiveStore.getId());

        double lat = findStore.getLatitude();
        double lon = findStore.getLongitude();
        int allowDistance = findStore.getAllowDistance();

        if (workInfoRepository.findWorkInfo(findMember, findStore) > 0) {
            isWorking = "working";
        } else {
            isWorking = "noWorking";
        }
        return new StoreAddressResponseDto(lat, lon, allowDistance, isWorking);
    }

    /*7.사업장 변경(소속되어 있는 사업장 정보 반환)*/
    public GetBelongStoreInfoResponse getBelongStoreInfo(Member member) {
        GetBelongStoreInfoResponse getBelongStoreInfoResponse = new GetBelongStoreInfoResponse();

        //활성화된 사업장 소속 정보 조회
        Belong activeStoreBelongInfo = belongRepository.findBelongInfo(member, member.getActiveStore());
        //활성화된 사업장 소속 정보 Response에 저장
        getBelongStoreInfoResponse.setActiveStoreName(activeStoreBelongInfo.getName());
        getBelongStoreInfoResponse.setActiveStorePosition(activeStoreBelongInfo.getPosition());

        //소속되어 있는 모든 사업장 소속 정보
        List<Belong> belongs = belongRepository.allBelongInfo(member);

        for (Belong belong : belongs) {
            String belongStoreName = belong.getStore().getName(); //소속된 사업장 이름
            //취합된 소속된 사업장 정보를 BelongedStore 클래스에 모아서 객체 생성
            BelongedStore belongedStore = new BelongedStore(belong.getName(), belong.getStore().getId(), belongStoreName, belong.getPosition());
            //HashMap에 소속된 사업장 정보 저장
            getBelongStoreInfoResponse.setBelongedStore(belongedStore);
        }
        log.info("**소속된 모든 사업장 정보 반환 성공, 소속된 사업장 개수:{}", belongs.size());
        return getBelongStoreInfoResponse;
    }

    /*8.소속되어 있는 직원 목록 반환*/
    public BelongEmployeeListResponseDto getBelongEmployeeList(Long storeId) {
        Store store = storeRepository.findOne(storeId);
        List<Belong> belongEmployeeList = belongRepository.getBelongEmployeeList(store);

        BelongEmployeeListResponseDto employeeListResponseDto = new BelongEmployeeListResponseDto();
        for (Belong employee : belongEmployeeList) {
            Long employeeId = employee.getMember().getId();
            String employeeName = employee.getName();
            employeeListResponseDto.setEmployee(employeeId, employeeName);
        }
        employeeListResponseDto.setMessage("Success");

        log.info("**소속되어 있는 직원 목록 반환 성공, 소속된 직원 수:{}", belongEmployeeList.size());
        return employeeListResponseDto;
    }

    /*9.직원 급여 변경*/
    public ResponseEntity setEmployeeSalary(Store store, Long employeeId, Integer newSalary) {
        Member employee = memberRepository.findOne(employeeId);
        Belong findBelong = belongRepository.findBelongInfo(employee, store);

        findBelong.setSalaryHour(newSalary);

        log.info("**직원 급여 변경 성공, 직원번호:{} / 가게이름:{} / 변경된 급여:{}",
                findBelong.getMember().getId(),findBelong.getName(),findBelong.getSalaryHour());

        return new ResponseEntity("Success", HttpStatus.OK);
    }

    /*10.사업장 출퇴근 허용 거리 변경*/
    @Transactional
    public void setStoreAllowDistance(Store store, Integer allowDistance) {
        store.setAllowDistance(allowDistance);
    }

    /*11.가입 대기 직원 목록 조회*/
    public GetWaitEmployeeResponse getWaitEmployee(Store store) {
        List<Belong> waitEmployeeList = belongRepository.findWaitEmployee(store);

        GetWaitEmployeeResponse waitEmployeeResponse = new GetWaitEmployeeResponse();

        for (Belong waitEmployee : waitEmployeeList) {
            Long employeeId = waitEmployee.getMember().getId();
            String employeeName = waitEmployee.getName();
            LocalDate belongDate = waitEmployee.getBelongDate();

            waitEmployeeResponse.addWaitEmployee(employeeId, employeeName, belongDate);
        }

        log.info("**가입 대기 직원 목록 조회 성공, 대기 직원 수:{}", waitEmployeeList.size());
        return waitEmployeeResponse;
    }

    /*12.가입 대기 직원 승인*/
    @Transactional
    public ResponseEntity allowNewEmployee(Store store, List<Long> employeeIdList, String request) {
        MemberPosition newPosition; //새로운 직급

        if (request == "Allow") {
            newPosition = MemberPosition.Employee;
        } else if (request == "Deny") {
            newPosition = MemberPosition.NoPosition;
        } else {
            return new ResponseEntity("Failed", HttpStatus.BAD_REQUEST);
        }

        for (Long employeeId : employeeIdList) {
            Member employee = memberRepository.findOne(employeeId);
            Belong belongInfo = belongRepository.allowNewEmployee(employee, store);
            belongInfo.setPosition(newPosition);
            log.info("가입 대기 직원 처리 결과 - 직원이름:{} / 직급:{}",belongInfo.getName(), belongInfo.getPosition());
        }
        return new ResponseEntity("Success", HttpStatus.OK);
    }
}
