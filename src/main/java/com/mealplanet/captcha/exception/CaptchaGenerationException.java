package com.mealplanet.captcha.exception;

public class CaptchaGenerationException extends RuntimeException {
  public CaptchaGenerationException() {
    super("There was an error generating the captcha");
  }
}
