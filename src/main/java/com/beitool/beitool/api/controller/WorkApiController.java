package com.beitool.beitool.api.controller;

import com.beitool.beitool.api.dto.ScheduleCreateRequestDto;
import com.beitool.beitool.api.repository.MemberRepository;
import com.beitool.beitool.api.service.MemberKakaoApiService;
import com.beitool.beitool.api.service.WorkService;
import com.beitool.beitool.domain.Member;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 회원 근로와 관련된 컨트롤러
 * 1.출퇴근
 * 2.근무 시프트 작성
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

    /*출퇴근(프론트에서 결과만 받음)*/
    @PostMapping("/work/commute/")
    public CommuteResponseDto workCommute(@RequestBody CommuteRequestDto commuteRequestDto) {
        String isWorking = commuteRequestDto.getWorkType(); //출근인지 퇴근인지
        return new CommuteResponseDto(workService.workCommute(isWorking, commuteRequestDto.getAccessToken()));
    }

    /*근무 시프트 작성*/
    @PostMapping("/work/create/schedule/")
    public ResponseEntity createSchedule(@RequestBody ScheduleCreateRequestDto scheduleCreateRequestDto) {
        Long memberId = memberKakaoApiService.getMemberInfoFromAccessToken(scheduleCreateRequestDto.getAccessToken());
        Member member = memberRepository.findOne(memberId);
        workService.createSchedule(member, scheduleCreateRequestDto);
        return new ResponseEntity("Success", HttpStatus.OK);
    }

    /*출퇴근 request DTO*/
    @Data
    static class CommuteRequestDto {
        private String workType;
        private String accessToken;
    }
    /*출퇴근 response DTO*/
    @Data
    static class CommuteResponseDto {
        private String message;

        public CommuteResponseDto(String message){
            this.message = message;
        }
    }
}
