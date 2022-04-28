package com.beitool.beitool.api.dto.store;

import com.beitool.beitool.domain.MemberPosition;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 소속된 사업장의 정보를 저장하기 위한 클래스
 * 현재, 사업장 변경할 때 사용됨.
 * @author Chanos
 * @since 2022-04-22
 */
@Data
@AllArgsConstructor
public class BelongedStore {
    private String storeName;
    private Long storeId;
    private String memberName;
    private MemberPosition memberPosition;
}