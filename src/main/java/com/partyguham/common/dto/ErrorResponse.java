package com.partyguham.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
@JsonPropertyOrder({"message", "code", "status", "path", "timestamp", "errors"})
public class ErrorResponse {

    private String message;
    private String code;
    private int status;
    private String path;
    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            timezone = "UTC")
    private Instant timestamp;

    // 상세 에러 리스트 추가 (null일 경우 JSON에서 제외)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<FieldErrorDetail> errors;
    
    public static ErrorResponse of(String message, String code, int status, String path) {
        return ErrorResponse.builder()
                .message(message)
                .code(code)
                .status(status)
                .path(path)
                .timestamp(Instant.now())
                .build();
    }

    // 새로운 정적 팩토리 메서드 (검증 예외용)
    public static ErrorResponse of(String message, String code, int status, String path, List<FieldErrorDetail> errors) {
        return ErrorResponse.builder()
                .message(message)
                .code(code)
                .status(status)
                .path(path)
                .timestamp(Instant.now())
                .errors(errors)
                .build();
    }

    // 상세 에러 정보를 담을 내부 클래스
    @Getter
    @AllArgsConstructor
    public static class FieldErrorDetail {
        private String field;
        private String value;
        private String reason;
    }

}