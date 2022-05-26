package com.beitool.beitool.api.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;

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

    List<WorkInfoResponse> workInfos;

    @Data
    public static class WorkInfoResponse {
        public WorkInfoResponse(Long id, String employeeName, LocalDateTime workStartTime, LocalDateTime workEndTime) {
            this.id = id;
            this.employeeName = employeeName;
            this.workStartTime = workStartTime;
            this.workEndTime= workEndTime;
        }

        public WorkInfoResponse(Long id, String employeeName, String authorName, LocalDateTime workStartTime, LocalDateTime workEndTime) {
            this.id = id;
            this.employeeName = employeeName;
            this.authorName = authorName;
            this.workStartTime = workStartTime;
            this.workEndTime= workEndTime;
        }

        private Long id; //게시글 id
        private String employeeName;
        private String authorName;

        @JsonDeserialize(using= LocalDateTimeDeserializer.class)
        @JsonSerialize(using= LocalDateTimeSerializer.class)
        private LocalDateTime workStartTime;

        @JsonDeserialize(using= LocalDateTimeDeserializer.class)
        @JsonSerialize(using= LocalDateTimeSerializer.class)
        private LocalDateTime workEndTime;
    }
}
