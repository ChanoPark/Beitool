package com.beitool.beitool.api.controller;

import com.beitool.beitool.api.dto.*;
import com.beitool.beitool.api.repository.MemberRepository;
import com.beitool.beitool.api.service.MemberKakaoApiService;
import com.beitool.beitool.api.service.WorkService;
import com.beitool.beitool.domain.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 회원 근로와 관련된 컨트롤러
 * 1.출퇴근
 * 2.캘린더 일정 작성
 * 3.캘린더 한달 일정 조회
 * 4.캘린더 하루 일정 조회
 * 5.캘린더 일정 삭제
 * 6.캘린더 일정 수정
 * 7.급여 계산기(사장)
 * 8.급여 계산기(직원)
 *
 * @since 2022-04-18
 * @author Chanos
 */

@Api(tags="근로")
@Slf4j
@RequiredArgsConstructor
@RestController
public class WorkApiController {
    private final MemberKakaoApiService memberKakaoApiService;
    private final MemberRepository memberRepository;
    private final WorkService workService;
    private final HttpServletRequest request;

    /*1.출퇴근(프론트에서 결과만 받음)*/
    @Operation(summary = "출/퇴근")
    @PostMapping("/work/commute/")
    public CommuteResponseDto workCommute(@RequestBody CommuteRequestDto commuteRequestDto) {
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        String isWorking = commuteRequestDto.getWorkType(); //출근인지 퇴근인지
        return new CommuteResponseDto(workService.workCommute(isWorking, accessToken));
    }

    /*2.캘린더 일정 작성*/
    @Operation(summary = "캘린더에 근무 일정 작성")
    @PostMapping("/work/create/schedule/")
    public ResponseEntity createSchedule(@RequestBody ScheduleCreateRequestDto scheduleCreateRequestDto) {
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        Long memberId = memberKakaoApiService.getMemberInfoFromAccessToken(accessToken);
        Member member = memberRepository.findOne(memberId);
        return workService.createSchedule(member, scheduleCreateRequestDto);
    }

    /*3.캘린더 한달 일정 조회*/
    @Operation(summary = "캘린더 한달 근무 일정 조회", description = "이미 근무한 기록과 근무 예정 기록을 날짜에 따라 반환")
    @PostMapping("/work/read/schedule/monthly/")
    public ScheduleReadResponseDto readScheduleMonthly(@RequestBody ScheduleReadRequestDto scheduleReadRequestDto) {
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        Long memberId = memberKakaoApiService.getMemberInfoFromAccessToken(accessToken);
        Member member = memberRepository.findOne(memberId);
        LocalDate requestTime = scheduleReadRequestDto.getWorkDay();

        LocalDate firstDate = requestTime.withDayOfMonth(1); //1일
        LocalDate lastDate = requestTime.withDayOfMonth(requestTime.lengthOfMonth()); //말일
        LocalTime zeroTime = LocalTime.of(0, 0, 0); //00시 00분 00초

        LocalDateTime firstDateTime = LocalDateTime.of(firstDate, zeroTime); //1일
        LocalDateTime lastDateTime = LocalDateTime.of(lastDate, zeroTime).plusDays(1); //다음달 1일 00시까지 출근한 것 포함.

        return workService.readScheduleMonthly(member.getActiveStore(), firstDateTime, lastDateTime);
    }

    /*4.캘린더 하루 일정 조회*/
    @Operation(summary = "캘린더 하루 일정 조회", description = "날짜에 따라 근무 기록 or 근무 예정 기록을 반환")
    @PostMapping("/work/read/schedule/")
    public ScheduleReadResponseDto readSchedule(@RequestBody ScheduleReadRequestDto scheduleReadRequestDto) {
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        Long memberId = memberKakaoApiService.getMemberInfoFromAccessToken(accessToken);
        Member member = memberRepository.findOne(memberId);
        LocalDate day = scheduleReadRequestDto.getWorkDay();
        return workService.readSchedule(member, day);
    }

    /*5.캘린더 일정 삭제*/
    @Operation(summary = "캘린더 근무 예정 기록 삭제", description = "캘린더의 근무 예정 기록을 삭제, 근무 기록은 삭제X")
    @PostMapping("/work/delete/schedule/")
    public ResponseEntity deleteSchedule(@RequestParam("id") Long id) { //PostId
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        Long memberId = memberKakaoApiService.getMemberInfoFromAccessToken(accessToken);
        Member member = memberRepository.findOne(memberId);

        return workService.deleteSchedule(member, id);
    }

    /*6.캘린더 일정 수정*/
    @Operation(summary = "캘린더 근무 예정 기록 수정")
    @PostMapping("/work/update/schedule/")
    public ResponseEntity updateSchedule(@RequestBody ScheduleUpdateRequestDto scheduleUpdateRequestDto) {
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        Long memberId = memberKakaoApiService.getMemberInfoFromAccessToken(accessToken);
        Member member = memberRepository.findOne(memberId);
        return workService.updateSchedule(member, scheduleUpdateRequestDto);
    }

    /*7.급여 계산기(사장)*/
    @Operation(summary = "급여 계산기 조회 - 사장")
    @PostMapping("/work/salary/president/")
    public SalaryCalPresidentResponseDto calculateSalaryForPresident(@RequestBody SalaryCalRequestDTO salaryCalRequestDTO) {
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        Long memberId = memberKakaoApiService.getMemberInfoFromAccessToken(accessToken);
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
        }
        else if (isMonthOrWeek.equals("Week")) {
            Integer countWeek = salaryCalRequestDTO.getCountWeek() - 1;
            firstDateTime = LocalDateTime.of(firstDate, zeroTime).plusWeeks(countWeek);
            lastDateTime = firstDateTime.plusWeeks(1).plusDays(1); //7일차까지 포함하기 위해서
            return workService.calculateSalaryForPresident(member, firstDateTime, lastDateTime);
        }

        log.warn("**급여 계산기(사장) 조회 실패 - 유효하지 않은 isMonthOrWeek 값");
        return new SalaryCalPresidentResponseDto("Failed");
    }

    /*8.급여 계산기(직원)*/
    @Operation(summary = "급여 계산기 조회 - 직원")
    @PostMapping("/work/salary/employee/")
    public SalaryCalEmployeeResponseDto calculateSalaryForEmployee(@RequestBody SalaryCalRequestDTO salaryCalRequestDTO) {
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        Member member = memberRepository.findByRefreshToken(accessToken);

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
        }
        else if (isMonthOrWeek.equals("Week")) {
            Integer countWeek = salaryCalRequestDTO.getCountWeek() - 1;
            firstDateTime = LocalDateTime.of(firstDate, zeroTime).plusWeeks(countWeek);
            lastDateTime = firstDateTime.plusWeeks(1).plusDays(1); //7일차까지 포함하기 위해서
            return workService.calculateSalaryForEmployee(member, firstDateTime, lastDateTime);
        }
        log.warn("**급여계산기(직원) 조회 실패 - 유효하지 않은 isMonthOrWeek 값");
        return new SalaryCalEmployeeResponseDto("Failed");
    }

    /*---------Inner DTO------------*/

    /*출퇴근 Request DTO*/
    @Data
    static class CommuteRequestDto {
        private String workType;
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

        @JsonSerialize(using= LocalDateSerializer.class)
        @JsonDeserialize(using= LocalDateDeserializer.class)
        private LocalDate workDay;
    }

    @Data
    static public class ReadScheduleMonthlyDto {

        @JsonSerialize(using= LocalDateSerializer.class)
        @JsonDeserialize(using= LocalDateDeserializer.class)
        LocalDate goalDay;
    }
}
