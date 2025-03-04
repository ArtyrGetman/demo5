package com.example.demo.dao;

import com.example.demo.dao.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;


@Component
public interface StudentRepository extends JpaRepository<Student, Long> {}
