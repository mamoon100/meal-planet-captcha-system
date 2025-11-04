package com.mealplanet.captcha.util;

import com.mealplanet.captcha.model.dto.MathExpressionDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExpressionGenerationUtilTest {


    @Test
    void givenLengthForString_whenGeneratingRandomString_thenReturnStringOfCorrectLength() {
        String randomString = ExpressionGenerationUtil.generateRandomString(10);
        assertEquals(10, randomString.length());
    }

    @Test
    void whenGenerateMathExpression_thenReturnValidMathExpressionWithValidAnswer() {
        MathExpressionDto mathExpression = ExpressionGenerationUtil.generateMathExpression();
        assertNotNull(mathExpression);
        assertTrue(mathExpression.mathExpression().matches("\\d+ \\+ \\d+"));
        assertEquals(Integer.parseInt(mathExpression.mathExpression().split(" ")[0]) + Integer.parseInt(mathExpression.mathExpression().split(" ")[2]), mathExpression.answer());
    }

}