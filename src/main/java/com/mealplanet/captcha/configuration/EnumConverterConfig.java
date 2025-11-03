package com.mealplanet.captcha.configuration;

import com.mealplanet.captcha.model.enums.CaptchaTypeEnum;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class EnumConverterConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new CaptchaTypeConverter());
    }

    static class CaptchaTypeConverter implements Converter<String, CaptchaTypeEnum> {
        @Override
        public CaptchaTypeEnum convert(String source) {
            try {
                return CaptchaTypeEnum.valueOf(source.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid captcha type: " + source);
            }
        }
    }
}
