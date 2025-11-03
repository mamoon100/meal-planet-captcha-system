package com.mealplanet.captcha.model.response;

public record AnalyticResponse(
        TypeStatisticsResponse image,
        TypeStatisticsResponse math
) {
}
