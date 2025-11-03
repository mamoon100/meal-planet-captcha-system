package com.mealplanet.captcha.service;

import com.mealplanet.captcha.model.dto.MathExpressionDto;
import java.security.SecureRandom;
import org.springframework.stereotype.Service;

@Service
public class CaptchaGenerateService {

  private final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
  private final SecureRandom random = new SecureRandom();


  public String generateRandomString(int length) {
    StringBuilder sb = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      int index = random.nextInt(CHARACTERS.length());
      sb.append(CHARACTERS.charAt(index));
    }
    return sb.toString();
  }

  public MathExpressionDto generateMathExpression() {
    int x = random.nextInt(9) + 1;
    int y = random.nextInt(9) + 1;
    String mathExpression = x + " + " + y;
    int answer = x + y;
    return new MathExpressionDto(
        mathExpression,
        answer
    );
  }
}
