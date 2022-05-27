package com.beitool.beitool.api.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 직원의 급여 계산기를 위한 Response DTO
 * @author Chanos
 * @since 2022-05-27
 */
@Data
public class SalaryCalEmployeeResponseDto {
    public SalaryCalEmployeeResponseDto() { this.workingHistories = new ArrayList<>(); }

    //Constructor
    public void setInfo(Integer totalSalary, Integer workingHour, Integer workingMin, Integer salaryHour) {
        this.totalSalary = totalSalary;
        this.workingHour = workingHour;
        this.workingMin = workingMin;
        this.salaryHour = salaryHour;
    }
    
    //근무 기록 추가
    public void addWorkingHistory(WorkingHistory workingHistory) {
        this.workingHistories.add(workingHistory);
    }

    private Integer totalSalary; //급여 합계

    private Integer workingHour; //근로 시간(시간) 합계
    private Integer workingMin;  //근로 시간(분) 합계

    private Integer salaryHour; //시급
    
    private List<WorkingHistory> workingHistories; //근로 기록

    @Data @AllArgsConstructor
    public static class WorkingHistory {
        @JsonDeserialize(using= LocalDateTimeDeserializer.class)
        @JsonSerialize(using= LocalDateTimeSerializer.class)
        private LocalDateTime workStartTime;
        
        @JsonDeserialize(using= LocalDateTimeDeserializer.class)
        @JsonSerialize(using= LocalDateTimeSerializer.class)
        private LocalDateTime workEndTime;
    }

}
