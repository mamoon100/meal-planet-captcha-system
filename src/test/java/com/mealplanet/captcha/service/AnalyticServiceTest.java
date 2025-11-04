package com.mealplanet.captcha.service;

import com.mealplanet.captcha.TestUtils.CaptchaGenerationTestUtil;
import com.mealplanet.captcha.mapper.CaptchaMapper;
import com.mealplanet.captcha.model.dto.CaptchaDto;
import com.mealplanet.captcha.model.enums.CaptchaStatusEnum;
import com.mealplanet.captcha.model.enums.CaptchaTypeEnum;
import com.mealplanet.captcha.model.response.AnalyticResponse;
import com.mealplanet.captcha.repository.CaptchaRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalyticServiceTest {

    @Mock
    private CaptchaRepo captchaRepo;

    @InjectMocks
    private AnalyticService analyticService;

    private final List<CaptchaStatusEnum> captchaStatusFilter = List.of(CaptchaStatusEnum.VALID, CaptchaStatusEnum.INVALID);

    private final SecureRandom secureRandom = new SecureRandom();


    @Test
    void givenListThatHaveOneType_whenGetStatistic_thenReturnCorrectStatistic() {
        List<CaptchaDto> captchaDtoList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            CaptchaDto captchaDto = CaptchaGenerationTestUtil.generateCaptchaDto(CaptchaTypeEnum.IMAGE, CaptchaStatusEnum.VALID);
            captchaDtoList.add(captchaDto);
        }
        when(captchaRepo.findCaptchaByStatusIn(captchaStatusFilter))
                .thenReturn(captchaDtoList.stream().map(CaptchaMapper::toEntity).toList());
        AnalyticResponse analyticResponse = assertDoesNotThrow(() -> analyticService.getStatistic());
        assertEquals(10, analyticResponse.image().correct());
        assertEquals(0, analyticResponse.image().incorrect());
        assertEquals(0, analyticResponse.math().correct());
        assertEquals(0, analyticResponse.math().incorrect());
    }

    @Test
    void givenListThatHaveTwoType_whenGetStatistic_thenReturnCorrectStatistic() {
        List<CaptchaDto> captchaDtoList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            CaptchaDto invalidImageCaptcha = CaptchaGenerationTestUtil.generateCaptchaDto(CaptchaTypeEnum.IMAGE, CaptchaStatusEnum.INVALID);
            CaptchaDto validImageCaptcha = CaptchaGenerationTestUtil.generateCaptchaDto(CaptchaTypeEnum.IMAGE, CaptchaStatusEnum.VALID);
            CaptchaDto invalidMathCaptcha = CaptchaGenerationTestUtil.generateCaptchaDto(CaptchaTypeEnum.MATH, CaptchaStatusEnum.INVALID);
            CaptchaDto validMathCaptcha = CaptchaGenerationTestUtil.generateCaptchaDto(CaptchaTypeEnum.MATH, CaptchaStatusEnum.VALID);
            captchaDtoList.add(invalidMathCaptcha);
            captchaDtoList.add(validMathCaptcha);
            captchaDtoList.add(validImageCaptcha);
            captchaDtoList.add(invalidImageCaptcha);
        }
        when(captchaRepo.findCaptchaByStatusIn(captchaStatusFilter))
                .thenReturn(captchaDtoList.stream().map(CaptchaMapper::toEntity).toList());
        AnalyticResponse analyticResponse = assertDoesNotThrow(() -> analyticService.getStatistic());
        assertEquals(10, analyticResponse.image().correct());
        assertEquals(10, analyticResponse.image().incorrect());
        assertEquals(10, analyticResponse.math().correct());
        assertEquals(10, analyticResponse.math().incorrect());
    }

}