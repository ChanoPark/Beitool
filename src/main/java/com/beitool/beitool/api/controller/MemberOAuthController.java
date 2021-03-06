package com.beitool.beitool.api.controller;

import com.beitool.beitool.api.dto.AuthorizationKakaoDto;
import com.beitool.beitool.api.dto.member.TokenResponseDto;
import com.beitool.beitool.api.repository.MemberCertificationRepository;
import com.beitool.beitool.api.repository.MemberRepository;
import com.beitool.beitool.api.repository.StoreRepository;
import com.beitool.beitool.api.service.MemberKakaoApiService;
import com.beitool.beitool.api.service.MemberService;
import com.beitool.beitool.domain.Member;
import com.beitool.beitool.domain.Store;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * 회원과 관련된 요청을 처리하는 컨트롤러 (로그인/회원가입)
 *
 * 1.엑세스 토큰을 받아 회원 정보 확인(신규/기존)
 * 2.직급을 받아 직급 선택(deprecated)
 * 3.회원이 사용하는 사업장 변경
 *
 * @author Chanos
 * @since 2022-06-25
 */

@Api(tags="회원")
@Slf4j
@RequiredArgsConstructor
@RestController
public class MemberSocialApiController {
    private final MemberKakaoApiService kakaoApiService;
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final MemberCertificationRepository memberCertificationRepository;
    private final MemberKakaoApiService memberKakaoApiService;
    private final StoreRepository storeRepository;
    private final HttpServletRequest request;

    /*1.로그인 후 엑세스토큰으로 회원 확인(신규/기존) */
    @Operation(summary = "로그인 V1", description = "카카오에서 발급받은 토큰만을 활용. 엑세스토큰 인증 실패->리프레시 토큰으로 재발급")
    @PostMapping("/login/kakao/")
    public AuthorizationKakaoDto kakaoLogin(@RequestBody AuthorizationKakaoDto authorizationKakaoDto) {
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        try {
            kakaoApiService.getTokenInfo(accessToken, authorizationKakaoDto);
        } catch (HttpClientErrorException e) { //토큰이 만료된 경우
            try {
                Member findMember = memberRepository.findByRefreshToken(authorizationKakaoDto.getRefreshToken());
                kakaoApiService.updateAccessToken(authorizationKakaoDto, findMember, authorizationKakaoDto.getRefreshToken()); //리프레시토큰으로 갱신
                kakaoApiService.getTokenInfo(authorizationKakaoDto.getAccessToken(), authorizationKakaoDto); //토큰 업데이트 후, 회원 정보 확인
            } catch (NoResultException e2) {
                log.info("**로그인 실패 - 리프레시토큰이 유효하지 않음.");
                authorizationKakaoDto.setScreen("LoginScreen");

                return authorizationKakaoDto;
            }
        }
        log.info("**로그인 성공");
        return authorizationKakaoDto;
    }

    @Operation(summary = "로그인 V2", description = "카카오 토큰을 받아서 자체 토큰 발급")
    public TokenResponseDto kakaoLoginV2() {
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        //회원 정보 조회
        long id = kakaoApiService.getMemberInfoFromAccessToken(accessToken);

        Optional<Member> member = memberCertificationRepository.findById(id);

        //기존 회원
        if (member.isPresent()) {

        } else {

        }

        return null;
    }

    /*2.직급 선택*/
    /*직급 선택할 때, 가게 등록이면 사장이고, 가입이면 직원이기 때문에 한 번에 처리하는 것으로 함.*/
//    @PostMapping("/register/position/")
//    public PositionResponse setPosition(@RequestBody PositionRequest position) {
//        System.out.println("**넘어온거: " + position);
//        String screen = memberService.setPosition(position.getId(), position.getPosition());

//        return new PositionResponse(position.getId(), position.getPosition(), screen);
//    }

    /*3.회원이 사용하는 사업장 변경*/
    @Operation(summary = "활성화된 사업장 변경", description = "회원의 사용중인 사업장 변경")
    @PostMapping("/member/change/activestore/")
    public void changeStore(@RequestParam("storeId") Long storeId) {
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        Long memberId = memberKakaoApiService.getMemberInfoFromAccessToken(accessToken);
        Member member = memberRepository.findOne(memberId);
        Store store =storeRepository.findOne(storeId);

        memberService.changeStore(member, store);
        log.info("**사업장 변경 성공, 사업장번호:{}", storeId);
    }

    /*-----DTO-----*/

    /*직급 등록을 위한 이너 클래스 DTO*/
    @Data
    static class PositionRequest {
        private Long id;
        private String position;
    }

    @Data @AllArgsConstructor
    static class PositionResponse {
        private Long id;
        private String position;
        private String screen;
    }
}