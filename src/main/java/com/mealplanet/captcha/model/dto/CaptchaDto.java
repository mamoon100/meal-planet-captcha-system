package com.mealplanet.captcha.model.dto;

import com.mealplanet.captcha.model.enums.CaptchaStatusEnum;
import com.mealplanet.captcha.model.enums.CaptchaTypeEnum;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CaptchaDto {
  private UUID id;
  private LocalDateTime expiresAt;
  private String fileName;
  private String answer;
  private CaptchaTypeEnum type;
  private CaptchaStatusEnum status;
}