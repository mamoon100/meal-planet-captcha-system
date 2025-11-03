package com.mealplanet.captcha.controller;

import com.mealplanet.captcha.model.response.AnalyticResponse;
import com.mealplanet.captcha.service.AnalyticService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/analytic")
public class AnalyticController {

    private final AnalyticService analyticService;

    public AnalyticController(AnalyticService analyticService) {
        this.analyticService = analyticService;
    }

    @GetMapping
    public AnalyticResponse getCaptcha() {
        return analyticService.getStatistic();
    }
}
