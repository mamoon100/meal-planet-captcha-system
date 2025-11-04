package com.mealplanet.captcha.exception;

public abstract class CaptchaSystemException extends RuntimeException {
    public CaptchaSystemException(String message) {
        super(message);
    }
}
