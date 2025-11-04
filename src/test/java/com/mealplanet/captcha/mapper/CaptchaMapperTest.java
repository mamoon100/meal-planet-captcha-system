package com.mealplanet.captcha.mapper;

import com.mealplanet.captcha.model.dto.CaptchaDto;
import com.mealplanet.captcha.model.entity.CaptchaEntity;
import com.mealplanet.captcha.model.enums.CaptchaStatusEnum;
import com.mealplanet.captcha.model.enums.CaptchaTypeEnum;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CaptchaMapperTest {


    @Test
    void givenCaptchaDto_whenToEntity_thenReturnCaptchaEntity() {
        CaptchaDto captchaDto = new CaptchaDto();
        captchaDto.setId(UUID.randomUUID());
        captchaDto.setExpiresAt(LocalDateTime.now());
        captchaDto.setFileName("test.png");
        captchaDto.setAnswer("test");
        captchaDto.setType(CaptchaTypeEnum.IMAGE);
        captchaDto.setStatus(CaptchaStatusEnum.NEW);
        captchaDto.setExpired(false);
        CaptchaEntity captchaEntity = CaptchaMapper.toEntity(captchaDto);
        assertNotNull(captchaEntity);
        assertEquals(captchaDto.getId(), captchaEntity.getId());
        assertEquals(captchaDto.getExpiresAt(), captchaEntity.getExpiresAt());
        assertEquals(captchaDto.getFileName(), captchaEntity.getFileName());
        assertEquals(captchaDto.getAnswer(), captchaEntity.getAnswer());
        assertEquals(captchaDto.getType(), captchaEntity.getType());
        assertEquals(captchaDto.getStatus(), captchaEntity.getStatus());
        assertEquals(captchaDto.isExpired(), captchaEntity.isExpired());
    }

    @Test
    void givenCaptchaEntity_whenToDto_thenReturnCaptchaDto() {
        CaptchaEntity captchaEntity = new CaptchaEntity();
        captchaEntity.setId(UUID.randomUUID());
        captchaEntity.setExpiresAt(LocalDateTime.now());
        captchaEntity.setFileName("test.png");
        captchaEntity.setAnswer("test");
        captchaEntity.setType(CaptchaTypeEnum.IMAGE);
        captchaEntity.setStatus(CaptchaStatusEnum.NEW);
        captchaEntity.setExpired(false);
        CaptchaDto captchaDto = CaptchaMapper.toDto(captchaEntity);
        assertNotNull(captchaDto);
        assertEquals(captchaDto.getId(), captchaEntity.getId());
        assertEquals(captchaDto.getExpiresAt(), captchaEntity.getExpiresAt());
        assertEquals(captchaDto.getFileName(), captchaEntity.getFileName());
        assertEquals(captchaDto.getAnswer(), captchaEntity.getAnswer());
        assertEquals(captchaDto.getType(), captchaEntity.getType());
        assertEquals(captchaDto.getStatus(), captchaEntity.getStatus());
        assertEquals(captchaDto.isExpired(), captchaEntity.isExpired());
    }
}


