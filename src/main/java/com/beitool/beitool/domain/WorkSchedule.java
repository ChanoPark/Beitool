package com.beitool.beitool.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 근무 예정 표를 작성하기 위한 도메인
 * @author Chanos
 * @since 2022-05-23
 */
@Entity @Table(name="work_schedule")
@Getter @NoArgsConstructor
public class WorkSchedule {

    public WorkSchedule(Member member, Store store,
                        LocalDate workDay, LocalDateTime workStartTime, LocalDateTime workEndTime) {
        this.member = member;
        this.store = store;
        this.workDay = workDay;
        this.workStartTime = workStartTime;
        this.workEndTime = workEndTime;
    }

    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="store_id")
    private Store store;

    @JsonDeserialize(using= LocalDateDeserializer.class)
    @JsonSerialize(using= LocalDateSerializer.class)
    @Column(name="work_day")
    private LocalDate workDay; //근로 날짜

    @JsonDeserialize(using= LocalDateTimeDeserializer.class)
    @JsonSerialize(using= LocalDateTimeSerializer.class)
    @Column(name="work_start_time")
    private LocalDateTime workStartTime; //출근 시간

    @JsonDeserialize(using=LocalDateTimeDeserializer.class)
    @JsonSerialize(using=LocalDateTimeSerializer.class)
    @Column(name="work_end_time")
    private LocalDateTime workEndTime; //퇴근 시간
}