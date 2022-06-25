package com.beitool.beitool.api.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;

/**
 * 엑세스 토큰을 카카오 API에 전송해서 얻은 토큰 정보를 받기 위한 DTO
 * 카카오 토큰 정보 가져올 때 사용하는 DTO
 *
 * @author Chanos
 * @since 2022-03-27
 **/
@Getter @Data
public class TokenInfoFromKakaoDto {
    @ApiModelProperty(value="회원 번호", example="418231")
    private Long id;

    @ApiModelProperty(value="엑세스 토큰 만료 시간(단위: s)", example="253198")
    private Integer expires_in; //엑세스 토큰 만료 시간(초)

    @ApiModelProperty(value="엑세스 토큰 만료 시간(단위: ms)", example="50875109")
    private Integer expiresInMillis; //엑세스 토큰 만료 밀리초

    @ApiModelProperty(value="카카오에서 App ID", example="719381")
    private String app_id; //우리 앱 ID

    @ApiModelProperty(value="카카오에서 App ID", example="719381")
    private String appId; //우리 앱 ID
}
