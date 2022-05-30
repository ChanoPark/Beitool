package com.beitool.beitool.api.controller;

import com.beitool.beitool.api.dto.*;
import com.beitool.beitool.api.repository.MemberRepository;
import com.beitool.beitool.api.service.MemberKakaoApiService;
import com.beitool.beitool.api.service.WorkService;
import com.beitool.beitool.domain.Member;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.NoResultException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 회원 근로와 관련된 컨트롤러
 * 1.출퇴근
 * 2.근무 시프트 작성(캘린더)
 * 3.근무 시프트 조회(캘린더)
 * 4.근무 시프트 삭제(캘린더)
 * 5.근무 시프트 수정(캘린더)
 * 6.급여 계산기(사장)
 * 7.급여 계산기(직원)
 *
 * @since 2022-04-18
 * @author Chanos
 */
@RestController
@RequiredArgsConstructor
public class WorkApiController {
    private final MemberKakaoApiService memberKakaoApiService;
    private final MemberRepository memberRepository;
    private final WorkService workService;

    /*1.출퇴근(프론트에서 결과만 받음)*/
    @PostMapping("/work/commute/")
    public CommuteResponseDto workCommute(@RequestBody CommuteRequestDto commuteRequestDto) {
        String isWorking = commuteRequestDto.getWorkType(); //출근인지 퇴근인지
        return new CommuteResponseDto(workService.workCommute(isWorking, commuteRequestDto.getAccessToken()));
    }

    /*2.근무 시프트 작성*/
    @PostMapping("/work/create/schedule/")
    public ResponseEntity createSchedule(@RequestBody ScheduleCreateRequestDto scheduleCreateRequestDto) {
        Long memberId = memberKakaoApiService.getMemberInfoFromAccessToken(scheduleCreateRequestDto.getAccessToken());
        Member member = memberRepository.findOne(memberId);
        return workService.createSchedule(member, scheduleCreateRequestDto);
    }

    /*3.근무 시프트 조회*/
    @PostMapping("/work/read/schedule/")
    public ScheduleReadResponseDto readSchedule(@RequestBody ScheduleReadRequestDto scheduleReadRequestDto) {
        Long memberId = memberKakaoApiService.getMemberInfoFromAccessToken(scheduleReadRequestDto.getAccessToken());
        Member member = memberRepository.findOne(memberId);
        LocalDate day = scheduleReadRequestDto.getWorkDay();
        return workService.readSchedule(member, day);
    }

    /*4.근무 시프트 삭제*/
    @PostMapping("/work/delete/schedule/")
    public ResponseEntity deleteSchedule(@RequestBody Map<String, String> param) {
        Long memberId = memberKakaoApiService.getMemberInfoFromAccessToken(param.get("accessToken"));
        Member member = memberRepository.findOne(memberId);

        Long postId = Long.parseLong(param.get("id"));

        return workService.deleteSchedule(member, postId);
    }

    /*5.근무 시프트 수정*/
    @PostMapping("/work/update/schedule/")
    public ResponseEntity updateSchedule(@RequestBody ScheduleUpdateRequestDto scheduleUpdateRequestDto) {
        Long memberId = memberKakaoApiService.getMemberInfoFromAccessToken(scheduleUpdateRequestDto.getAccessToken());
        Member member = memberRepository.findOne(memberId);
        return workService.updateSchedule(member, scheduleUpdateRequestDto);
    }

    /*6.급여 계산기(사장)*/
    @PostMapping("/work/salary/president/")
    public SalaryCalPresidentResponseDto calculateSalaryForPresident(@RequestBody SalaryCalRequestDTO salaryCalRequestDTO) {
        Long memberId = memberKakaoApiService.getMemberInfoFromAccessToken(salaryCalRequestDTO.getAccessToken());
        Member member = memberRepository.findOne(memberId);

        String isMonthOrWeek = salaryCalRequestDTO.getIsMonthOrWeek();
        LocalDate requestTime = salaryCalRequestDTO.getRequestTime();

        LocalDate firstDate = requestTime.withDayOfMonth(1); //1일
        LocalDate lastDate = requestTime.withDayOfMonth(requestTime.lengthOfMonth()); //말일
        LocalTime zeroTime = LocalTime.of(0, 0, 0); //00시 00분 00초

        LocalDateTime firstDateTime;
        LocalDateTime lastDateTime;

        if (isMonthOrWeek.equals("Month")) {
            firstDateTime = LocalDateTime.of(firstDate, zeroTime); //1일
            lastDateTime = LocalDateTime.of(lastDate, zeroTime).plusDays(1); //다음달 1일 00시까지 출근한 것 포함.
            return workService.calculateSalaryForPresident(member, firstDateTime, lastDateTime);
        } else if (isMonthOrWeek.equals("Week")) {
            Integer countWeek = salaryCalRequestDTO.getCountWeek() - 1;
            firstDateTime = LocalDateTime.of(firstDate, zeroTime).plusWeeks(countWeek);
            lastDateTime = firstDateTime.plusWeeks(1).plusDays(1); //7일차까지 포함하기 위해서
            return workService.calculateSalaryForPresident(member, firstDateTime, lastDateTime);
        }
        return new SalaryCalPresidentResponseDto("Failed");
    }

    /*7.급여 계산기(직원)*/
    @PostMapping("/work/salary/employee/")
    public SalaryCalEmployeeResponseDto calculateSalaryForEmployee(@RequestBody SalaryCalRequestDTO salaryCalRequestDTO) {
        Member member = memberRepository.findByRefreshToken(salaryCalRequestDTO.getAccessToken());

        String isMonthOrWeek = salaryCalRequestDTO.getIsMonthOrWeek();
        LocalDate requestTime = salaryCalRequestDTO.getRequestTime();

        LocalDate firstDate = requestTime.withDayOfMonth(1); //1일
        LocalDate lastDate = requestTime.withDayOfMonth(requestTime.lengthOfMonth()); //말일
        LocalTime zeroTime = LocalTime.of(0, 0, 0); //00시 00분 00초

        LocalDateTime firstDateTime;
        LocalDateTime lastDateTime;

        if (isMonthOrWeek.equals("Month")) {
            firstDateTime = LocalDateTime.of(firstDate, zeroTime); //1일
            lastDateTime = LocalDateTime.of(lastDate, zeroTime).plusDays(1); //다음달 1일 00시까지 출근한 것 포함.
            return workService.calculateSalaryForEmployee(member, firstDateTime, lastDateTime);
        } else if (isMonthOrWeek.equals("Week")) {
            Integer countWeek = salaryCalRequestDTO.getCountWeek() - 1;
            firstDateTime = LocalDateTime.of(firstDate, zeroTime).plusWeeks(countWeek);
            lastDateTime = firstDateTime.plusWeeks(1).plusDays(1); //7일차까지 포함하기 위해서
            return workService.calculateSalaryForEmployee(member, firstDateTime, lastDateTime);
        }
        return new SalaryCalEmployeeResponseDto("Failed");
    }

    /*---------Inner DTO------------*/

    /*출퇴근 Request DTO*/
    @Data
    static class CommuteRequestDto {
        private String workType;
        private String accessToken;
    }
    /*출퇴근 Response DTO*/
    @Data
    static class CommuteResponseDto {
        private String message;

        public CommuteResponseDto(String message){
            this.message = message;
        }
    }

    /*근무 시프트 Request DTO*/
    @Data
    static public class ScheduleReadRequestDto {
        private String accessToken;

        @JsonSerialize(using= LocalDateSerializer.class)
        @JsonDeserialize(using= LocalDateDeserializer.class)
        private LocalDate workDay;
    }
}
