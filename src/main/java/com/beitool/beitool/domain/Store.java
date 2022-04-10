package com.beitool.beitool.domain;

import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 2022-04-10 사업장과 관련된 도메인
 *
 * Implemented by Chanos
 */
@Entity
@Getter @Table(name="store")
public class Store {

    @Id @GeneratedValue
    @Column(name="store_id")
    private Long id; //사업장 기본키

    @Column(name="store_name")
    private String name; //사업장 이름

//    private String code; //사업장 코드 -> 방식 미정

    @Column(name="store_address")
    private String address; //사업장 주소
    @Column(name="store_address_detail")
    private String addressDetail; //사업장 상세 주소

    private double latitude; //사업장 위도
    private double longitude; //사업장 경도

    @Column(name="allow_distance")
    private int allowDistance; //출퇴근 허용 거리
    @Column(name="salary_cycle")
    private int salaryCycle; //급여 주기
    @Column(name="salary_date")
    private LocalDateTime salaryDate; //급여 날짜

}
