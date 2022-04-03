package com.beitool.beitool.api.dto;

import lombok.Data;
import lombok.Getter;

/** 2022-03-29
 * 리프레시 토큰을 사용해 엑세스 토큰을 갱신할 때 사용하는 DTO
 *
 * Implemented by Chanos
 */
@Getter @Data
public class UpdateTokenFromKakaoDto {
    String access_token;
    String refresh_token;
    String id_token;
    String token_type;
    Integer refresh_token_expires_in;
    Integer expires_in;
}