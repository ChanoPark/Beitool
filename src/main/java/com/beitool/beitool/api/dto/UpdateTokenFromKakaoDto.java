package com.beitool.beitool.api.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;

/**
 * 리프레시 토큰을 사용해 엑세스 토큰을 갱신할 때 사용하는 DTO
 *
 * @author Chanos
 * @since 2022-03-29
 */
@Getter @Data
public class UpdateTokenFromKakaoDto {

    @ApiModelProperty(value="엑세스 토큰", example="nLAmq1Oc")
    String access_token;

    @ApiModelProperty(value="리프레시 토큰", example="Knql1mOz")
    String refresh_token;

    @ApiModelProperty(value="ID 토큰")
    String id_token;

    @ApiModelProperty(value="토큰 종류")
    String token_type;

    @ApiModelProperty(value="리프레시 토큰 만료 시간", example="23987")
    Integer refresh_token_expires_in;

    @ApiModelProperty(value="엑세스 토큰 만료 시간", example="98145")
    Integer expires_in;
}