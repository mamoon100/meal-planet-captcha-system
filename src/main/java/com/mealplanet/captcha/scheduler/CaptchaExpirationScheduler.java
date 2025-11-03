package com.mealplanet.captcha.scheduler;


import com.mealplanet.captcha.model.entity.CaptchaEntity;
import com.mealplanet.captcha.repository.CaptchaRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
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

    @Scheduled(cron = "0 * * * * *")
    public void checkCaptchaExpiration() {
        List<CaptchaEntity> captchaByExpiresAtBefore = captchaRepo.findCaptchaByExpiresAtBeforeAndFileNameNotNull(LocalDateTime.now());
        captchaByExpiresAtBefore.forEach(captcha -> {
            File file = new File(captcha.getFileName());
            if (file.exists()) {
                boolean deleted = file.delete();
                if (deleted) {
                    captcha.setFileName(null);
                    captcha.setExpired(true);
                    captchaRepo.save(captcha);
                } else {
                    log.warn("Could not delete the file {}", captcha.getFileName());
                }
            }
        });
    }
}
