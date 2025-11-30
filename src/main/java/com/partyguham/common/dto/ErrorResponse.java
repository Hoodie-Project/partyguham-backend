package com.partyguham.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private String message;
    private String error;
    private int statusCode;
    private String path;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant timestamp;

    public static ErrorResponse of(String message, String error, int statusCode, String path) {
        return new ErrorResponse(message, error, statusCode, path, Instant.now());
    }
}

