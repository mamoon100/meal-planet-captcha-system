package com.mealplanet.captcha.exception;

public class CaptchaValidationException extends RuntimeException {
  public CaptchaValidationException() {
    super("Captcha is not in NEW status");
  }
}
