package com.beitool.beitool.api.dto.store;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/*가입 대기 직원 목록 조회를 위한 Response DTO*/
@Data
public class GetWaitEmployeeResponse {

    @ApiModelProperty(value="가입 대기중인 회원 목록", example="대기중인 회원의 ID, 이름, 신청 날짜 포함됨.")
    private List<WaitEmployeeInfo> waitEmployeeInfoList;

    public GetWaitEmployeeResponse() {
        this.waitEmployeeInfoList = new ArrayList<>();
    }

    public void addWaitEmployee(Long employeeId, String employeeName, LocalDate belongDate) {
        this.waitEmployeeInfoList.add(new WaitEmployeeInfo(employeeId, employeeName, belongDate));
    }


    @Data @AllArgsConstructor
    static class WaitEmployeeInfo {
        private Long employeeId;
        private String employeeName;

        @JsonDeserialize(using= LocalDateDeserializer.class)
        @JsonSerialize(using= LocalDateSerializer.class)
        @JsonFormat(pattern="yyyy-MM-dd")
        private LocalDate belongDate;
    }
}