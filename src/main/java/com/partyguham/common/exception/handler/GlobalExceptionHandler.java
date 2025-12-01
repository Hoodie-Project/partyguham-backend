package com.partyguham.common.exception.handler;

import com.partyguham.common.dto.ErrorResponse;
import com.partyguham.common.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 모든 비즈니스 예외를 통합 처리
     * - 각 예외가 자신의 code, message, HTTP 상태 코드를 가지고 있음
     * - 모든 도메인의 BusinessException을 상속받은 예외를 처리
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException e,
            HttpServletRequest request) {
        
        ErrorResponse errorResponse = ErrorResponse.of(
                e.getMessage(),
                e.getCode(),
                e.getHttpStatus(),
                request.getRequestURI()
        );
        
        return ResponseEntity.status(e.getHttpStatus()).body(errorResponse);
    }
}

