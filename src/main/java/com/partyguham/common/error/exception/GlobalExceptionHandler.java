package com.partyguham.common.error.exception;

import com.partyguham.common.dto.ErrorResponse;
import com.partyguham.common.error.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * [1] 비즈니스 로직 상 발생하는 에러 처리
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException e,
            HttpServletRequest request
    ) {
        ErrorCode errorCode = e.getErrorCode();
        log.warn("Business Exception : {}", errorCode.getMessage()); // 비즈니스 예외는 warn 수준 로그

        ErrorResponse errorResponse = ErrorResponse.of(
                errorCode.getMessage(),
                errorCode.getCode(),
                errorCode.getStatus(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(errorResponse);
    }

    /**
     * [2] 우리가 예측하지 못한 모든 에러 처리 (Internal Server Error)
     */
    @ExceptionHandler(Exception.class) // 최상위 예외인 Exception
    public ResponseEntity<ErrorResponse> handleException(
            Exception e,
            HttpServletRequest request
    ) {
        log.error("Unhandled Exception 발생! : ", e); // 모니터링 대상

        ErrorResponse errorResponse = ErrorResponse.of(
                "서버 내부 오류가 발생했습니다. 관리자에게 문의하세요.",
                "INTERNAL_SERVER_ERROR", // 공통 에러 코드
                500,
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }
}