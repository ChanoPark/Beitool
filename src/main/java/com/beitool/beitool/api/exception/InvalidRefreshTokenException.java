package com.beitool.beitool.api.exception;

/**
 * 2022-04-03 리프레시토큰으로 회원을 조회할 때, 조회가 안되는 경우
 * 리프레시 토큰이 잘못 된 경우 -> 설계상 불가능한 상황이라고 생각하지만, 혹시나를 대비해 예외처리
 */
public class InvalidRefreshTokenException extends RuntimeException {
    public InvalidRefreshTokenException(String message) {
            super(message);
    }
}
