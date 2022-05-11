package com.hotnerds.common.exception.handler;

import com.hotnerds.common.exception.BusinessException;
import com.hotnerds.common.exception.ErrorCode;
import com.hotnerds.common.exception.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.naming.AuthenticationException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String LOG_FORMAT = "Exception: {}, ErrorCode: {}, Message: {}";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        log.warn(LOG_FORMAT, e.getClass().getName(), ErrorCode.INVALID_INPUT_VALUE_EXCEPTION.getCode(), ErrorCode.INVALID_INPUT_VALUE_EXCEPTION.getMessage());
        final BindingResult bindingResult = e.getBindingResult();
        final ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE_EXCEPTION, bindingResult);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSuported(HttpRequestMethodNotSupportedException e) {
        log.warn(LOG_FORMAT, e.getClass().getName(), ErrorCode.METHOD_NOT_ALLOWED_EXCEPTION.getCode(), ErrorCode.METHOD_NOT_ALLOWED_EXCEPTION.getMessage());
        final ErrorResponse response = ErrorResponse.of(ErrorCode.METHOD_NOT_ALLOWED_EXCEPTION);
        return new ResponseEntity<>(response, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(AuthenticationException.class)
    protected ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException e) {
        log.warn(LOG_FORMAT, e.getClass().getName(), ErrorCode.AUTHENTICATION_EXCEPTION.getCode(), ErrorCode.AUTHENTICATION_EXCEPTION.getMessage());
        ErrorResponse response =  ErrorResponse.of(ErrorCode.AUTHENTICATION_EXCEPTION, e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();
        log.warn(LOG_FORMAT, e.getClass().getName(), errorCode.getCode(), errorCode.getMessage());
        ErrorResponse response = ErrorResponse.of(errorCode);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

    @ExceptionHandler(IllegalStateException.class)
    protected ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException e) {
        log.warn(LOG_FORMAT, e.getClass().getName(), ErrorCode.INVALID_INPUT_VALUE_EXCEPTION.getCode(), ErrorCode.INVALID_INPUT_VALUE_EXCEPTION.getMessage());
        ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE_EXCEPTION);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BindException.class)
    protected ResponseEntity<ErrorResponse> handleBindException(BindException e) {
        log.warn(LOG_FORMAT, e.getClass().getName(), ErrorCode.INVALID_TYPE_VALUE_EXCEPTION.getCode(), ErrorCode.INVALID_TYPE_VALUE_EXCEPTION.getMessage());
        BindingResult bindingResult = e.getBindingResult();
        ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_TYPE_VALUE_EXCEPTION, bindingResult);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.warn(LOG_FORMAT, e.getClass().getName(), ErrorCode.INTERNAL_SERVER_ERROR_EXCEPTION.getCode(), ErrorCode.INTERNAL_SERVER_ERROR_EXCEPTION.getMessage());
        ErrorResponse response = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR_EXCEPTION);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
