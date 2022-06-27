package com.beitool.beitool.api.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
/*
* 토큰 값을 주고 받기 위한 DTO (프론트로부터 받은 토큰값 관리를 위한 DTO)
*
* @author Chanos
* @since 2022-03-26
**/
@Getter @Data
public class AuthorizationKakaoDto {

    @ApiModelProperty(value="리프레시 토큰", example="zKoqkm1Imv")
    private String refreshToken; //리프레시 토큰 값

    @ApiModelProperty(value="ID 토큰", example="openID Connect 활성화할 때 사용 예정")
    private String idToken; //ID토큰 값

    @ApiModelProperty(value="사용자 정보 조회할 때, 권한 범위", example="프로필 사진 등록 필요!!")
    private String[] scopes; //사용자의 정보 조회 권한 범위 ->> 안쓰는중

    @ApiModelProperty(value="카카오에서 App ID", example="719381")
    private String app_id; //우리 앱ID(카카오)

    @ApiModelProperty(value="엑세스 토큰 만료 시간", example="2022-05-26 00:12:42")
    private String accessTokenExpiresAt; //엑세스 토큰 만료 시간 (ID토큰 만료 시간과 동일)

    @ApiModelProperty(value="리프레시 토큰 만료 시간", example="2022-06-26 01:13:42")
    private String refreshTokenExpiresAt; //리프레시 토큰 만료 날짜 (포맷: 2022-05-26 00:48:48)

    @ApiModelProperty(value="프론트에서 띄우는 화면 이름", example="UserSelect")
    private String screen; // 프론트에서 띄우는 화면 이름

    @ApiModelProperty(value="회원ID (카카오의 회원 번호 사용)", example="1729841")
    private Long id; // 로그인 후 회원ID

    public void setId(Long id) {
        this.id = id;
    }

    public void setAppId(String id) {
        this.app_id = id;
    }

    public void setScreen(String screen) {
        this.screen = screen;
    }

    public void setAccessToken(String token) {
        this.accessToken = token;
    }
}
