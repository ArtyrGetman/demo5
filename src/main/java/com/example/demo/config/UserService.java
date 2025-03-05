package com.example.demo.config;

import com.example.demo.dao.UserRepository;
import com.example.demo.dao.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUserName(username).orElseThrow(()-> new UsernameNotFoundException("Not found"));
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUserName())
                .password(user.getPassword())
                .roles(user.getRole())
                .build();
    }

    /**
     * UserDetailsService – це інтерфейс Spring Security, який використовується для завантаження користувачів із бази даних під час автентифікації.
     *
     * 🔹 Основна роль:
     * Отримує користувача за username (логін).
     * Повертає об'єкт UserDetails, який містить інформацію для перевірки автентифікації.
     * Використовується Spring Security у процесі входу.
     * 🔍 Як працює цей метод?
     * 📌 Розбираємо код рядок за рядком
     * java
     * Копіювати
     * Редагувати
     * @Override
     * public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
     * Метод loadUserByUsername – стандартний метод UserDetailsService, який Spring Security викликає при логіні.
     * Параметр username – значення, яке передав користувач при авторизації.
     * Якщо користувач не знайдений, кидаємо UsernameNotFoundException.
     * java
     * Копіювати
     * Редагувати
     * User user = userRepository.findByUserName(username)
     *         .orElseThrow(() -> new UsernameNotFoundException("Not found"));
     * 🔹 Виконуємо пошук користувача у базі через userRepository.findByUserName(username).
     * 🔹 Якщо користувача немає в базі, кидаємо помилку UsernameNotFoundException, і Spring Security автоматично поверне 401 Unauthorized.
     * java
     * Копіювати
     * Редагувати
     * return org.springframework.security.core.userdetails.User
     *         .withUsername(user.getUserName()) // ✅ Встановлюємо логін користувача
     *         .password(user.getPassword()) // 🔑 Передаємо пароль (він вже захешований у БД)
     *         .roles(user.getRole()) // 🛡️ Додаємо ролі (ROLE_USER, ROLE_ADMIN)
     *         .build();
     * 🔹 Використовуємо User.withUsername() для створення об'єкта UserDetails.
     * 🔹 Передаємо пароль із бази, бо Spring Security його перевірятиме під час входу.
     * 🔹 Встановлюємо ролі (roles(user.getRole())).
     * 📌 UserDetails – це стандартний об'єкт Spring Security, який використовується для перевірки автентифікації та авторизації.
     *
     * 🔍 Чому ми використовуємо withUsername().password().roles()?
     * Spring Security очікує UserDetails, тому ми використовуємо User.withUsername().
     * Передаємо пароль, який вже захешований у базі (наприклад, через BCrypt).
     * Додаємо ролі, щоб Spring Security міг керувати доступом (ROLE_USER, ROLE_ADMIN).
     * Використовуємо build(), щоб створити повноцінний об'єкт UserDetails.
     * 🔍 Як це використовується у Spring Security?
     * 📌 В SecurityConfig реєструємо UserDetailsService:
     *
     * java
     * Копіювати
     * Редагувати
     * @Bean
     * public UserDetailsService userDetailsService() {
     *     return new MyUserDetailsService(); // Використовуємо наш кастомний сервіс
     * }
     *
     * @Bean
     * public AuthenticationManager authenticationManager(
     *         AuthenticationConfiguration authenticationConfiguration) throws Exception {
     *     return authenticationConfiguration.getAuthenticationManager();
     * }
     * 🔥 Тепер Spring Security буде автоматично викликати loadUserByUsername() під час логіну. 🚀
     *
     * 🔍 Висновок
     * Що робить?	Як працює?
     * Завантажує користувача з бази	userRepository.findByUserName(username)
     * Якщо користувача немає	Кидає UsernameNotFoundException
     * Повертає UserDetails	Використовує User.withUsername().password().roles().build()
     * Використовується в Spring Security	Автоматично підключається для автентифікації
     * ✅ Без UserDetailsService Spring Security не зможе перевірити логін і пароль користувача.
     * ✅ Цей метод – основа авторизації в Spring Security. 🚀
     */
}
