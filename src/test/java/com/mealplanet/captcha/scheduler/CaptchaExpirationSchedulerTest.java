package com.mealplanet.captcha.scheduler;

import com.mealplanet.captcha.TestUtils.CaptchaGenerationTestUtil;
import com.mealplanet.captcha.model.entity.CaptchaEntity;
import com.mealplanet.captcha.model.enums.CaptchaStatusEnum;
import com.mealplanet.captcha.model.enums.CaptchaTypeEnum;
import com.mealplanet.captcha.repository.CaptchaRepo;
import com.mealplanet.captcha.util.ImageGenerationUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class CaptchaExpirationSchedulerTest {

    private static final String OUTPUT = "captcha/output";


    @Mock
    private CaptchaRepo captchaRepo;

    @InjectMocks
    private CaptchaExpirationScheduler captchaExpirationScheduler;

    @AfterEach
    void tearDown() {
        truncateOutputFolder(OUTPUT);
    }

    private void truncateOutputFolder(String folder) {
        File file = new File(folder);
        File[] files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                f.delete();
            }
        }
    }

    @Test
    void givenOldAndNewCaptchaFiles_whenCleanupExpiredCaptchaFiles_thenDeleteExpiredImagesAndSetExpire() throws Exception {
        List<CaptchaEntity> captchaEntities = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            CaptchaEntity captchaEntity = CaptchaGenerationTestUtil.generateCaptchaEntity(CaptchaTypeEnum.IMAGE, CaptchaStatusEnum.VALID);
            File file = ImageGenerationUtil.generateAndSaveImage(UUID.randomUUID().toString(), "test", OUTPUT);
            captchaEntity.setFileName(OUTPUT + File.separator + file.getName());
            captchaEntities.add(captchaEntity);
        }
        when(captchaRepo.findCaptchaByExpiresAtBeforeAndFileNameNotNull(any())).thenReturn(captchaEntities);
        captchaExpirationScheduler.cleanupExpiredCaptchaFiles();
        captchaEntities.forEach(captchaEntity -> {
            File file = new File(OUTPUT + File.separator + captchaEntity.getFileName());
            assert (!file.exists());
        });
        Mockito.verify(captchaRepo, Mockito.times(1)).saveAll(any());

    }

}