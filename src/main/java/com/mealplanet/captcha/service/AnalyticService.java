package com.mealplanet.captcha.service;

import com.mealplanet.captcha.mapper.CaptchaMapper;
import com.mealplanet.captcha.model.dto.CaptchaDto;
import com.mealplanet.captcha.model.enums.CaptchaStatusEnum;
import com.mealplanet.captcha.model.enums.CaptchaTypeEnum;
import com.mealplanet.captcha.model.response.AnalyticResponse;
import com.mealplanet.captcha.model.response.TypeStatisticsResponse;
import com.mealplanet.captcha.repository.CaptchaRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AnalyticService {

    private final CaptchaRepo captchaRepo;

    public AnalyticService(CaptchaRepo captchaRepo) {
        this.captchaRepo = captchaRepo;
    }

    public AnalyticResponse getStatistic() {
        Map<CaptchaTypeEnum, List<CaptchaDto>> captchaDtoByTypeMap = captchaRepo
                .findCaptchaByStatusIn(List.of(CaptchaStatusEnum.VALID, CaptchaStatusEnum.INVALID))
                .stream()
                .map(CaptchaMapper::toDto)
                .collect(Collectors.groupingBy(CaptchaDto::getType));
        TypeStatisticsResponse imageStats = buildTypeStats(captchaDtoByTypeMap, CaptchaTypeEnum.IMAGE);
        TypeStatisticsResponse mathStats = buildTypeStats(captchaDtoByTypeMap, CaptchaTypeEnum.MATH);
        return new AnalyticResponse(imageStats, mathStats);
    }

    private TypeStatisticsResponse buildTypeStats(Map<CaptchaTypeEnum, List<CaptchaDto>> grouped, CaptchaTypeEnum type) {
        List<CaptchaDto> list = grouped.getOrDefault(type, List.of());
        long valid = list.stream().filter(captchaDto -> captchaDto.getStatus() == CaptchaStatusEnum.VALID).count();
        long invalid = list.stream().filter(captchaDto -> captchaDto.getStatus() == CaptchaStatusEnum.INVALID).count();
        return new TypeStatisticsResponse(valid, invalid);
    }
}
