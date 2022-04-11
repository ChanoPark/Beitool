package com.beitool.beitool.api.controller;

import com.beitool.beitool.api.dto.AuthorizationKakaoDto;
import com.beitool.beitool.api.repository.MemberRepository;
import com.beitool.beitool.api.service.MemberKakaoApiService;
import com.beitool.beitool.api.service.MemberService;
import com.beitool.beitool.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

/*
* 2022-03-26
* 회원과 관련된 요청을 처리하는 컨트롤러 (로그인/회원가입)
* 1.엑세스 토큰을 받아 회원 정보 확인(신규/기존)
* 2.직급을 받아 직급 선택(deprecated)
* Implemented By Chanos
* */
@RestController
@RequiredArgsConstructor
public class MemberSocialApiController {
    private final MemberKakaoApiService kakaoApiService;
    private final MemberService memberService;
    private final MemberRepository memberRepository;

    /*로그인 후 엑세스토큰으로 회원 확인(신규/기존) */
    @PostMapping("/login/kakao")
    public AuthorizationKakaoDto getKakaoToken(@RequestBody AuthorizationKakaoDto token) {
        System.out.println("***전달받은 토큰 = " + token);
        System.out.println("***엑세스 토큰 = " + token.getAccessToken());
        try {
            kakaoApiService.getTokenInfo(token);
        } catch (HttpClientErrorException e) { //토큰이 만료된 경우
            Member findMember = memberRepository.findByRefreshToken(token.getRefreshToken());

            if (findMember.getRefreshToken() == "InvalidUser") { //리프레시 토큰이 잘못된 회원이라면 다시 로그인
                token.setScreen("LoginScreen");
                return token;
            }
            kakaoApiService.updateAccessToken(token, findMember, token.getRefreshToken()); //리프레시토큰으로 갱신
            kakaoApiService.getTokenInfo(token); //토큰 업데이트 후, 회원 정보 확인
        }
        return token;
    }

    /*직급 선택*/
    /*직급 선택할 때, 가게 등록이면 사장이고, 가입이면 직원이기 때문에 한 번에 처리하는 것으로 함.*/
    @PostMapping("/register/position/")
    public PositionResponse setPosition(@RequestBody PositionRequest position) {
        System.out.println("**넘어온거: " + position);
        String screen = memberService.setPosition(position.getId(), position.getPosition());

        return new PositionResponse(position.getId(), position.getPosition(), screen);
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