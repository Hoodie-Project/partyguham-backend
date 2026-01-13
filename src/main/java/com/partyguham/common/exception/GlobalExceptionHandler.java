package com.partyguham.common.exception;

import com.partyguham.common.dto.ErrorResponse;
import com.partyguham.common.error.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** enum 오류 */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException e, HttpServletRequest request) {

        log.warn("Json Parse Error : {}", e.getMessage());

        String errorMessage = "요청 데이터 형식이 잘못되었습니다.";
        List<ErrorResponse.FieldErrorDetail> errors = null;

        // 원인이 JSON 변환 실패(InvalidFormatException)인 경우 상세 정보 추출
        if (e.getCause() instanceof com.fasterxml.jackson.databind.exc.InvalidFormatException ife) {
            String fieldName = ife.getPath().get(0).getFieldName(); // 에러가 발생한 필드명 (ex: gender)
            String targetType = ife.getTargetType().getSimpleName(); // 목표 타입 (ex: Gender)
            String value = ife.getValue().toString(); // 사용자가 보낸 잘못된 값

            // Enum인 경우 허용되는 값들을 목록으로 만들어줌
            if (ife.getTargetType().isEnum()) {
                Object[] enums = ife.getTargetType().getEnumConstants();
                errorMessage = String.format("잘못된 입력값입니다. 필드: '%s', 허용되는 값: %s", fieldName, java.util.Arrays.toString(enums));

                errors = List.of(new ErrorResponse.FieldErrorDetail(
                        fieldName,
                        value,
                        targetType + " 타입으로 변환할 수 없습니다."
                ));
            }
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(errorMessage, "INVALID_JSON_FORMAT", 400, request.getRequestURI(), errors));
    }

    /**
     * @Valid 검증 실패 시 발생하는 예외 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e, HttpServletRequest request) {

        // 1. 필드 에러(FieldError) 추출 (@Pattern, @URL 등)
        List<ErrorResponse.FieldErrorDetail> fieldErrors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ErrorResponse.FieldErrorDetail(
                        error.getField(),
                        error.getRejectedValue() == null ? "" : error.getRejectedValue().toString(),
                        error.getDefaultMessage()))
                .collect(Collectors.toList());

        // 2. 글로벌 에러(ObjectError) 추가 (@AssertTrue 등)
        List<ErrorResponse.FieldErrorDetail> globalErrors = e.getBindingResult()
                .getGlobalErrors()
                .stream()
                .map(error -> new ErrorResponse.FieldErrorDetail(
                        error.getObjectName(),
                        "N/A",
                        error.getDefaultMessage()))
                .collect(Collectors.toList());

        // 두 에러 리스트 합치기
        fieldErrors.addAll(globalErrors);

        ErrorResponse response = ErrorResponse.of(
                "입력값이 유효하지 않습니다.",
                "INVALID_INPUT_VALUE",
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI(),
                fieldErrors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

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

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ErrorResponse.of(
                        errorCode.getMessage(),
                        errorCode.getCode(),
                        errorCode.getStatus(),
                        request.getRequestURI()
                ));
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