package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/auth/**").permitAll()
                .antMatchers("/students").authenticated()
                .anyRequest().permitAll()
                .and()
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); // ✅ Дозволяємо кукі та заголовки
        config.setAllowedOrigins(List.of("http://localhost:3000")); // ✅ Дозволяємо тільки frontend
        config.setAllowedHeaders(List.of("*")); // ✅ Дозволяємо всі заголовки
        config.setExposedHeaders(List.of("Authorization")); // ✅ Дозволяємо `Authorization`
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // ✅ Дозволяємо `OPTIONS`

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }


    /**
     * Що таке CORS (Cross-Origin Resource Sharing)?
     * CORS (Cross-Origin Resource Sharing) – це механізм безпеки браузера, який визначає чи дозволено фронтенду (http://localhost:3000) робити запити до бекенду (http://localhost:8081).
     *
     * 🔒 Проблема без CORS:
     * Коли фронтенд (http://localhost:3000) надсилає запит на бекенд (http://localhost:8081), браузер блокує запит через Same-Origin Policy.
     *
     * 🔓 CORS дозволяє такі запити, якщо сервер повертає відповідні заголовки.
     *
     * 🔍 Коли CORS потрібен?
     * Фронтенд і бекенд працюють на різних портах або доменах
     *
     * http://localhost:3000 (React/Vue/Angular)
     * http://localhost:8081 (Spring Boot)
     * 🔴 Без CORS браузер заблокує запит
     * ✅ З CORS бекенд повідомляє браузеру, що запит дозволено
     * API доступне з іншого домену
     *
     * Наприклад, бекенд api.myapp.com доступний для фронтенду frontend.myapp.com
     * CORS дозволяє запити тільки з довірених сайтів.
     * Захист від шкідливих скриптів
     *
     * CORS блокує несанкціоновані запити від інших сайтів, захищаючи дані користувачів.
     * 🔍 Як працює CORS у браузері?
     * Фронтенд відправляє запит на бекенд (http://localhost:8081)
     * http
     * Копіювати
     * Редагувати
     * GET /students HTTP/1.1
     * Host: localhost:8081
     * Origin: http://localhost:3000
     * Бекенд перевіряє Origin і додає CORS-заголовки у відповідь
     * http
     * Копіювати
     * Редагувати
     * HTTP/1.1 200 OK
     * Access-Control-Allow-Origin: http://localhost:3000
     * Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS
     * Access-Control-Allow-Headers: Content-Type, Authorization
     * Браузер дозволяє запит, якщо Access-Control-Allow-Origin містить http://localhost:3000.
     * 🔍 Як ми налаштували CORS у Spring Boot?
     * java
     * Копіювати
     * Редагувати
     * @Bean
     * public CorsFilter corsFilter() {
     *     UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
     *     CorsConfiguration config = new CorsConfiguration();
     *
     *     config.setAllowCredentials(true); // ✅ Дозволяємо кукі та токени
     *     config.setAllowedOrigins(List.of("http://localhost:3000")); // ✅ Дозволяємо тільки frontend
     *     config.setAllowedHeaders(List.of("*")); // ✅ Дозволяємо всі заголовки
     *     config.setExposedHeaders(List.of("Authorization")); // ✅ Дозволяємо `Authorization`
     *     config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // ✅ Дозволяємо всі HTTP-методи
     *
     *     source.registerCorsConfiguration("/**", config);
     *     return new CorsFilter(source);
     * }
     * 📌 Що тут відбувається?
     * Рядок	Що робить?
     * config.setAllowCredentials(true);	✅ Дозволяє авторизацію через кукі та токени
     * config.setAllowedOrigins(List.of("http://localhost:3000"));	✅ Дозволяє запити тільки з frontend
     * config.setAllowedHeaders(List.of("*"));	✅ Дозволяє всі заголовки
     * config.setExposedHeaders(List.of("Authorization"));	✅ Дозволяє клієнту бачити заголовок Authorization
     * config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));	✅ Дозволяє всі HTTP-методи
     * 🔍 Чому важливий OPTIONS?
     * Коли клієнт надсилає запит із нестандартними заголовками (Authorization, Content-Type тощо), браузер спочатку відправляє preflight-запит (OPTIONS):
     *
     * http
     * Копіювати
     * Редагувати
     * OPTIONS /students HTTP/1.1
     * Host: localhost:8081
     * Origin: http://localhost:3000
     * Access-Control-Request-Method: GET
     * Access-Control-Request-Headers: Authorization
     * Бекенд відповідає:
     *
     * http
     * Копіювати
     * Редагувати
     * HTTP/1.1 204 No Content
     * Access-Control-Allow-Origin: http://localhost:3000
     * Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS
     * Access-Control-Allow-Headers: Content-Type, Authorization
     * Тепер браузер дозволяє справжній GET-запит.
     *
     * 🔍 Висновок
     * Що таке CORS?	Навіщо він потрібен?
     * CORS – це механізм безпеки браузера	Дозволяє або блокує запити між різними доменами
     * Захищає від несанкціонованих запитів	Використовується для frontend ↔ backend API
     * Браузер спочатку робить OPTIONS-запит	Бекенд повинен правильно відповідати
     * Spring Boot може налаштувати CORS	Використовуючи CorsFilter або @CrossOrigin
     * 🚀 Після цих налаштувань браузер більше не блокуватиме запити між frontend (3000) і backend (8081).
     */
}