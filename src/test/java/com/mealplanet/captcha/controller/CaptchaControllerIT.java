package com.mealplanet.captcha.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mealplanet.captcha.model.enums.CaptchaTypeEnum;
import com.mealplanet.captcha.model.request.CaptchaValidationRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CaptchaControllerIT {

    private static final String OUTPUT = "captcha/output";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        truncateOutputFolder();
    }

    @AfterEach
    void tearDown() {
        truncateOutputFolder();
    }

    private void truncateOutputFolder() {
        File file = new File(OUTPUT);
        if (!file.exists()) {
            file.mkdirs();
            return;
        }
        File[] files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                f.delete();
            }
        }
    }

    @Test
    @DisplayName("Generate IMAGE captcha -> fetch image -> validate with wrong answer -> further image fetch fails")
    void givenWrongAnswer_whenFullCaptchaFlow_thenReturnFalse() throws Exception {
        String generateResponse = mockMvc.perform(post("/api/v1/captcha/generate").param("type", CaptchaTypeEnum.IMAGE.name()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        JsonNode json = objectMapper.readTree(generateResponse);
        UUID id = UUID.fromString(json.get("id").asText());
        int expiresIn = json.get("expires_in").asInt();
        assertThat(expiresIn).isGreaterThan(0);

        File outputDirectory = new File(OUTPUT);
        List<File> files = Arrays.stream(outputDirectory.listFiles()).filter(File::isFile).toList();
        assertThat(files).hasSize(1);
        assertThat(files.get(0).length()).isGreaterThan(0);

        byte[] imageBytes = mockMvc.perform(get("/api/v1/captcha/" + id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG))
                .andReturn().getResponse().getContentAsByteArray();
        assertThat(imageBytes.length).isGreaterThan(0);

        CaptchaValidationRequest request = new CaptchaValidationRequest("WRONG");
        String validateResponse = mockMvc.perform(post("/api/v1/captcha/validate/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        JsonNode validateJson = objectMapper.readTree(validateResponse);
        assertThat(validateJson.get("valid").asBoolean()).isFalse();

        mockMvc.perform(get("/api/v1/captcha/" + id))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("When type param is missing or invalid -> 400 with error body")
    void generate_missingOrInvalidType_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/captcha/generate"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Missing required parameter: type"));

        mockMvc.perform(post("/api/v1/captcha/generate").param("type", "FOO"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Invalid value for parameter: type. Allowed values are: " + Arrays.toString(CaptchaTypeEnum.values())));
    }
}
