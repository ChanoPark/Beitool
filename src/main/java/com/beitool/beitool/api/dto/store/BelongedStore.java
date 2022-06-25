package com.beitool.beitool.api.dto.store;

import com.beitool.beitool.domain.MemberPosition;
import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(value="사업장 이름", example="사업장1")
    private String storeName;
    
    @ApiModelProperty(value="사업장 번호", example="5")
    private Long storeId;
    
    @ApiModelProperty(value="소속된 회원 이름", example="직원1")
    private String memberName;

    @ApiModelProperty(value="해당 사업장에서 회원의 직급", example="Employee")
    private MemberPosition memberPosition;
}