package com.beitool.beitool.api.controller;

import com.beitool.beitool.api.dto.AuthorizationKakaoDto;
import com.beitool.beitool.api.service.MemberKakaoApiService;
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

    /*로그인 후 엑세스토큰으로 회원 확인(신규/기존) */
    @PostMapping("/login/kakao")
    public void getKakaoToken(@RequestBody AuthorizationKakaoDto token) {
        System.out.println("***전달받은 토큰 = " + token);
        System.out.println("***엑세스 토큰 = " + token.getAccessToken());
        kakaoApiService.getTokenInfo(token);

        //회원 정보 확인 (토큰 정보 확인 후, 기존 회원인지 확인하고 해야 할 필요가 있음)
//        kakaoApiService.getMemberInfo(token);
    }

}