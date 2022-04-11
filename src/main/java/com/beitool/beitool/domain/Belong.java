package com.beitool.beitool.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 2022-04-10 사업장에 소속된 회원을 관리하기 위한 클래스
 * 사업장에 소속된 각 회원의 정보를 수정하거나, 조회할 때 사용하기 위함. (기능이 늘어날수록 필요한 것들이 많아질 것이라 예상)
 * Implemented by Chanos
 */
@Entity
@IdClass(BelongPK.class)
@Getter @Table(name="belong")
@NoArgsConstructor
public class Belong {
    
    /*사업장 생성 후 사장의 가입을 위한 생성자*/
    public Belong(Member member, Store store, LocalDate currentTime) {
        this.member = member;
        this.store = store;
        this.belongDate = currentTime;
    }

    @Id @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member; //회원과 소속은 다대일관계(회원은 여러 사업장에 소속될 수 있음)

    @Id @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="store_id")
    private Store store; //가게와 소속은 다대일관계(가게는 여러 회원을 가질 수 있음)

    @JsonDeserialize(using=LocalDateDeserializer.class)
    @JsonSerialize(using= LocalDateSerializer.class)
    @Column(name="belong_date")
    private LocalDate belongDate; //가입 날짜

    @Column(name="salary_hour")
    private int salaryHour; //시급

}
