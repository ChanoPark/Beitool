package com.beitool.beitool.api.service;

import com.beitool.beitool.api.dto.AuthorizationKakaoDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * 2022-03-21 카카오 소셜 로그인
 *
 * Implemented by Chanos
 */

@Service
@RequiredArgsConstructor
public class MemberKakaoApiService {
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    /*컨트롤러에서 엑세스토큰을 받으면(프론트에서 로그인하면) 사용자 확인 & 토큰 만료 확인*/
    public void getTokenInfo(AuthorizationKakaoDto authorizationKakaoDto) {
        String token = authorizationKakaoDto.getAccessToken();

        //헤더
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        //Http 엔티티로 조합
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(headers);

        //카카오에게 GET 요청
        ResponseEntity<String> response = restTemplate.exchange(
                "https://kapi.kakao.com/v1/user/access_token_info",
                HttpMethod.GET,
                entity,
                String.class
        );

        //결과
        System.out.println("***토큰정보의 response: " + response.getBody());

        //남은 초 비교해서 토큰 갱신 여부 판단하자 -> JSON을 스트링으로 파싱하면 되지않을까? (GSON활용)
    }

    /*컨트롤러에서 카카오 엑세스 토큰을 받아 회원정보 불러오는 API 호출*/
    public void getMemberInfo(AuthorizationKakaoDto authorizationKakaoDto) {
        String token = authorizationKakaoDto.getAccessToken();
        System.out.println("***token = " + token);

        //헤더
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token); //데이터를 두 번 저장할 경우 set은 덮어쓰고, add는 추가되어 두개가 조회됌.
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        //Http 엔티티로 조합
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(headers);

        //카카오에게 POST 요청
        ResponseEntity<String> response = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                entity,
                String.class
        );

        System.out.println("***Response: " + response.getBody());
    }
}
