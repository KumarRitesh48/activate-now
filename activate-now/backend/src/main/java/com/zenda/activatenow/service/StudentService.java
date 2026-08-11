package com.zenda.activatenow.service;

import com.zenda.activatenow.dto.ActivationRequest;
import com.zenda.activatenow.dto.ActivationResponse;
import com.zenda.activatenow.dto.DashboardResponse;
import com.zenda.activatenow.dto.StudentSummary;
import com.zenda.activatenow.exception.StudentAlreadyActivatedException;
import com.zenda.activatenow.exception.StudentNotFoundException;
import com.zenda.activatenow.model.Activation;
import com.zenda.activatenow.model.Student;
import com.zenda.activatenow.repository.ActivationRepository;
import com.zenda.activatenow.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final ActivationRepository activationRepository;

    public StudentService(StudentRepository studentRepository, ActivationRepository activationRepository) {
        this.studentRepository = studentRepository;
        this.activationRepository = activationRepository;
    }

    // Backs the student picker in the frontend - lightweight list, not full dashboard data.
    public List<StudentSummary> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(s -> new StudentSummary(s.getId(), s.getStudentName(), s.getSchoolName(), s.isActivated()))
                .toList();
    }

    public DashboardResponse getDashboard(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));

        return new DashboardResponse(
                student.getId(),
                student.getSchoolName(),
                student.getStudentName(),
                student.getClassSection(),
                student.getProfilePhotoUrl(),
                student.getAnnualFee(),
                student.getInterestRatePercent(),
                student.isActivated()
        );
    }

    @Transactional
    public ActivationResponse activate(Long studentId, ActivationRequest request) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));

        // Business rule: "Activate Now" is a one-time action per student.
        // Also backed by the DB-level unique constraint on activations.student_id.
        if (student.isActivated()) {
            throw new StudentAlreadyActivatedException(studentId);
        }

        Activation activation = new Activation();
        activation.setStudent(student);
        activation.setPhoneNumber(request.getPhoneNumber());
        activation.setPanNumber(request.getPanNumber());
        activation.setNameAsInPan(request.getNameAsInPan());
        activation.setEmail(request.getEmail());
        activationRepository.save(activation);

        student.setActivated(true);
        studentRepository.save(student);

        return new ActivationResponse(true, "Activation successful", true);
    }
}
