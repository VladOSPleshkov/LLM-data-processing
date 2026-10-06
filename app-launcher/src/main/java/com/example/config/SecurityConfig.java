package com.example.config;

import com.example.security.JwtAuthentificationFilter;
import com.example.security.CustomAuthentificationEntryPoint;
import com.example.security.CustomAccessDeniedHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Включает аннотации @PreAuthorize @PostAuthorize над методами
public class SecurityConfig {

    private final JwtAuthentificationFilter jwtAuthentificationFilter;
    private final CustomAuthentificationEntryPoint authEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    public SecurityConfig(JwtAuthentificationFilter jwtAuthentificationFilter,
                          CustomAuthentificationEntryPoint authEntryPoint,
                          CustomAccessDeniedHandler accessDeniedHandler) {
        this.jwtAuthentificationFilter = jwtAuthentificationFilter;
        this.authEntryPoint = authEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }
    @Bean(name = "mvcHandlerMappingIntrospector")
    public HandlerMappingIntrospector mvcHandlerMappingIntrospector() {
        return new HandlerMappingIntrospector();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // для REST API на JWT с авторизацией через Bearer-токен CSRF не требуется
                .csrf(AbstractHttpConfigurer::disable)

                //Настраиваем Stateless-режим (без создания HTTP-сессий на сервере)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // кастомные обработчики ошибок 401 Unauthorized и 403 Forbidden
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((AuthenticationEntryPoint) authEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        // Разграничение прав доступа
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/tasks/**", "/api/v1/reviews/**").hasAnyRole("MANAGER", "ADMIN")
                        // Все остальные эндпоинты требуют аутентификации
                        .anyRequest().authenticated()
                )

                // кастомный JWT-фильтр перед стандартным фильтром логина по паролю
                .addFilterBefore(jwtAuthentificationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}

