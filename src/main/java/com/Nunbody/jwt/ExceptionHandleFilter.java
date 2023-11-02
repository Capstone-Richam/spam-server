package com.Nunbody.jwt;

import com.Nunbody.domain.ErrorCode;
import com.Nunbody.domain.ErrorResponse;
import com.Nunbody.jwt.exception.EmptyTokenException;
import com.Nunbody.jwt.exception.InvalidTokenException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

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
            setErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ErrorCode.INTERNAL_SERVER_ERROR,
                    request, response, exception.getMessage(), "DEFAULT-ERROR-01"
            );
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
