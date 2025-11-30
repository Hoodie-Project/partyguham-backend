package com.partyguham.common.exception;

import com.partyguham.common.dto.ErrorResponse;
import com.partyguham.party.exception.PartyUserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PartyUserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePartyUserNotFoundException(
            PartyUserNotFoundException e,
            HttpServletRequest request) {
        
        ErrorResponse errorResponse = ErrorResponse.of(
                e.getMessage(),
                "PARTY_USER_NOT_EXIST",
                HttpStatus.NOT_FOUND.value(),
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
}

