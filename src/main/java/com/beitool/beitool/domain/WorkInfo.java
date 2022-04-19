package com.beitool.beitool.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 2022-04-10 근로정보와 관련된 테이블
 * 근로정보는 캘린더에 띄울 때, 조회 쿼리가 많이 발생할 것으로 예상해 분리해서 관리한다.
 * Implemented by Chanos
 */
@Entity
@IdClass(BelongPK.class)
@Getter @Table(name="work_info") @NoArgsConstructor
public class WorkInfo {

    public WorkInfo(Member member, Store store, LocalDateTime workStartTime) {
        this.member = member;
        this.store = store;
        this.workStartTime = workStartTime;
    }


    @Id @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="member_id")
    public Member member;

    @Id @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="store_id")
    public Store store;

//    @JsonDeserialize(using=LocalDateTimeDeserializer.class)
//    @JsonSerialize(using=LocalDateTimeSerializer.class)
//    @Column(name="work_date")
//    public LocalDateTime workDate; //근로 날짜

    @JsonDeserialize(using=LocalDateTimeDeserializer.class)
    @JsonSerialize(using=LocalDateTimeSerializer.class)
    @Column(name="work_start_time")
    public LocalDateTime workStartTime; //출근 시간

    @JsonDeserialize(using=LocalDateTimeDeserializer.class)
    @JsonSerialize(using=LocalDateTimeSerializer.class)
    @Column(name="work_end_time")
    public LocalDateTime workEndTime; //퇴근 시간
}
