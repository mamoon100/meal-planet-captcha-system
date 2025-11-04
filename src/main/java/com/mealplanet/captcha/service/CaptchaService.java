package com.mealplanet.captcha.service;

import com.mealplanet.captcha.exception.*;
import com.mealplanet.captcha.mapper.CaptchaMapper;
import com.mealplanet.captcha.model.dto.CaptchaDto;
import com.mealplanet.captcha.model.dto.MathExpressionDto;
import com.mealplanet.captcha.model.entity.CaptchaEntity;
import com.mealplanet.captcha.model.enums.CaptchaStatusEnum;
import com.mealplanet.captcha.model.enums.CaptchaTypeEnum;
import com.mealplanet.captcha.model.request.CaptchaValidationRequest;
import com.mealplanet.captcha.model.response.CaptchaResponse;
import com.mealplanet.captcha.model.response.CaptchaValidationResponse;
import com.mealplanet.captcha.repository.CaptchaRepo;
import com.mealplanet.captcha.util.ExpressionGenerationUtil;
import com.mealplanet.captcha.util.HashingUtil;
import com.mealplanet.captcha.util.ImageGenerationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
public class CaptchaService {


    private final int TIME_CORRECTNESS_VALUE = 1;
    private final CaptchaRepo captchaRepo;
    private final int captchaExpirationSeconds;

    public CaptchaService(
            CaptchaRepo captchaRepo,
            @Value("${captcha.lifespan}") int captchaExpirationSeconds
    ) {
        this.captchaRepo = captchaRepo;
        this.captchaExpirationSeconds = captchaExpirationSeconds;
    }

    public CaptchaResponse generateCaptcha(CaptchaTypeEnum type) {
        log.info("Generating captcha of type {}", type);
        try {
            return switch (type) {
                case IMAGE -> generateImageCaptcha();
                case MATH -> generateMathCaptcha();
            };
        } catch (Exception e) {
            log.error("Error generating captcha", e);
            throw new CaptchaGenerationException();
        }
    }

    public byte[] getCaptchaImage(UUID id) {
        log.info("Getting captcha image with id {}", id);
        try {
            CaptchaDto captchaDto = captchaRepo.findById(id).map(CaptchaMapper::toDto).orElseThrow(CaptchaNotFoundException::new);
            validateStatusAndExpiration(captchaDto);
            return Files.readAllBytes(Paths.get(captchaDto.getFileName()));
        } catch (IOException e) {
            log.error("Error reading captcha image", e);
            throw new GeneralErrorException();
        }
    }

    public CaptchaValidationResponse validateCaptcha(UUID id, CaptchaValidationRequest captcha) {
        log.info("Validating captcha with id {}", id);
        CaptchaDto captchaDto = captchaRepo.findById(id).map(CaptchaMapper::toDto).orElseThrow(CaptchaNotFoundException::new);
        validateStatusAndExpiration(captchaDto);
        boolean isValid = captchaDto.getAnswer().equals(HashingUtil.hashSHA256(captcha.answer()));
        captchaDto.setStatus(isValid ? CaptchaStatusEnum.VALID : CaptchaStatusEnum.INVALID);
        captchaRepo.save(CaptchaMapper.toEntity(captchaDto));
        log.info("Captcha validation result: {}", isValid);
        return new CaptchaValidationResponse(isValid);
    }

    private void validateStatusAndExpiration(CaptchaDto captchaDto) {
        log.info("Validating captcha status and expiration for id {}", captchaDto.getId());
        if (!captchaDto.getStatus().equals(CaptchaStatusEnum.NEW)) {
            log.info("Captcha with id {} has invalid status {}", captchaDto.getId(), captchaDto.getStatus());
            throw new CaptchaValidationException();
        }
        if (captchaDto.getExpiresAt().isBefore(LocalDateTime.now())) {
            log.info("Captcha with id {} has expired", captchaDto.getId());
            captchaDto.setExpired(true);
            captchaRepo.save(CaptchaMapper.toEntity(captchaDto));
            throw new CaptchaExpiredException();
        }
    }


    private CaptchaResponse generateImageCaptcha() throws IOException {
        log.info("Generating image captcha");
        String randomString = ExpressionGenerationUtil.generateRandomString(5);
        return generateAndStoreCaptcha(randomString, randomString, CaptchaTypeEnum.IMAGE);
    }

    private CaptchaResponse generateMathCaptcha() throws IOException {
        log.info("Generating math captcha");
        MathExpressionDto mathExpressionDto = ExpressionGenerationUtil.generateMathExpression();
        return generateAndStoreCaptcha(mathExpressionDto.mathExpression(), String.valueOf(mathExpressionDto.answer()), CaptchaTypeEnum.MATH);
    }

    private CaptchaResponse generateAndStoreCaptcha(String captchaExpression, String answer, CaptchaTypeEnum captchaType) throws IOException {
        log.info("Generating and storing captcha of type {}", captchaType);
        UUID uuid = UUID.randomUUID();
        File file = ImageGenerationUtil.generateAndSaveImage(uuid.toString(), captchaExpression, "captcha/output");
        CaptchaEntity entity = generateCaptchaEntity(
                uuid,
                file.getPath(),
                answer,
                captchaType
        );
        captchaRepo.save(entity);
        return new CaptchaResponse(entity.getId(), Duration.between(LocalDateTime.now(), entity.getExpiresAt()).getSeconds());
    }

    private CaptchaEntity generateCaptchaEntity(
            UUID uuid, String fileName,
            String answer,
            CaptchaTypeEnum captchaTypeEnum
    ) {
        log.info("Generating captcha entity with id {}", uuid);
        CaptchaEntity captchaEntity = new CaptchaEntity();
        captchaEntity.setId(uuid);
        captchaEntity.setExpiresAt(LocalDateTime.now().plusSeconds(captchaExpirationSeconds + TIME_CORRECTNESS_VALUE));
        captchaEntity.setFileName(fileName);
        captchaEntity.setAnswer(HashingUtil.hashSHA256(answer));
        captchaEntity.setType(captchaTypeEnum);
        captchaEntity.setStatus(CaptchaStatusEnum.NEW);
        return captchaEntity;
    }
}