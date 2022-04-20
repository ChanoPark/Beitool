package com.beitool.beitool.api.controller;

import com.beitool.beitool.api.service.WorkService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 회원 근로와 관련된 컨트롤러
 * 1.출퇴근
 *
 * @since 2022-04-18
 * @author Chanos
 */
@RestController
@RequiredArgsConstructor
public class WorkApiController {
    private final WorkService workService;

    /*출퇴근(프론트에서 결과만 받음)*/
    @PostMapping("/work/commute/") //String workType, String accessToken는 dto로 바꿔받든가 해야될듯 지금 제대로 못받는 상황인거같음.
    public CommuteResponseDto workCommute(@RequestBody CommuteRequestDto commuteRequestDto) {
        String isWorking = commuteRequestDto.getWorkType(); //출근인지 퇴근인지
        return new CommuteResponseDto(workService.workCommute(isWorking, commuteRequestDto.getAccessToken()));
    }

    /*출퇴근 request DTO*/
    @Data
    static class CommuteRequestDto {
        private String workType;
        private String accessToken;
    }
    /*출퇴근 response DTO*/
    @Data @Setter
    static class CommuteResponseDto {
        private String message;

        public CommuteResponseDto(String message){
            this.message = message;
        }
    }
}
