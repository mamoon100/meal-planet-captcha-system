package com.mealplanet.captcha.mapper;

import com.mealplanet.captcha.model.dto.CaptchaDto;
import com.mealplanet.captcha.model.entity.CaptchaEntity;

public class CaptchaMapper {

    public static CaptchaDto toDto(CaptchaEntity entity) {
        return new CaptchaDto(
                entity.getId(),
                entity.getExpiresAt(),
                entity.getFileName(),
                entity.getAnswer(),
                entity.getType(),
                entity.getStatus(),
                entity.isExpired()
        );
    }

    public static CaptchaEntity toEntity(CaptchaDto dto) {
        return new CaptchaEntity(
                dto.getId(),
                dto.getExpiresAt(),
                dto.getFileName(),
                dto.getAnswer(),
                dto.getType(),
                dto.getStatus(),
                dto.isExpired()
        );
    }
}
