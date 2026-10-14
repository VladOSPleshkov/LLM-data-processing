package com.shared.security.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.shared.security.CustomAccessDeniedHandler;
import com.shared.security.CustomAuthenticationEntryPoint;
import com.shared.security.JwtAuthenticationFilter;
import com.shared.security.JwtTokenValidator;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Slf4j
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass(SecurityFilterChain.class)
public class SharedSecurityAutoConfiguration {

    @Bean
    public JwtTokenValidator jwtTokenValidator(@Value("${jwt.secret}") String secret, Environment environment) {

        if (Arrays.asList(environment.getActiveProfiles()).contains("test")) {
            log.info("Тестовый профиль: Генерация случайного криптографического ключа в памяти для HMAC-SHA");
            SecretKey randomKey = Jwts.SIG.HS256.key().build();
            return new JwtTokenValidator(randomKey); // Передаем готовый безопасный объект
        }

        // Для продакшена/докера — строго требуем надежный ключ извне
        if (secret == null || secret.trim().isBlank() || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException("КРИТИЧЕСКАЯ ОШИБКА: Для работы в прод-режиме необходим JWT_SECRET минимум 32 байта!");
        }
        return new JwtTokenValidator(secret);

    }

//    @Bean
//    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtTokenValidator jwtTokenValidator, HandlerExceptionResolver handlerExceptionResolver) {
//        return new JwtAuthenticationFilter(jwtTokenValidator, handlerExceptionResolver);
//    }
    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilterRegistration(JwtAuthenticationFilter filter) {
        FilterRegistrationBean<JwtAuthenticationFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    public CustomAuthenticationEntryPoint customAuthenticationEntryPoint(ObjectMapper objectMapper) {
        return new CustomAuthenticationEntryPoint(objectMapper);
    }

    @Bean
    public CustomAccessDeniedHandler customAccessDeniedHandler(ObjectMapper objectMapper) {
        return new CustomAccessDeniedHandler(objectMapper);
    }
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(
            JwtTokenValidator jwtTokenValidator,
            HandlerExceptionResolver handlerExceptionResolver) { // Внедряем резолвер спринга
        return new JwtAuthenticationFilter(jwtTokenValidator, handlerExceptionResolver);
    }
}