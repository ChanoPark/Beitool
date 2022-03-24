package com.beitool.beitool.api.controller;

import com.beitool.beitool.api.dto.AuthorizationKakaoDto;
import com.beitool.beitool.api.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberSocialApiController {
    private final MemberService memberService;

    @GetMapping("/oauth2/authorization/kakao")
    public void oauth2AuthorizationKakao(@RequestBody String code) {
        memberService.oauth2AuthorizationKakao(code);
    }

    @PostMapping("/login/kakao")
    public void getKakaoToken(@RequestBody String token) {
        AuthorizationKakaoDto authorizationKakaoDto;

//        authorizationKakaoDto.setAccessToken(token);

        memberService.getMemberInfo(token);

    }

}