package com.cookie.global.exception.handler;

import com.cookie.domain.user.dto.response.auth.RegistrationRequiredResponse;
import com.cookie.domain.user.exception.RegistrationRequiredException;
import com.cookie.global.util.ApiUtil;
import com.cookie.global.util.ApiUtil.ApiError;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
                                                             HttpStatusCode statusCode, WebRequest request) {
        log.error(ex.getMessage(), ex);

        ApiError<String> error = ApiUtil.error(statusCode.value(), "알 수 없는 오류가 발생했습니다. 문의 바랍니다.");
        return super.handleExceptionInternal(ex, error, headers, statusCode, request);
    }

    @ExceptionHandler({
            IllegalArgumentException.class,
            NotFoundException.class
    })
    protected ResponseEntity<?> handleIllegalArgumentException(Exception e) {
        log.error(e.getMessage(), e);
        ApiError<String> error = ApiUtil.error(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND).body(error);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(RegistrationRequiredException.class)
    public ResponseEntity<?> handleRegistrationRequiredException(RegistrationRequiredException ex) {
        RegistrationRequiredResponse response = new RegistrationRequiredResponse(
                ex.getUser().getSocialProvider(),
                ex.getUser().getSocialId(),
                ex.getUser().getEmail()
        );

        return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT)
                .header("Location", "/api/auth/register")
                .body(ApiUtil.success(response));
    }
}