package com.beitool.beitool.api.dto.store;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

/**
 * 2022-04-15 사용자가 지도에 들어왔을 때, 사업장의 위도,경도,출퇴근 허용거리 반환하기 위한 Response DTO
 * Implemented by Chanos
 */
@Data @Setter @AllArgsConstructor
public class StoreAddressResponseDto {
    @ApiModelProperty(value="사업장 위도", example="92.123123")
    private double latitude;

    @ApiModelProperty(value="사업장 경도", example="182.312412")
    private double longitude;

    @ApiModelProperty(value="출퇴근 허용 거리(단위:m)", example="300")
    private int allowDistance;

    @ApiModelProperty(value="결과 메시지", example="Working & noWorking")
    private String message;
}