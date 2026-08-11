package com.zenda.activatenow.repository;

import com.zenda.activatenow.model.Activation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ActivationRepository extends JpaRepository<Activation, Long> {

    @Query("SELECT a FROM Activation a WHERE a.student.id = :studentId")
    List<Activation> findByStudentId(@Param("studentId") Long studentId);
}
