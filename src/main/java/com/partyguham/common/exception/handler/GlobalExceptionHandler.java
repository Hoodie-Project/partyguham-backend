package com.partyguham.common.exception.handler;

import com.partyguham.common.dto.ErrorResponse;
import com.partyguham.common.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException e,
            HttpServletRequest request
    ) {
        ErrorResponse errorResponse = ErrorResponse.of(
                e.getMessage(),
                e.getCode(),
                e.getHttpStatus().value(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(e.getHttpStatus())
                .body(errorResponse);
    }

    /** 예상 못한 나머지 예외 → 500 */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            Exception e,
            HttpServletRequest request
    ) {
        ErrorResponse errorResponse = ErrorResponse.of(
                "서버 오류가 발생했습니다.",
                "INTERNAL_SERVER_ERROR",
                500,
                request.getRequestURI()
        );
        return ResponseEntity.status(500).body(errorResponse);
    }
}

