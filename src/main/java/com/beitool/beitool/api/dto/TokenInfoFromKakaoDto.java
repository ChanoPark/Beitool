package com.beitool.beitool.api.dto;

import lombok.Data;
import lombok.Getter;

/** 2022-03-27
 * 엑세스 토큰을 카카오 API에 전송해서 얻은 토큰 정보를 받기 위한 DTO
 * 카카오 토큰 정보 가져올 때 사용하는 DTO
 * Implemented By Chanos
 **/
@Getter @Data
public class TokenInfoFromKakaoDto {
    private Long id; //회원 번호
    private Integer expires_in; //엑세스 토큰 만료 시간(초)
    private Integer expiresInMillis; //엑세스 토큰 만료 밀리초

    private String app_id; //우리 앱 ID
    private String appId; //우리 앱 ID
}
