package com.beitool.beitool.api.dto.store;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 사업장에 소속된 직원 목록을 반환하기 위한 Response DTO
 * ToDoList의 업무를 변경할 때 사용하기 위함이므로, 필요한 정보인 이름, 회원번호만 반환하도록 한다.
 * @author Chanos
 * @since 2022-05-06
 */
@Data
public class BelongEmployeeListResponseDto {
    public BelongEmployeeListResponseDto() {
        this.employees = new ArrayList<>();
    }
    public void setEmployee(Long employeeId, String employeeName) {
        EmployeeInfo employeeInfo = new EmployeeInfo(employeeId, employeeName);
        employees.add(employeeInfo);
    }

    @ApiModelProperty(value="회원 정보", example="회원 번호, 회원 이름 포함됨.")
    private List<EmployeeInfo> employees;

    @ApiModelProperty(value="결과 메시지", example="Success & Fail")
    private String message;

    @Data
    public static class EmployeeInfo {
        public EmployeeInfo(Long employeeId, String employeeName) {
            this.employeeId = employeeId;
            this.employeeName = employeeName;
        }
        private Long employeeId;
        private String employeeName;
    }
}
