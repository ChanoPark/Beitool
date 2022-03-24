package com.beitool.beitool.api.service;

import com.beitool.beitool.api.dto.AuthorizationKakaoDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * 2022-03-30 카카오 소셜 로그인
 * 프론트에서 받은 카카오 토큰을
 */

@Service
@RequiredArgsConstructor
public class Oauth2Kakao {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private final String kakaoOauth2ClientId = "\\t64ebcc55e9ce025378904a725743ba67";
    private final String redirectUrl = "http://52.79.203.173:8080";

    public AuthorizationKakaoDto callTokenApi(String code) {
        String grantType = "authorization_code";

        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_Type", grantType);
        params.add("client_id", kakaoOauth2ClientId);
        params.add("redirect_uri", redirectUrl + "/callback/kakao");
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        String url = "https://kauth.kakao.com/oauth/token";
        System.out.println("login proceeding");
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            AuthorizationKakaoDto authorization = objectMapper.readValue(response.getBody(), AuthorizationKakaoDto.class);

            return authorization;
        } catch (RestClientException | JsonProcessingException ex) {
            ex.printStackTrace();
            throw new IllegalStateException(); /*임시*/
        }
    }

    public String callGetUserByAccessToken(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        String url = "https://kapi.kakao.com/v2/user/me";
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            // 값 리턴
            return response.getBody();
        }catch (RestClientException ex) {
            ex.printStackTrace();
            throw new IllegalStateException(); /*임시*/
        }
    }
}