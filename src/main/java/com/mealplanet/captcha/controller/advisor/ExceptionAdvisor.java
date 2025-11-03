package com.mealplanet.captcha.controller.advisor;


import com.mealplanet.captcha.exception.CaptchaSystemException;
import com.mealplanet.captcha.model.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Arrays;

@Slf4j
@ControllerAdvice
public class ExceptionAdvisor {

    public static final ErrorResponse ERROR_RESPONSE = new ErrorResponse("General error occurred, Please contact support");

    @ExceptionHandler(CaptchaSystemException.class)
    public ResponseEntity<ErrorResponse> handleMyCustomException(CaptchaSystemException ex) {
        log.error(ex.getMessage(), ex);
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleException(MissingServletRequestParameterException ex) {
        log.error(ex.getMessage(), ex);
        return new ResponseEntity<>(new ErrorResponse(String.format("Missing required parameter: %s", ex.getParameterName())), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleException(MethodArgumentTypeMismatchException ex) {
        log.error(ex.getMessage(), ex);
        String message;
        if ("type".equals(ex.getName()) && ex.getRequiredType() != null && ex.getRequiredType().isEnum()) {
            Object[] allowed = ex.getRequiredType().getEnumConstants();
            message = "Invalid value for parameter: type. Allowed values are: " + Arrays.toString(allowed);
        } else {
            message = "Invalid parameter value: " + ex.getName();
        }
        return new ResponseEntity<>(new ErrorResponse(message), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorResponse> handleException(Throwable ex) {
        log.error(ex.getMessage(), ex);
        return new ResponseEntity<>(ERROR_RESPONSE, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
