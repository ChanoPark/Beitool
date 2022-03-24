package com.beitool.beitool.api.service;

import com.beitool.beitool.api.dto.AuthorizationKakaoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * 2022-03-21 카카오 소셜 로그인 개발
 * 회원과 관련된 서비스 제공
 * Implemented by 박찬호
 */

@Service
@RequiredArgsConstructor
public class MemberService {
    private final Oauth2Kakao oauth2Kakao;

    public void oauth2AuthorizationKakao(String code) {
        AuthorizationKakaoDto authorization = oauth2Kakao.callTokenApi(code);
    }

    public void getMemberInfo(String token) {
        System.out.println("token = " + token);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//        params.add("Authorization", token);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);

        RestTemplate rt = new RestTemplate();

        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                entity,
                String.class
        );

        System.out.println("Response: " + response);
    }
}
