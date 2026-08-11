package com.zenda.activatenow.repository;

import com.zenda.activatenow.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
}
