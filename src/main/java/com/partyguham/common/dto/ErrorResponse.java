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
    private String error;
    private int statusCode;
    private String path;

    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            timezone = "UTC")
    private Instant timestamp;
    
    public static ErrorResponse of(String message, String error, int statusCode, String path) {
        return ErrorResponse.builder()
                .message(message)
                .error(error)
                .statusCode(statusCode)
                .path(path)
                .timestamp(Instant.now())
                .build();
    }
}