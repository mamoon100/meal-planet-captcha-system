package com.mealplanet.captcha.model.response;

import java.util.UUID;

public record CaptchaResponse(UUID id, Long expires_in) {
}
