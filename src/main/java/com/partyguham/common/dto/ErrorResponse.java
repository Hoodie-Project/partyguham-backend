package com.partyguham.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
@Builder
@JsonPropertyOrder({"message", "error", "statusCode", "path", "timestamp"})
public class ErrorResponse {

    private String message;
    private String code;
    private int statusCode;
    private String path;
    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            timezone = "UTC")
    private Instant timestamp;
    
    public static ErrorResponse of(String message, String code, int statusCode, String path) {
        return ErrorResponse.builder()
                .message(message)
                .code(code)
                .statusCode(statusCode)
                .path(path)
                .timestamp(Instant.now())
                .build();
    }

    // Bean Validation(@Valid) 에러 처리용 (내부 정적 클래스)
    @Getter
    @AllArgsConstructor
    public static class FieldError {
        private String field;
        private String value;
        private String reason;
    }
}