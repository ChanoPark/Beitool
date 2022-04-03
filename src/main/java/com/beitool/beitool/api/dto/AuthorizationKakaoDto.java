package com.beitool.beitool.api.dto;

import lombok.Data;
import lombok.Getter;
/*
* 2022-03-26 토큰 값을 주고 받기 위한 DTO (프론트로부터 받은 토큰값 관리를 위한 DTO)
*
* Implemented By Chanos
**/
@Getter @Data
public class AuthorizationKakaoDto {
    private String accessToken; //엑세스 토큰 값
    private String refreshToken; //리프레시 토큰 값
    private String idToken; //ID토큰 값
    private String[] scopes; //사용자의 정보 조회 권한 범위 ->> 안쓰는중

    private String accessTokenExpiresAt; //엑세스 토큰 만료 시간 (ID토큰 만료 시간과 동일)
    private String refreshTokenExpiresAt; //리프레시 토큰 만료 날짜 (포맷: 2022-05-26 00:48:48)

    private String screen; // 프론트에서 띄우는 화면 이름
    private Long id; // 로그인 후 회원ID

    public void setId(Long id) {
        this.id = id;
    }

    public void setScreen(String screen) {
        this.screen = screen;
    }

    public void setAccessToken(String token) {
        this.accessToken = token;
    }
}
