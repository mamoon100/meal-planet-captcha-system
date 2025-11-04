package com.mealplanet.captcha.service;

import com.mealplanet.captcha.TestUtils.CaptchaGenerationTestUtil;
import com.mealplanet.captcha.exception.*;
import com.mealplanet.captcha.mapper.CaptchaMapper;
import com.mealplanet.captcha.model.dto.CaptchaDto;
import com.mealplanet.captcha.model.enums.CaptchaStatusEnum;
import com.mealplanet.captcha.model.enums.CaptchaTypeEnum;
import com.mealplanet.captcha.model.request.CaptchaValidationRequest;
import com.mealplanet.captcha.model.response.CaptchaResponse;
import com.mealplanet.captcha.repository.CaptchaRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class CaptchaServiceTest {

    private static final String OUTPUT = "captcha/output";

    @Mock
    private CaptchaRepo captchaRepo;

    private CaptchaService captchaService;

    private final int captchaExpirationSeconds = 120;

    @BeforeEach
    void setUp() {
        captchaService = new CaptchaService(captchaRepo, captchaExpirationSeconds);
    }

    @AfterEach
    void tearDown() {
        truncateOutputFolder();
    }

    private void truncateOutputFolder() {
        File file = new File(OUTPUT);
        File[] files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                f.delete();
            }
        }
    }

    @Test
    void givenAnErrorThrown_whenGenerateCaptcha_thenThrowCaptchaGenerationException() {
        Mockito.when(captchaRepo.save(any())).thenThrow(new RuntimeException("Error saving captcha"));
        assertThrows(CaptchaGenerationException.class, () -> captchaService.generateCaptcha(CaptchaTypeEnum.IMAGE));
    }


    @Test
    void givenImageCaptchaType_whenGenerateCaptcha_thenReturnCaptchaResponse() {
        CaptchaResponse captchaResponse = assertDoesNotThrow(() -> captchaService.generateCaptcha(CaptchaTypeEnum.IMAGE));
        File outputDirectory = new File(OUTPUT);
        List<File> files = Arrays.stream(outputDirectory.listFiles()).filter(File::isFile).toList();
        assertNotNull(files);
        assertEquals(1, files.size());
        assertNotNull(outputDirectory);
        assertNotNull(captchaResponse);
        assertNotNull(captchaResponse.id());
        assertNotNull(captchaResponse.expires_in());
        assertEquals(captchaExpirationSeconds, captchaResponse.expires_in());
    }

    @Test
    void givenMathCaptchaType_whenGenerateCaptcha_thenReturnCaptchaResponseWithFileName() {
        CaptchaResponse captchaResponse = assertDoesNotThrow(() -> captchaService.generateCaptcha(CaptchaTypeEnum.MATH));
        File outputDirectory = new File(OUTPUT);
        List<File> files = Arrays.stream(outputDirectory.listFiles()).filter(File::isFile).toList();
        assertNotNull(files);
        assertEquals(1, files.size());
        assertNotNull(outputDirectory);
        assertNotNull(captchaResponse);
    }

    @Test
    void givenInvalidId_whenGetCaptchaImage_thenThrowCaptchaNotFoundException() {
        UUID invaludUUID = UUID.randomUUID();
        Mockito.when(captchaRepo.findById(invaludUUID)).thenReturn(Optional.empty());
        assertThrows(CaptchaNotFoundException.class,
                () -> captchaService.getCaptchaImage(invaludUUID)
        );
    }

    @Test
    void givenNotNewCaptchaUUID_whenGetCaptchaImage_thenThrowCaptchaValidationException() {
        UUID invaludUUID = UUID.randomUUID();
        Mockito.when(captchaRepo.findById(invaludUUID)).thenReturn(
                Optional.of(CaptchaGenerationTestUtil.generateCaptchaEntity(CaptchaTypeEnum.IMAGE, CaptchaStatusEnum.VALID))
        );
        assertThrows(CaptchaValidationException.class,
                () -> captchaService.getCaptchaImage(invaludUUID)
        );
    }

    @Test
    void givenExpiredCaptchaUUID_whenGetCaptchaImage_thenThrowCaptchaExpiredException() {
        UUID expiredCaptchaId = UUID.randomUUID();
        CaptchaDto captchaDto = CaptchaGenerationTestUtil.generateCaptchaDto(CaptchaTypeEnum.IMAGE, CaptchaStatusEnum.NEW);
        captchaDto.setExpiresAt(LocalDateTime.now().minusSeconds(captchaExpirationSeconds));
        Mockito.when(captchaRepo.findById(expiredCaptchaId)).thenReturn(
                Optional.of(CaptchaMapper.toEntity(captchaDto))
        );
        assertThrows(CaptchaExpiredException.class,
                () -> captchaService.getCaptchaImage(expiredCaptchaId)
        );
    }


    @Test
    void givenValidNewUUID_whenGetCaptchaImage_thenReturnImageBytes() {
        CaptchaResponse captchaResponse = captchaService.generateCaptcha(CaptchaTypeEnum.IMAGE);
        CaptchaDto captchaDto = CaptchaGenerationTestUtil.generateCaptchaDto(CaptchaTypeEnum.IMAGE, CaptchaStatusEnum.NEW);
        String fileName = OUTPUT + File.separator + getFileName();
        captchaDto.setId(captchaResponse.id());
        captchaDto.setFileName(fileName);
        Mockito.when(captchaRepo.findById(captchaResponse.id())).thenReturn(
                Optional.of(CaptchaMapper.toEntity(captchaDto))
        );
        byte[] bytes = assertDoesNotThrow(() -> captchaService.getCaptchaImage(captchaResponse.id()));
        assertNotNull(bytes);
    }

    @Test
    void givenValidNewUUIDWithInvalidFileNamePath_whenGetCaptchaImage_thenThrowGeneralErrorException() {
        CaptchaResponse captchaResponse = captchaService.generateCaptcha(CaptchaTypeEnum.IMAGE);
        CaptchaDto captchaDto = CaptchaGenerationTestUtil.generateCaptchaDto(CaptchaTypeEnum.IMAGE, CaptchaStatusEnum.NEW);
        captchaDto.setId(captchaResponse.id());
        Mockito.when(captchaRepo.findById(captchaResponse.id())).thenReturn(
                Optional.of(CaptchaMapper.toEntity(captchaDto))
        );
        assertThrows(GeneralErrorException.class, () -> captchaService.getCaptchaImage(captchaResponse.id()));
    }

    @Test
    void givenInvalidUUID_whenValidateCaptcha_thenThrowCaptchaNotFoundException() {
        UUID invaludUUID = UUID.randomUUID();
        Mockito.when(captchaRepo.findById(invaludUUID)).thenReturn(Optional.empty());
        assertThrows(CaptchaNotFoundException.class,
                () -> captchaService.validateCaptcha(invaludUUID, new CaptchaValidationRequest("test"))
        );
    }

    @Test
    void givenAlreadyAnsweredCaptchaId_whenValidateCaptcha_thenThrowCaptchaValidationException() {
        UUID invaludUUID = UUID.randomUUID();
        Mockito.when(captchaRepo.findById(invaludUUID)).thenReturn(Optional.of(CaptchaGenerationTestUtil.generateCaptchaEntity(
                CaptchaTypeEnum.IMAGE, CaptchaStatusEnum.INVALID
        )));
        assertThrows(CaptchaValidationException.class,
                () -> captchaService.validateCaptcha(invaludUUID, new CaptchaValidationRequest("test"))
        );
    }

    @Test
    void givenExpiredCaptchaId_whenValidateCaptcha_thenThrowCaptchaExpiredException() {
        UUID invaludUUID = UUID.randomUUID();
        CaptchaDto dto = CaptchaGenerationTestUtil.generateCaptchaDto(
                CaptchaTypeEnum.MATH, CaptchaStatusEnum.NEW
        );
        dto.setExpiresAt(LocalDateTime.now().minusSeconds(captchaExpirationSeconds));
        Mockito.when(captchaRepo.findById(invaludUUID)).thenReturn(Optional.of(CaptchaMapper.toEntity(dto)));
        assertThrows(CaptchaExpiredException.class,
                () -> captchaService.validateCaptcha(invaludUUID, new CaptchaValidationRequest("test"))
        );
    }

    @Test
    void givenWrongAnswer_whenValidateCaptcha_thenReturnFalse() {
        UUID invaludUUID = UUID.randomUUID();
        CaptchaDto dto = CaptchaGenerationTestUtil.generateCaptchaDto(
                CaptchaTypeEnum.MATH, CaptchaStatusEnum.NEW
        );
        Mockito.when(captchaRepo.findById(invaludUUID)).thenReturn(Optional.of(CaptchaMapper.toEntity(dto)));
        assertFalse(captchaService.validateCaptcha(invaludUUID, new CaptchaValidationRequest("wrongAnswer")).valid());
    }

    @Test
    void givenCorrectAnswer_whenValidateCaptcha_thenReturnTrue() {
        UUID invaludUUID = UUID.randomUUID();
        CaptchaDto dto = CaptchaGenerationTestUtil.generateCaptchaDto(
                CaptchaTypeEnum.MATH, CaptchaStatusEnum.NEW
        );
        Mockito.when(captchaRepo.findById(invaludUUID)).thenReturn(Optional.of(CaptchaMapper.toEntity(dto)));
        assertTrue(captchaService.validateCaptcha(invaludUUID, new CaptchaValidationRequest("test")).valid());
    }


    private String getFileName() {
        File outputDirectory = new File(OUTPUT);
        List<File> files = Arrays.stream(outputDirectory.listFiles()).filter(File::isFile).toList();
        assertNotNull(files);
        assertEquals(1, files.size());
        return files.get(0).getName();
    }


}