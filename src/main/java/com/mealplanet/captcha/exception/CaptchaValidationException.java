package com.mealplanet.captcha.exception;

public class CaptchaValidationException extends CaptchaSystemException {
  public CaptchaValidationException() {
    super("Captcha was validated");
  }
}
