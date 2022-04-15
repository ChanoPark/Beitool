package com.beitool.beitool.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 2022-04-10 사업장과 관련된 도메인
 *
 * Implemented by Chanos
 */
@Entity
@Getter @Table(name="store")
@NoArgsConstructor
public class Store {

    public Store(String storeName, int code, String address, String addressDetail, double latitude, double longitude) {
        this.name=storeName;
        this.inviteCode=code;
        this.address=address;
        this.addressDetail=addressDetail;
        this.latitude=latitude;
        this.longitude=longitude;
        this.allowDistance=500; //출퇴근 허용거리 디폴트 500M
    }


    @Id @GeneratedValue
    @Column(name="store_id")
    private Long id; //사업장 기본키

    @Column(name="store_name") @NotNull
    private String name; //사업장 이름

    @Column(name="invite_code") @NotNull
    private int inviteCode; //사업장 코드 -> 우선 4자리 난수로 발급

    @Column(name="store_address") @NotNull
    private String address; //사업장 주소
    @Column(name="store_address_detail") @NotNull
    private String addressDetail; //사업장 상세 주소

    @NotNull
    private double latitude; //사업장 위도
    @NotNull
    private double longitude; //사업장 경도

    @Column(name="allow_distance")
    private int allowDistance; //출퇴근 허용 거리
    @Column(name="salary_cycle")
    private int salaryCycle; //급여 주기
    @Column(name="salary_date")
    private LocalDateTime salaryDate; //급여 날짜

}
