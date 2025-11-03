package com.mealplanet.captcha.model.response;

public record TypeStatisticsResponse(
        long correct,
        long incorrect
) {
}
