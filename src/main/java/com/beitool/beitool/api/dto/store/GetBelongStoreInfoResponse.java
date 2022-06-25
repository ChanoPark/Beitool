package com.beitool.beitool.api.dto.store;

import com.beitool.beitool.domain.MemberPosition;
import io.swagger.annotations.ApiModelProperty;
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

    @ApiModelProperty(value="활성화된 사업장 이름", example="사업장1")
    private String activeStoreName;

    @ApiModelProperty(value="활성화된 사업장에서의 직급", example="President")
    private MemberPosition activeStorePosition;

    @ApiModelProperty(value="소속된 사업장 정보", example="사업장 이름, 번호 / 회원 이름, 번호 포함됨.")
    private List<BelongedStore> belongedStore;

    public GetBelongStoreInfoResponse() {
        this.belongedStore = new ArrayList<>();
    }

    public void setBelongedStore(BelongedStore belongedStore) {
        this.belongedStore.add(belongedStore);
    }
}