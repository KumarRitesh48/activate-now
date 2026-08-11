package com.zenda.activatenow.controller;

import com.zenda.activatenow.dto.ActivationRequest;
import com.zenda.activatenow.dto.ActivationResponse;
import com.zenda.activatenow.dto.DashboardResponse;
import com.zenda.activatenow.dto.StudentSummary;
import com.zenda.activatenow.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API backing the "Activate Now" flow.
 *
 * GET  /api/students                 -> list all students (for the student picker)
 * GET  /api/students/{id}/dashboard  -> dashboard screen data (profile + fee widget)
 * POST /api/students/{id}/activate   -> submit the "Activate Now" modal form
 */
@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = {"http://localhost:4200"})
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public List<StudentSummary> getAllStudents() {
        return studentService.getAllStudents();
    }

    @GetMapping("/{id}/dashboard")
    public DashboardResponse getDashboard(@PathVariable Long id) {
        return studentService.getDashboard(id);
    }

    @PostMapping("/{id}/activate")
    public ActivationResponse activate(@PathVariable Long id, @Valid @RequestBody ActivationRequest request) {
        return studentService.activate(id, request);
    }
}
