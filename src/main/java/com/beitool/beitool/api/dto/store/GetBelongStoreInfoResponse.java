package com.beitool.beitool.api.dto.store;

import com.beitool.beitool.domain.MemberPosition;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
/**
 * 사업장 변경 시 데이터를 전달하기 위한 Response DTO
 * @author Chanos
 * @since 2022-04-25
 */
@Data
public class GetBelongStoreInfoResponse {
    private String activeStoreName;
    private MemberPosition activeStorePosition;
    private List<BelongedStore> belongedStore;

    public GetBelongStoreInfoResponse() {
        this.belongedStore = new ArrayList<>();
    }

    public void setBelongedStore(BelongedStore belongedStore) {
        this.belongedStore.add(belongedStore);
    }
}