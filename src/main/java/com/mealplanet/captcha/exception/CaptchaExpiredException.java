package com.mealplanet.captcha.exception;

public class CaptchaExpiredException extends CaptchaSystemException {
    public CaptchaExpiredException() {
        super("Captcha has expired");
    }
}
