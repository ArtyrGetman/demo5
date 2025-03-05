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

@Configuration
//Позначає цей клас як конфігураційний, що дозволяє Spring автоматично створювати та налаштовувати Bean-и.
@EnableWebSecurity // Увімкнення Spring Security у цьому застосунку.
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter; //Це наш власний фільтр, який перевіряє JWT-токен у кожному запиті.


    @Bean//  Метод повертає SecurityFilterChain, який буде керувати безпекою всього застосунку.
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/auth").permitAll()        // Дозволяємо доступ до `/auth` (реєстрація, логін)
                .antMatchers("/students").authenticated() //         //  Захищаємо `/students` (доступ тільки з токеном)
                .anyRequest().permitAll() //         ✅ Всі інші запити дозволяємо без авторизації
                .and()
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        //   addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
        //Додає наш кастомний JWT-фільтр JwtFilter перед стандартним фільтром логіну Spring Security.
         // Це дозволяє перевіряти JWT у кожному запиті до обробки логіну.

        //authorizeRequests() → Починаємо налаштування доступу до URL.
        //antMatchers("/auth").permitAll() →
        // Будь-хто може звертатися до /auth (реєстрація та логін без авторизації).
        //antMatchers("/students").authenticated() →
        // Доступ до /students тільки для автентифікованих користувачів.
        //anyRequest().permitAll() →
        // Інші маршрути відкриті для всіх (не потребують авторизації).
        return http.build();
    }


    //PasswordEncoder → Додає BCryptPasswordEncoder для захищеного хешування паролів.
    // BCryptPasswordEncoder – це надійний алгоритм хешування паролів.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Підсумок
     * Рядок	Що робить?
     * @Configuration Позначає клас як конфігураційний для Spring
     * @EnableWebSecurity Вмикає Spring Security
     * @Autowired private JwtFilter jwtFilter;	Впроваджує фільтр JwtFilter для перевірки JWT
     * @Bean public SecurityFilterChain securityFilterChain(HttpSecurity http)	Головна конфігурація безпеки
     * http.csrf().disable()	Вимикає CSRF-захист (бо використовуємо JWT)
     * .authorizeRequests()	Починаємо налаштування авторизації
     * .antMatchers("/auth").permitAll()	Дозволяємо доступ до /auth без авторизації
     * .antMatchers("/students").authenticated()	Доступ до /students тільки для автентифікованих
     * .anyRequest().permitAll()	Всі інші запити дозволені
     * .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)	Додаємо наш JwtFilter для перевірки токенів
     * @Bean public PasswordEncoder passwordEncoder()	Додаємо BCryptPasswordEncoder для шифрування паролів
     */

    /**
     *  Як працює CSRF-атака?
     * Користувач входить у bank.com та отримує SESSION_ID (авторизований).
     * Хакер створює фейковий сайт (evil.com), який надсилає запит:
     * html
     * Копіювати
     * Редагувати
     * <img src="http://bank.com/transfer?amount=1000&to=hacker" />
     * Користувач заходить на evil.com, і браузер автоматично додає SESSION_ID до запиту.
     * Банк приймає запит, бо він прийшов від авторизованого користувача.
     * Гроші втрачені.
     * ⚠ Основна небезпека: атака працює, якщо користувач вже залогінений і сесія активна.
     * 🔹 Браузер автоматично додає кукі (SESSION_ID) без перевірки походження.
     *
     * 🔹 Як Spring Security захищає від CSRF?
     * За замовчуванням, Spring Security додає CSRF-токен до всіх POST, PUT, DELETE запитів.
     *
     * При завантаженні сторінки сервер додає CSRF-токен у meta або hidden input.
     * Під час запиту браузер має передати CSRF-токен разом із SESSION_ID.
     * Сервер перевіряє, чи CSRF-токен правильний.
     * Якщо токен відсутній або неправильний → запит блокується.
     * 🔹 Чому ми вимикаємо csrf().disable()?
     * java
     * Копіювати
     * Редагувати
     * http.csrf().disable()
     * ❌ Ми вимикаємо CSRF, тому що використовуємо JWT-токени.
     *
     * Причина:
     *
     * CSRF актуальний тільки для сесійних кукі, а JWT не використовує сесію.
     * У JWT токен передається в Authorization заголовку, а не через кукі.
     * ✅ Коли НЕ потрібно вимикати CSRF?
     *
     * Якщо застосунок використовує сесійну авторизацію (SESSION_ID).
     * Якщо форми надсилають дані через POST (HTML-форми).
     * ✅ Коли можна вимкнути CSRF?
     *
     * Якщо використовуємо JWT-токени у заголовках (Bearer Token).
     * Якщо клієнтська частина працює окремо (SPA, React, Angular, Vue).
     * 🔹 Висновок
     * Ситуація	Потрібно CSRF?
     * Сесійну авторизацію (SESSION_ID) використовуємо	✅ Так
     * JWT через заголовки (Authorization: Bearer)	❌ Ні
     * Тільки REST API (немає форм у HTML)	❌ Ні
     * Форми надсилають POST-запити (HTML)	✅ Так
     * 🔒 Якщо не використовуємо JWT, CSRF краще НЕ вимикати.
     * 🔥 Якщо використовуємо JWT, CSRF можна вимкнути, бо браузер не передає автоматично токен у запитах. 🚀
     */
}