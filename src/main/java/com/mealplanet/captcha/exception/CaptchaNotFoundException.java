package com.mealplanet.captcha.exception;

public class CaptchaNotFoundException extends CaptchaSystemException {
  public CaptchaNotFoundException() {
    super("Captcha not found");
  }
}
