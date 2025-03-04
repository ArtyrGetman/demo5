package com.example.demo.config;

import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    // ✅ Передаємо залежності через конструктор (без @Autowired)
    public JwtFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        // ✅ Отримуємо `Authorization` заголовок з HTTP-запиту
        String authHeader = request.getHeader("Authorization");
        // ✅ Перевіряємо, чи є заголовок та чи починається з "Bearer "
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // Видаляємо "Bearer "
            if (!token.equals("null") &&  !token.isEmpty()) {
                String username = jwtUtil.extractUserName(token); // Отримуємо username з токена
                log.info("JWT токен отримано. Username: {}", username);
                // Якщо токен дійсний, додаємо користувача у SecurityContextHolder
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    if (jwtUtil.validateToken(token, userDetails.getUsername())) {
                        SecurityContextHolder.getContext().setAuthentication(
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()));
                    } else {
                        log.info("JWT токен недійсний для користувача: {}", username);
                    }
                }
            }else {
                log.info("Authorization заголовок відсутній або неправильний формат");
            }
        }else {
            log.info("Authorization заголовок відсутній або неправильний формат");

        }
        chain.doFilter(request, response); // Передаємо запит далі
    }
}