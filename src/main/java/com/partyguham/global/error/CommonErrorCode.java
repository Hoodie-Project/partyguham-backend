package com.partyguham.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommonErrorCode implements ErrorCode {

    // 400: 잘못된 요청
    BAD_REQUEST(400, "COMMON_400", "잘못된 요청입니다."),

    // 401: 인증 실패
    UNAUTHORIZED(401, "COMMON_401", "인증이 필요합니다."),

    // 403: 권한 없음
    FORBIDDEN(403, "COMMON_403", "접근 권한이 없습니다."),

    // 404: 리소스 없음
    NOT_FOUND(404, "COMMON_404", "요청하신 리소스를 찾을 수 없습니다."),

    // 405: 메서드 불허
    METHOD_NOT_ALLOWED(405, "COMMON_405", "허용되지 않는 HTTP 메서드입니다."),

    // 409: 리소스 충돌
    CONFLICT(409, "COMMON_409", "리소스 충돌이 발생했습니다."),
    CONCURRENT_REQUEST_ERROR(409, "COMMON_409_1", "현재 처리 중인 요청이 많습니다. 잠시 후 다시 시도해주세요."),

    // 500: 서버 내부 오류
    INTERNAL_SERVER_ERROR(500, "COMMON_500", "서버 내부 오류가 발생했습니다.");

    private final int status;
    private final String code;
    private final String message;
}