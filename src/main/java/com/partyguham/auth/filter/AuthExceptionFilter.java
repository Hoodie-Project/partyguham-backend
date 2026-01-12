package com.partyguham.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.partyguham.common.dto.ErrorResponse;
import com.partyguham.common.error.CommonErrorCode;
import com.partyguham.common.error.ErrorCode;
import com.partyguham.common.exception.BusinessException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor // ObjectMapper 주입을 위해 필요
public class AuthExceptionFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper; // JSON 변환기

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        try {
            chain.doFilter(request, response);
        } catch (BusinessException e) {
            // 1.  정의한 Exception
            sendErrorResponse(request, response, e.getErrorCode());
        } catch (Exception e) {
            // 2. 그 외 예상치 못한 시스템 에러가 터졌을 때 처리
            log.error("AuthExceptionFilter System Error : ", e);
            sendErrorResponse(request, response, CommonErrorCode.UNAUTHORIZED);
        }
    }

    private void sendErrorResponse(HttpServletRequest request, HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setStatus(errorCode.getStatus());
        response.setContentType("application/json;charset=UTF-8");

        ErrorResponse errorResponse = ErrorResponse.of(
                errorCode.getMessage(),
                errorCode.getCode(),
                errorCode.getStatus(),
                request.getRequestURI()
        );

        // 객체를 JSON 문자열로 변환
        String json = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(json);
        response.flushBuffer();
    }
}