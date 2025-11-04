package com.mealplanet.captcha.exception;

public class GeneralErrorException extends CaptchaSystemException {
    public GeneralErrorException() {
        super("General error occurred, please try again later");
    }
}
