package com.beitool.beitool.api.exception;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 예외에 대한 내용을 담고 있는 클래스
 * @author Chanos
 * @since 2022-07-05
 */
@Getter @Setter
public class ErrorResponse {
    private Date timestamp;
    private int status;
    private String message;
    private String details;

    public ErrorResponse(ErrorCode errorCode, String details) {
        this.timestamp = new Date();
        this.status = errorCode.getStatus();
        this.message = errorCode.getMessage();
        this.details = details;
    }
}
