package com.beitool.beitool.api.service;

import com.beitool.beitool.api.dto.AuthorizationKakaoDto;
import com.beitool.beitool.api.dto.TokenInfoFromKakaoDto;
import com.beitool.beitool.api.dto.UpdateTokenFromKakaoDto;
import com.beitool.beitool.api.repository.BelongRepository;
import com.beitool.beitool.api.repository.MemberRepository;
import com.beitool.beitool.domain.Member;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

/**
 * 1.카카오API에게 엑세스 토큰을 주고, 토큰 정보를 획득(만료 기간 등)
 * 2.DB정보를 통해 신규/기존 유저 구분
 * 3.리프레시 토큰을 사용해 엑세스 토큰 갱신(+리프레시 토큰이 얼마 남지 않을 경우 같이 갱신)
 * 4.엑세스 토큰을 통해 회원 정보 획득(회원 번호 등)
 *
 * @author Chanos
 * @since 2022-03-21
 */

@RequiredArgsConstructor
@Transactional
@Slf4j
@Service
public class MemberKakaoApiService {
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private final MemberRepository memberRepository;
    private final BelongRepository belongRepository;

    /*컨트롤러에서 엑세스토큰을 받으면(프론트에서 로그인하면) 사용자 확인 & 토큰 만료 확인*/
    /*1.토큰 정보 확인*/
    public void getTokenInfo(String token, AuthorizationKakaoDto authorizationKakaoDto) throws HttpClientErrorException {

        //헤더
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        //Http 엔티티로 조합
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(headers);

        //카카오에게 GET 요청
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                "https://kapi.kakao.com/v1/user/access_token_info",
                HttpMethod.GET,
                entity,
                String.class
            );

            //결과
            log.info("**카카오로부터 받은 토큰 정보:{} ", response.getBody());

            //남은 초 비교해서 토큰 갱신 여부 판단하자(GSON활용)
            String tokenInfoFromKakao = response.getBody();

            TokenInfoFromKakaoDto tokenInfo = objectMapper.readValue(tokenInfoFromKakao, TokenInfoFromKakaoDto.class);
            authorizationKakaoDto.setAppId(tokenInfo.getAppId());
            authorizationKakaoDto.setId(tokenInfo.getId());

            //엑세스 토큰 만료 시간이 얼마 남지 않은 경우
            if (tokenInfo.getExpires_in() <= 6000) { //토큰만료가 100분 이하일경우
                log.warn("**엑세스 토큰의 만료 시간이 100분 이하입니다.");
                throw new HttpClientErrorException(UNAUTHORIZED);// 리프레시 토큰을 활용해 토큰 갱신 -> 컨트롤러로 throws
            } else {
                //기존 유저인지 확인부터 해야지
                checkNewMember(authorizationKakaoDto, token);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    /*컨트롤러에서 카카오 엑세스 토큰을 받아 회원정보 불러오는 API 호출*/
    /*2.신규 유저와 기존 유저 구분(직급 유/무까지)*/
    public void checkNewMember(AuthorizationKakaoDto authorizationKakaoDto, String accessToken) {
        Long kakaoUserId = getMemberInfoFromAccessToken(accessToken);

        //프론트에게 전해줄 회원ID
        authorizationKakaoDto.setId(kakaoUserId);

        //유저 생성
        if(memberRepository.findOne(kakaoUserId) == null) { //신규 유저
            Member member = new Member(kakaoUserId, authorizationKakaoDto.getRefreshToken());
            memberRepository.save(member);
            authorizationKakaoDto.setScreen("UserSelect");
        } else { //기존 유저 -> 소속 여부 확인
            Member findMember = memberRepository.findOne(kakaoUserId);
            if (belongRepository.findBelongCount(findMember) > 0)
                authorizationKakaoDto.setScreen("MainScreen"); //소속된 곳이 있으면 메인 화면으로 이동
            else
                authorizationKakaoDto.setScreen("UserSelect"); //소속된 곳이 없으면 직급 선택으로 이동
        }
    }

    /*3.리프레시 토큰을 사용해서 엑세스토큰 갱신*/
    public AuthorizationKakaoDto updateAccessToken(AuthorizationKakaoDto token, Member member, String refreshToken) throws HttpClientErrorException {
        final String appKey = "64ebcc55e9ce025378904a725743ba67"; // 카카오 REST API Key 값

        //헤더
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        //바디
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "refresh_token");
        params.add("client_id", appKey);
        params.add("refresh_token", refreshToken);

        //Http 엔티티로 조합
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);

        //카카오에게 POST 요청
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    "https://kauth.kakao.com/oauth/token",
                    HttpMethod.POST,
                    entity,
                    String.class
            );
            String responseBody = response.getBody();
            UpdateTokenFromKakaoDto newTokenInfo = objectMapper.readValue(responseBody, UpdateTokenFromKakaoDto.class);
            //리프레시 토큰 업데이트
            log.info("**새로 발급된 엑세스 토큰:{}", newTokenInfo.getAccess_token());
            log.info("**새로 발급된 리프레시 토큰:{}", newTokenInfo.getRefresh_token());
            memberRepository.updateRefreshToken(member, newTokenInfo.getRefresh_token());
            token.setAccessToken(newTokenInfo.getAccess_token());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return token;
    }

    /*4.엑세스토큰을 통해 회원찾기*/
    public Long getMemberInfoFromAccessToken(String accessToken) {
        Long memberId = -1L; //예외 발생시 -1 반환
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken); //데이터를 두 번 저장할 경우 set은 덮어쓰고, add는 추가되어 두개가 조회됌.
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

        log.info("엑세스 토큰을 활용해 얻은 회원 정보:{}", response.getBody());
        String responseBody = response.getBody();
        try {
            Map<String, Object> memberInfo = objectMapper.readValue(responseBody, new TypeReference<Map<String, Object>>() {});
            memberId = (Long) memberInfo.get("id");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return memberId;
    }
}