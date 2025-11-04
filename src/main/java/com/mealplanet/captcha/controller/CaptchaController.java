package com.mealplanet.captcha.controller;

import com.mealplanet.captcha.model.enums.CaptchaTypeEnum;
import com.mealplanet.captcha.model.request.CaptchaValidationRequest;
import com.mealplanet.captcha.model.response.CaptchaResponse;
import com.mealplanet.captcha.model.response.CaptchaValidationResponse;
import com.mealplanet.captcha.service.CaptchaService;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/captcha")
public class CaptchaController {


    private final CaptchaService captchaService;
    private final MeterRegistry meterRegistry;

    public CaptchaController(CaptchaService captchaService, MeterRegistry meterRegistry) {
        this.captchaService = captchaService;
        this.meterRegistry = meterRegistry;
    }

    @PostMapping("/generate")
    public CaptchaResponse generateCaptcha(@RequestParam CaptchaTypeEnum type) {
        CaptchaResponse captchaResponse = captchaService.generateCaptcha(type);
        meterRegistry.counter("captcha.generated.count").increment();
        return captchaResponse;
    }

    @GetMapping(path = "/{id}", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] getCaptchaById(@PathVariable UUID id) {
        return captchaService.getCaptchaImage(id);
    }

    @PostMapping("/validate/{id}")
    public CaptchaValidationResponse validateCaptcha(@PathVariable UUID id, @RequestBody CaptchaValidationRequest captcha) {
        meterRegistry.counter("captcha.verification.count").increment();
        CaptchaValidationResponse captchaValidationResponse = captchaService.validateCaptcha(id, captcha);
        if (captchaValidationResponse.valid()) {
            meterRegistry.counter("captcha.valid.count").increment();
        } else {
            meterRegistry.counter("captcha.invalid.count").increment();
        }
        return captchaValidationResponse;
    }


}
