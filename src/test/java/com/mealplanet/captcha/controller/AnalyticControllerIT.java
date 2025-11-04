package com.mealplanet.captcha.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mealplanet.captcha.TestUtils.CaptchaGenerationTestUtil;
import com.mealplanet.captcha.model.entity.CaptchaEntity;
import com.mealplanet.captcha.model.enums.CaptchaStatusEnum;
import com.mealplanet.captcha.model.enums.CaptchaTypeEnum;
import com.mealplanet.captcha.repository.CaptchaRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AnalyticControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CaptchaRepo captchaRepo;

    @BeforeEach
    void setup() {
        captchaRepo.deleteAll();
        generateInitialData();
    }

    private CaptchaEntity getEntity(CaptchaTypeEnum type, CaptchaStatusEnum status) {
        return CaptchaGenerationTestUtil.generateCaptchaEntity(type, status);
    }

    @Test
    @DisplayName("GET /api/v1/analytic aggregates VALID/INVALID counts per type")
    void givenSomeCaptchaDataStoredInTheRepo_whenGetStatistic_thenReturnCorrectStatisticData() throws Exception {
        String body = mockMvc.perform(get("/api/v1/analytic"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        JsonNode json = objectMapper.readTree(body);
        long imageCorrect = json.get("image").get("correct").asLong();
        long imageIncorrect = json.get("image").get("incorrect").asLong();
        long mathCorrect = json.get("math").get("correct").asLong();
        long mathIncorrect = json.get("math").get("incorrect").asLong();

        assertThat(imageCorrect).isEqualTo(2);
        assertThat(imageIncorrect).isEqualTo(1);
        assertThat(mathCorrect).isEqualTo(1);
        assertThat(mathIncorrect).isEqualTo(3);
    }

    private void generateInitialData() {
        captchaRepo.save(getEntity(CaptchaTypeEnum.IMAGE, CaptchaStatusEnum.VALID));
        captchaRepo.save(getEntity(CaptchaTypeEnum.IMAGE, CaptchaStatusEnum.VALID));
        captchaRepo.save(getEntity(CaptchaTypeEnum.IMAGE, CaptchaStatusEnum.INVALID));
        captchaRepo.save(getEntity(CaptchaTypeEnum.MATH, CaptchaStatusEnum.VALID));
        captchaRepo.save(getEntity(CaptchaTypeEnum.MATH, CaptchaStatusEnum.INVALID));
        captchaRepo.save(getEntity(CaptchaTypeEnum.MATH, CaptchaStatusEnum.INVALID));
        captchaRepo.save(getEntity(CaptchaTypeEnum.MATH, CaptchaStatusEnum.INVALID));
        captchaRepo.save(getEntity(CaptchaTypeEnum.IMAGE, CaptchaStatusEnum.NEW));
    }
}
