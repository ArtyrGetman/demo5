package com.example.demo.controller;

import com.example.demo.config.JwtUtil;
import com.example.demo.dao.UserRepository;
import com.example.demo.dao.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User inputUser) {
        System.out.println(" in controller");
        System.out.println(inputUser.getUserName());
        System.out.println(inputUser.getPassword());
        Optional<User> user = userRepository.findByUserName(inputUser.getUserName());
        if (user.isPresent() ) {
            String token = jwtUtil.generateToken(inputUser.getUserName());
            return ResponseEntity.ok(token);
        }else {
            User newUser = new User();
            newUser.setUserName(inputUser.getUserName());
            newUser.setPassword(passwordEncoder.encode(inputUser.getUserName()));
            newUser.setRole("USER");
            userRepository.save(newUser);
            String token = jwtUtil.generateToken(inputUser.getUserName());
            return ResponseEntity.ok(token);
        }

    }

}
