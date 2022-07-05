package com.beitool.beitool.api.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * HTTP Status
 * @author Chanos
 * @since 2022-07-05
 */

@Getter
@AllArgsConstructor
public enum ErrorCode {
    INTERNAL_SERVER_ERROR(500, "INTERNAL SERVER ERROR"),
    RESOURCE_NOT_FOUND(404, "RESOURCE NOT FOUND"),
    UNAUTHORIZED(401, "UNAUTHORIZED"),
    EXPIRED_JWT(401, "EXPIRED_JWT"),
    BAD_REQUEST(400, "BAD REQUEST");

    private int status;
    private String message;
}
