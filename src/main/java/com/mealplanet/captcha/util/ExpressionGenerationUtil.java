package com.mealplanet.captcha.util;

import com.mealplanet.captcha.model.dto.MathExpressionDto;

import java.security.SecureRandom;

public class ExpressionGenerationUtil {

    private final static String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private final static SecureRandom random = new SecureRandom();


    public static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }
        return sb.toString();
    }

    public static MathExpressionDto generateMathExpression() {
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
