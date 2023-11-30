package com.Nunbody.jwt;

import com.Nunbody.global.error.ErrorCode;
import com.Nunbody.global.error.ErrorResponse;
import com.Nunbody.jwt.exception.EmptyTokenException;
import com.Nunbody.jwt.exception.InvalidTokenException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.mail.AuthenticationFailedException;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class ExceptionHandleFilter extends OncePerRequestFilter {
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        try {
            filterChain.doFilter(request, response);
        } catch (InvalidTokenException exception) {
            setErrorResponse(
                    HttpStatus.BAD_REQUEST,
                    ErrorCode.INVALID_ACCESS_TOKEN_ERROR,
                    request, response, exception.getMessage(), "TOKEN-ERROR-01"
            );

        } catch (EmptyTokenException exception) {
            setErrorResponse(
                    HttpStatus.BAD_REQUEST,
                    ErrorCode.INVALID_ACCESS_TOKEN_ERROR,
                    request, response, exception.getMessage(), "TOKEN-ERROR-02"
            );
        } catch (Exception exception) {
//            if (exception instanceof AuthenticationFailedException) {
                String errorMessage = exception.getMessage();
                if (errorMessage.contains("존재하지 않는 이메일 정보입니다")) {
                    // Handle username or password error
                    // You can log the error message or perform any other necessary actions
                    setErrorResponse(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            ErrorCode.INVALID_EMAIL_ERROR,
                            request, response, "ID 또는 비밀번호가 올바르지 않습니다.", "DEFAULT-ERROR-01"
                    );
                } else if (errorMessage.contains("IMAP 설정을 해주세요")) {
                    // Handle IMAP/SMTP settings error
                    // You can log the error message or perform any other necessary actions
                    setErrorResponse(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            ErrorCode.IMAP_ERROR,
                            request, response, "IMAP/SMTP 설정을 확인하세요.", "DEFAULT-ERROR-01"
                    );
                }
            }
        }


    private void setErrorResponse(HttpStatus status,
                                  ErrorCode code,
                                  HttpServletRequest request,
                                  HttpServletResponse response,
                                  String exceptionMessage,
                                  String errorCode) {
        response.setStatus(status.value());
        response.setContentType("application/json");
        ErrorResponse errorResponse = new ErrorResponse(code, exceptionMessage);
        try {
            log.error("에러코드 "+errorCode+": "+exceptionMessage+", 요청 url: "+request.getRequestURI());
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
