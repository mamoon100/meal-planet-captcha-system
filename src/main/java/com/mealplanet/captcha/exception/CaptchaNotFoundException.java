package com.mealplanet.captcha.exception;

public class CaptchaNotFoundException extends RuntimeException {
  public CaptchaNotFoundException() {
    super("Captcha not found");
  }
}
