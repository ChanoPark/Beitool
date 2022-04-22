package com.beitool.beitool.api.dto.store;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

/**
 * 2022-04-15 사용자가 지도에 들어왔을 때, 사업장의 위도,경도,출퇴근 허용거리 반환하기 위한 Response DTO
 * Implemented by Chanos
 */
@Data @Setter @AllArgsConstructor
public class StoreAddressResponseDto {
    private double latitude; //사업장 위도
    private double longitude; //사업장 경도
    private int allowDistance; //출퇴근 허용 거리
    private String message; //response 메세지
}