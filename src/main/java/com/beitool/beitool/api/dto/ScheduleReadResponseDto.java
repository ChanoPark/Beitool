package com.beitool.beitool.api.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 캘린더 조회를 위한 Response DTO
 * @author Chanos
 * @since 2022-05-25
 */
@Data
public class ScheduleReadResponseDto {

    public ScheduleReadResponseDto() {
        workInfos = new ArrayList<>();
    }

    public void setWorkInfos(WorkInfoResponse workInfoResponse) {
        this.workInfos.add(workInfoResponse);
    }

    @ApiModelProperty(value="근로 일정 정보", example="직원 번호, 이름, 출근/퇴근 시간, 근로 날짜")
    List<WorkInfoResponse> workInfos;

    @Data
    public static class WorkInfoResponse {
        public WorkInfoResponse(Long id, String employeeName, LocalDateTime workStartTime, LocalDateTime workEndTime, LocalDate workDay) {
            this.id = id;
            this.employeeName = employeeName;
            this.workStartTime = workStartTime;
            this.workEndTime = workEndTime;
            this.workDay = workDay;
        }

        public WorkInfoResponse(Long id, String employeeName, String authorName,
                                LocalDateTime workStartTime, LocalDateTime workEndTime, LocalDate workDay) {
            this.id = id;
            this.employeeName = employeeName;
            this.authorName = authorName;
            this.workStartTime = workStartTime;
            this.workEndTime = workEndTime;
            this.workDay = workDay;
        }

        private Long id; //게시글 id
        private String employeeName;
        private String authorName;

        @JsonDeserialize(using= LocalDateDeserializer.class)
        @JsonSerialize(using= LocalDateSerializer.class)
        private LocalDate workDay;

        @JsonDeserialize(using= LocalDateTimeDeserializer.class)
        @JsonSerialize(using= LocalDateTimeSerializer.class)
        private LocalDateTime workStartTime;

        @JsonDeserialize(using= LocalDateTimeDeserializer.class)
        @JsonSerialize(using= LocalDateTimeSerializer.class)
        private LocalDateTime workEndTime;
    }
}
