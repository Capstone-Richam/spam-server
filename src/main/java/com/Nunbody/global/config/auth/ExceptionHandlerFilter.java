package com.Nunbody.global.config.auth;

import com.Nunbody.global.error.ErrorCode;
import com.Nunbody.global.error.ErrorResponse;
import com.Nunbody.global.error.dto.ErrorBaseResponse;
import com.Nunbody.global.error.exception.InvalidValueException;
import com.Nunbody.global.error.exception.UnauthorizedException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.Nunbody.global.error.ErrorCode.INTERNAL_SERVER_ERROR;

@Slf4j
public class ExceptionHandlerFilter extends OncePerRequestFilter {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (UnauthorizedException e) {
            handleUnauthorizedException(response, e);
        } catch (Exception exception) {
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
            } else if (errorMessage.contains("이미 존재하는 아이디입니다")) {
                setErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        ErrorCode.ACCOUNT_EXISTS_ERROR,
                        request, response, "아이디 중복.", "DEFAULT-ERROR-01"
                );
            }else if (errorMessage.contains("이미 존재하는 닉네임입니다")) {
                setErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        ErrorCode.NAME_EXISTS_ERROR,
                        request, response, "닉네임 중복.", "DEFAULT-ERROR-01"
                );
            }else if(errorMessage.contains("잘못")){setErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ErrorCode.NAME_ERROR,
                    request, response, "잘못.", "DEFAULT-ERROR-01"
            );}
            else{
            handleException(response);}
        }
    }

    private void handleUnauthorizedException(HttpServletResponse response, Exception e) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");

        if (UnauthorizedException.class.isAssignableFrom(e.getClass())) {
            UnauthorizedException ue = (UnauthorizedException) e;
            response.setStatus(ue.getErrorCode().getHttpStatus().value());
            response.getWriter().write(objectMapper.writeValueAsString(ErrorBaseResponse.of(ue.getErrorCode())));
        } else if (InvalidValueException.class.isAssignableFrom(e.getClass())) {
            InvalidValueException ie = (InvalidValueException) e;
            response.setStatus(ie.getErrorCode().getHttpStatus().value());
            response.getWriter().write(objectMapper.writeValueAsString(ErrorBaseResponse.of(ie.getErrorCode())));
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

    private void handleException(HttpServletResponse response) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");
        response.setStatus(INTERNAL_SERVER_ERROR.getHttpStatus().value());
        response.getWriter().write(objectMapper.writeValueAsString(ErrorBaseResponse.of(INTERNAL_SERVER_ERROR)));
    }
}

