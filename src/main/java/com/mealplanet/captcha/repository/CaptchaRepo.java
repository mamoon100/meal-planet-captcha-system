package com.mealplanet.captcha.repository;

import com.mealplanet.captcha.model.entity.CaptchaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CaptchaRepo extends JpaRepository<CaptchaEntity, UUID> {
}
