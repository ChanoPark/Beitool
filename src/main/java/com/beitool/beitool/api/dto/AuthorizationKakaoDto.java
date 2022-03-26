package com.beitool.beitool.api.dto;

import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;
/*
* 2022-03-27 토큰 값을 주고 받기 위한 DTO
*
* Implemented By Chanos
* */
@Getter @Data
public class AuthorizationKakaoDto {
    private String accessToken; //엑세스 토큰 값
    private String refreshToken; //리프레시 토큰 값
    private String idToken; //ID토큰 값
    private LocalDateTime accessTokenExpiresAt; //엑세스 토큰 만료 시간 (ID토큰 만료 시간과 동일)
    private String scopes; //사용자의 정보 조회 권한 범위
    private LocalDateTime refreshTokenExpiresAt; //리프레시 토큰 만료 날짜 (포맷: 2022-05-26 00:48:48)
}
