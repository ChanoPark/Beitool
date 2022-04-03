package com.beitool.beitool.api.controller;

import com.beitool.beitool.api.dto.AuthorizationKakaoDto;
import com.beitool.beitool.api.service.MemberKakaoApiService;
import com.beitool.beitool.api.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
/*
* 2022-03-26
* 회원과 관련된 요청을 처리하는 컨트롤러 (로그인/회원가입)
* Implemented By Chanos
* */
@RestController
@RequiredArgsConstructor
public class MemberSocialApiController {
    private final MemberKakaoApiService kakaoApiService;
    private final MemberService memberService;

    /*로그인 후 엑세스토큰으로 회원 확인(신규/기존) */
    @PostMapping("/login/kakao")
    public AuthorizationKakaoDto getKakaoToken(@RequestBody AuthorizationKakaoDto token) {
        System.out.println("***전달받은 토큰 = " + token);
        System.out.println("***엑세스 토큰 = " + token.getAccessToken());
        kakaoApiService.getTokenInfo(token);
        return token;
    }

    /*직급 선택*/
    @PostMapping("/register/position/{id}")
    public PositionResponse setPosition(@PathVariable Long id, @RequestBody PositionRequest position) {
        System.out.println("**넘어온거: " + position.getPosition());
        String screen = memberService.setPosition(id, position.getPosition());

        return new PositionResponse(id, position.getPosition(), screen);
    }

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