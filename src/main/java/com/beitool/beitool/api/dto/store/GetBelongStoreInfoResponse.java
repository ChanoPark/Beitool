package com.beitool.beitool.api.dto.store;

import com.beitool.beitool.domain.MemberPosition;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 사업장 변경 시 데이터를 전달하기 위한 Response DTO
 * @author Chanos
 * @since 2022-04-22
 */
@Data
public class GetBelongStoreInfoResponse {
    private String activeStoreName;
    private MemberPosition activeStorePosition;
    private List<Map<String, BelongedStore>> belongedStore;

    public GetBelongStoreInfoResponse() {
        this.belongedStore = new ArrayList<>();
    }

    public void setBelongedStore(BelongedStore belongedStore) {
        Map<String, BelongedStore> belongedStoreMap = new HashMap<>();
        belongedStoreMap.put("belongedStore", belongedStore);
        this.belongedStore.add(belongedStoreMap); //리스트안에 맵을 감싸서 보냄
    }
}