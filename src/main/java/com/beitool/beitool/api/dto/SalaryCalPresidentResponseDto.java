package com.beitool.beitool.api.dto;

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

    public SalaryCalPresidentResponseDto() { this.salaryInfos = new ArrayList<>(); }

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

    Integer employeeNum; // 고용 인원
    Integer totalSalary; // 모든 직원의 급여 합계
    Integer totalWorkingHour; //모든 직원의 근로 시간(시간)
    Integer totalWorkingMin;  //모든 직원의 근로 시간(분)
    Integer totalHolidayPay; //주휴수당 합계
    Integer totalInsurance; //4대보험 합계

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
