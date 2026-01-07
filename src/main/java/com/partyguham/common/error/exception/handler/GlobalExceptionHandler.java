package com.partyguham.common.error.exception.handler;

import com.partyguham.common.dto.ErrorResponse;
import com.partyguham.common.error.exception.BusinessException;
import com.partyguham.common.error.ErrorCode;
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
        ErrorCode errorCode = e.getErrorCode();
        // ErrorResponse 생성 (정적 팩토리 메서드 활용)
        ErrorResponse errorResponse = ErrorResponse.of(
                errorCode.getMessage(),
                errorCode.getCode(),
                errorCode.getStatus(),
                request.getRequestURI()
        );

        // 3. 빌더 패턴으로 가독성 있게 반환
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(errorResponse);
    }

    /** 예상 못한 나머지 예외 → 500 */
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ErrorResponse> handleException(
//            Exception e,
//            HttpServletRequest request
//    ) {
//        ErrorResponse errorResponse = ErrorResponse.of(
//                "서버 오류가 발생했습니다.",
//                "INTERNAL_SERVER_ERROR",
//                500,
//                request.getRequestURI()
//        );
//        return ResponseEntity.status(500).body(errorResponse);
//    }
}

