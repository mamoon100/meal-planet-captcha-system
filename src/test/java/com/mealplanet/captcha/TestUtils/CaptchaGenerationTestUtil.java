package com.mealplanet.captcha.TestUtils;

import com.mealplanet.captcha.model.dto.CaptchaDto;
import com.mealplanet.captcha.model.enums.CaptchaStatusEnum;
import com.mealplanet.captcha.model.enums.CaptchaTypeEnum;
import com.mealplanet.captcha.util.HashingUtil;

import java.time.LocalDateTime;
import java.util.UUID;

public class CaptchaGenerationTestUtil {

    public static CaptchaDto generateCaptcha(CaptchaTypeEnum captchaType,CaptchaStatusEnum statusEnum ) {
        CaptchaDto captchaDto = new CaptchaDto();
        captchaDto.setId(UUID.randomUUID());
        captchaDto.setExpiresAt(LocalDateTime.now().plusSeconds(120));
        captchaDto.setFileName(System.currentTimeMillis() + "-test.png");
        captchaDto.setAnswer(HashingUtil.hashSHA256("test"));
        captchaDto.setType(captchaType);
        captchaDto.setStatus(statusEnum);
        captchaDto.setExpired(false);
        return captchaDto;
    }
}
