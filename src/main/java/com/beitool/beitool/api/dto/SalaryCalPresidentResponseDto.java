package com.beitool.beitool.api.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 급여 계산기(사장)의 Response DTO
 * @ahthor Chanos
 * @since 2022-05-27
 */
@Data
public class SalaryCalPresidentResponseDto {

    public SalaryCalPresidentResponseDto() {
        this.salaryInfos = new ArrayList<>();
        this.message = "Success";
    }
    public SalaryCalPresidentResponseDto(String message) { this.message = message; }

    /*정보 추가*/
    public void addInfo(SalaryInfo salaryInfo) {
        this.salaryInfos.add(salaryInfo);
    }
    /*총계 추가*/
    public void setTotalInfo(Integer totalSalary, Integer totalWorkingHour, Integer totalWorkingMin,
                             Integer totalHolidayPay, Integer employeeNum, Integer totalInsurance) {
        this.totalSalary = totalSalary;
        this.totalWorkingHour = totalWorkingHour;
        this.totalWorkingMin = totalWorkingMin;
        this.totalHolidayPay = totalHolidayPay;
        this.employeeNum = employeeNum;
        this.totalInsurance = totalInsurance;
    }

    @ApiModelProperty(value="결과 메시지", example="Success & Fail")
    String message;

    @ApiModelProperty(value="고용 인원", example="15")
    Integer employeeNum;

    @ApiModelProperty(value="급여 합계", example="1364125")
    Integer totalSalary;

    @ApiModelProperty(value="모든 직원의 근로 시간(시간)", example="134")
    Integer totalWorkingHour;

    @ApiModelProperty(value="모든 직원의 근로 시간(분)", example="42")
    Integer totalWorkingMin;

    @ApiModelProperty(value="주휴수당 합계", example="173523")
    Integer totalHolidayPay;

    @ApiModelProperty(value="4대 보험 합계", example="946117")
    Integer totalInsurance;

    @ApiModelProperty(value="급여 정보", example="직원 이름, 급여, 일한 시간, 분, 주휴수당 포함됨.")
    List<SalaryInfo> salaryInfos;

    
    @Data
    public static class SalaryInfo {
        public SalaryInfo(String name, Integer salary, Integer workingHour, Integer workingMin, Integer holidayPay) {
            this.name=name;
            this.salary=salary;
            this.workingHour=workingHour;
            this.workingMin=workingMin;
            this.holidayPay=holidayPay;
        }
        private String name;
        private Integer salary;
        private Integer workingHour;
        private Integer workingMin;
        private Integer holidayPay;
    }
}
