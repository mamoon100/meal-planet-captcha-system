package com.mealplanet.captcha.service;

import com.mealplanet.captcha.exception.CaptchaGenerationException;
import com.mealplanet.captcha.exception.CaptchaNotFoundException;
import com.mealplanet.captcha.exception.CaptchaValidationException;
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
import com.mealplanet.captcha.util.HashingUtil;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CaptchaService {

  private final CaptchaGenerateService captchaGenerateService;
  private final ImageGenerationService imageGenerationService;
  private final CaptchaRepo captchaRepo;
  private final int captchaExpirationSeconds;

  public CaptchaService(
      CaptchaGenerateService captchaGenerateService,
      ImageGenerationService imageGenerationService,
      CaptchaRepo captchaRepo,
      @Value("${captcha.lifespan}") int captchaExpirationSeconds
  ) {
    this.captchaGenerateService = captchaGenerateService;
    this.imageGenerationService = imageGenerationService;
    this.captchaRepo = captchaRepo;
    this.captchaExpirationSeconds = captchaExpirationSeconds;
  }

  public CaptchaResponse generateCaptcha(CaptchaTypeEnum type) {
    log.info("Generating captcha of type {}", type);
    try {
      return switch (type) {
        case MATH -> generateImageCaptcha();
        case IMAGE -> generateMathCaptcha();
      };
    } catch (Exception e) {
      log.error("Error generating captcha", e);
      throw new CaptchaGenerationException();
    }
  }

  public byte[] getCaptchImage(UUID id) {
    try {
      CaptchaDto captchaDto = captchaRepo.findById(id).map(CaptchaMapper::toDto).orElseThrow(CaptchaNotFoundException::new);
      return Files.readAllBytes(Paths.get(captchaDto.getFileName()));
    } catch (Exception e) {
      log.error("Error reading captcha image", e);
      throw new CaptchaNotFoundException();
    }
  }

  public CaptchaValidationResponse validateCaptcha(UUID id, CaptchaValidationRequest captcha) {
    CaptchaDto captchaDto = captchaRepo.findById(id).map(CaptchaMapper::toDto).orElseThrow(CaptchaNotFoundException::new);
    if (!captchaDto.getStatus().equals(CaptchaStatusEnum.NEW)) {
      throw new CaptchaValidationException();
    }
    boolean valid = captchaDto.getAnswer().equals(HashingUtil.hashSHA256(captcha.answer()));
    captchaDto.setStatus(valid ? CaptchaStatusEnum.VALID : CaptchaStatusEnum.INVALID);
    captchaRepo.save(CaptchaMapper.toEntity(captchaDto));
    return new CaptchaValidationResponse(valid);
  }

  private CaptchaResponse generateImageCaptcha() throws IOException {
    String randomString = captchaGenerateService.generateRandomString(5);
    return generateAndStoreCaptcha(randomString, randomString, CaptchaTypeEnum.IMAGE);
  }

  private CaptchaResponse generateMathCaptcha() throws IOException {
    MathExpressionDto mathExpressionDto = captchaGenerateService.generateMathExpression();
    return generateAndStoreCaptcha(mathExpressionDto.mathExpression(), String.valueOf(mathExpressionDto.answer()), CaptchaTypeEnum.MATH);
  }

  private CaptchaResponse generateAndStoreCaptcha(String captchaExpression, String answer, CaptchaTypeEnum captchaType) throws IOException {
    File file = imageGenerationService.generateAndSaveImage(captchaExpression, "captcha/output");
    CaptchaEntity entity = generateCaptchaEntity(
        file.getName(),
        answer,
        captchaType
    );
    captchaRepo.save(entity);
    return new CaptchaResponse(entity.getId(), Duration.between(LocalDateTime.now(), entity.getExpiresAt()).getSeconds());
  }

  private CaptchaEntity generateCaptchaEntity(
      String fileName,
      String answer,
      CaptchaTypeEnum captchaTypeEnum
  ) {
    CaptchaEntity captchaEntity = new CaptchaEntity();
    captchaEntity.setId(UUID.randomUUID());
    captchaEntity.setExpiresAt(LocalDateTime.now().plusSeconds(captchaExpirationSeconds));
    captchaEntity.setFileName(fileName);
    captchaEntity.setAnswer(HashingUtil.hashSHA256(answer));
    captchaEntity.setType(captchaTypeEnum);
    captchaEntity.setStatus(CaptchaStatusEnum.NEW);
    return captchaEntity;
  }
}
