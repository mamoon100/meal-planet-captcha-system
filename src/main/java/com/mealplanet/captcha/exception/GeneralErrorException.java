package com.mealplanet.captcha.exception;

public class GeneralErrorException extends RuntimeException {
  public GeneralErrorException() {
    super("General error occurred, please try again later");
  }
}
