package com.mealplanet.captcha.repository;

import com.mealplanet.captcha.TestUtils.CaptchaGenerationTestUtil;
import com.mealplanet.captcha.model.entity.CaptchaEntity;
import com.mealplanet.captcha.model.enums.CaptchaStatusEnum;
import com.mealplanet.captcha.model.enums.CaptchaTypeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class CaptchaRepoIT {

    @Autowired
    private CaptchaRepo captchaRepo;

    @BeforeEach
    void setup() {
        captchaRepo.deleteAll();
    }

    private CaptchaEntity entity(CaptchaTypeEnum typeEnum, CaptchaStatusEnum status, String fileName) {
        CaptchaEntity captchaEntity = CaptchaGenerationTestUtil.generateCaptchaEntity(typeEnum, status);
        captchaEntity.setFileName(fileName);
        return captchaEntity;
    }

    @Test
    @DisplayName("findCaptchaByStatusIn returns only VALID/INVALID from mixed statuses")
    void givenCaptchaStatus_whenFindCaptchaByStatusIn_thenReturnCorrectData() {
        CaptchaEntity newImg = entity(CaptchaTypeEnum.IMAGE, CaptchaStatusEnum.NEW, "file1.png");
        CaptchaEntity validImg = entity(CaptchaTypeEnum.IMAGE, CaptchaStatusEnum.VALID, "file2.png");
        CaptchaEntity invalidMath = entity(CaptchaTypeEnum.MATH, CaptchaStatusEnum.INVALID, null);
        captchaRepo.saveAll(List.of(newImg, validImg, invalidMath));

        List<CaptchaEntity> result = captchaRepo.findCaptchaByStatusIn(List.of(CaptchaStatusEnum.VALID, CaptchaStatusEnum.INVALID));

        assertThat(result)
                .extracting(CaptchaEntity::getStatus)
                .containsExactlyInAnyOrder(CaptchaStatusEnum.VALID, CaptchaStatusEnum.INVALID);
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("findCaptchaByExpiresAtBeforeAndFileNameNotNull returns only expired with file present")
    void givenSomeExpiredCaptchaFile_whenFindCaptchaByExpiresAtBeforeAndFileNameNotNull_thenReturnCorrectData() {
        LocalDateTime now = LocalDateTime.now();
        CaptchaEntity expiredWithFile = entity(CaptchaTypeEnum.IMAGE, CaptchaStatusEnum.NEW, "file.png");
        expiredWithFile.setExpiresAt(now.minusMinutes(1));
        CaptchaEntity expiredNoFile = entity(CaptchaTypeEnum.IMAGE, CaptchaStatusEnum.NEW, null);
        expiredNoFile.setExpiresAt(now.minusMinutes(1));
        CaptchaEntity futureWithFile = entity(CaptchaTypeEnum.IMAGE, CaptchaStatusEnum.NEW, "file2.png");
        futureWithFile.setExpiresAt(now.plusMinutes(1));

        captchaRepo.saveAll(List.of(expiredWithFile, expiredNoFile, futureWithFile));

        List<CaptchaEntity> result = captchaRepo.findCaptchaByExpiresAtBeforeAndFileNameNotNull(now);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(expiredWithFile.getId());
    }
}
