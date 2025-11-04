package com.mealplanet.captcha.scheduler;


import com.mealplanet.captcha.model.entity.CaptchaEntity;
import com.mealplanet.captcha.repository.CaptchaRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@EnableScheduling
@Component
@Slf4j
public class CaptchaExpirationScheduler {

    private final CaptchaRepo captchaRepo;

    public CaptchaExpirationScheduler(CaptchaRepo captchaRepo) {
        this.captchaRepo = captchaRepo;
    }

    @Scheduled(cron = "0 * * * * *") // every minute
    @Transactional
    public void cleanupExpiredCaptchaFiles() {
        List<CaptchaEntity> expired = captchaRepo.findCaptchaByExpiresAtBeforeAndFileNameNotNull(LocalDateTime.now());
        for (CaptchaEntity captchaEntity : expired) {
            try {
                Files.deleteIfExists(Paths.get(captchaEntity.getFileName()));
                captchaEntity.setFileName(null);
                captchaEntity.setExpired(true);
            } catch (IOException e) {
                log.warn("Could not delete file {} for captcha {}", captchaEntity.getFileName(), captchaEntity.getId(), e);
            }
        }
        captchaRepo.saveAll(expired);
    }
}
