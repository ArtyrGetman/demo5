package com.example.demo.controller;

import com.example.demo.dao.StudentRepository;
import com.example.demo.dao.entity.Student;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentRepository repository;

    @GetMapping
    public ResponseEntity<?> getAllStudents() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            log.info("Користувач НЕ АВТОРИЗОВАНИЙ. Доступ до /students заборонений!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Необхідна авторизація!");
        }
        log.info("✅ Користувач {} отримує список студентів", auth.getName());
        List<Student> students = repository.findAll();
        return ResponseEntity.ok(students);
    }

    @PostMapping
    public ResponseEntity<Student> addStudent(@RequestBody Student student) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            log.info("Користувач НЕ АВТОРИЗОВАНИЙ. Доступ до /students заборонений!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        log.info("✅ Користувач {} отримує список студентів", auth.getName());
        Student studentForSave = student;
        repository.save(student);
        return ResponseEntity.status(HttpStatus.CREATED).body(studentForSave);
    }



}
