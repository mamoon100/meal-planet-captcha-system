package com.mealplanet.captcha.controller;

import com.mealplanet.captcha.model.enums.CaptchaTypeEnum;
import com.mealplanet.captcha.model.request.CaptchaValidationRequest;
import com.mealplanet.captcha.model.response.CaptchaResponse;
import com.mealplanet.captcha.model.response.CaptchaValidationResponse;
import com.mealplanet.captcha.service.CaptchaService;
import java.util.UUID;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/captcha")
public class CaptchaController {


  private final CaptchaService captchaService;

  public CaptchaController(CaptchaService captchaService) {
    this.captchaService = captchaService;
  }

  @PostMapping("/generate")
  public CaptchaResponse generateCaptcha(@RequestParam CaptchaTypeEnum type) {
    return captchaService.generateCaptcha(type);
  }

  @GetMapping(path = "/{id}", produces = MediaType.IMAGE_PNG_VALUE)
  public byte[] getCaptchaById(@PathVariable UUID id) {
    return captchaService.getCaptchaImage(id);
  }

  @PostMapping("/validate/{id}")
  public CaptchaValidationResponse validateCaptcha(@PathVariable UUID id, @RequestBody CaptchaValidationRequest captcha) {
    return captchaService.validateCaptcha(id, captcha);
  }


}
