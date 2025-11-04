package com.mealplanet.captcha.repository;

import com.mealplanet.captcha.model.entity.CaptchaEntity;
import com.mealplanet.captcha.model.enums.CaptchaStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface CaptchaRepo extends JpaRepository<CaptchaEntity, UUID> {
    List<CaptchaEntity> findCaptchaByStatusIn(Collection<CaptchaStatusEnum> status);

    List<CaptchaEntity> findCaptchaByExpiresAtBeforeAndFileNameNotNull(LocalDateTime time);
}
