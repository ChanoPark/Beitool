package com.beitool.beitool.domain;

import lombok.Data;
import java.io.Serializable;

/**
 * 회원(Member)과 사업장(Store)의 기본키를 갖고 복합키를 만들기 위함
 * 회원과 사업장을 갖고 조회하는 쿼리가 많이 발생할 것으로 예상되기 때문에, 복합키를 통해 성능을 올리고자 함.
 * 회원과 사업장 정보를 묶어 인덱스로 활용함으로써 향상된 성능 기대.
 * Belong, WorkInfo 클래스에서 사용(2022-04-10 기준)
 *
 * @author Chanos
 * @since 2022-04-10
 */
@Data
public class BelongPK implements Serializable {
    private Member member;
    private Store store;
}
