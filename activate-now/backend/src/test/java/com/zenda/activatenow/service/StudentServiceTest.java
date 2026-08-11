package com.zenda.activatenow.service;

import com.zenda.activatenow.dto.ActivationRequest;
import com.zenda.activatenow.dto.ActivationResponse;
import com.zenda.activatenow.dto.DashboardResponse;
import com.zenda.activatenow.exception.StudentAlreadyActivatedException;
import com.zenda.activatenow.exception.StudentNotFoundException;
import com.zenda.activatenow.model.Activation;
import com.zenda.activatenow.model.Student;
import com.zenda.activatenow.repository.ActivationRepository;
import com.zenda.activatenow.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for StudentService - the business logic layer.
 * Repositories are mocked so these tests run without a real database.
 */
@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private ActivationRepository activationRepository;

    @InjectMocks
    private StudentService studentService;

    private Student sampleStudent;

    @BeforeEach
    void setUp() {
        sampleStudent = new Student(
                "Delhi Public School",
                "Jessica John Jones",
                "FS1 Acacia",
                "https://i.pravatar.cc/150?img=47",
                new BigDecimal("340000.00"),
                0
        );
        sampleStudent.setId(1L);
    }

    // ---------- getDashboard ----------

    @Test
    void getDashboard_returnsDashboardData_whenStudentExists() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(sampleStudent));

        DashboardResponse response = studentService.getDashboard(1L);

        assertThat(response.getStudentId()).isEqualTo(1L);
        assertThat(response.getSchoolName()).isEqualTo("Delhi Public School");
        assertThat(response.getStudentName()).isEqualTo("Jessica John Jones");
        assertThat(response.getClassSection()).isEqualTo("FS1 Acacia");
        assertThat(response.getAnnualFee()).isEqualByComparingTo("340000.00");
        assertThat(response.isActivated()).isFalse();
    }

    @Test
    void getDashboard_throwsStudentNotFoundException_whenStudentDoesNotExist() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.getDashboard(99L))
                .isInstanceOf(StudentNotFoundException.class)
                .hasMessageContaining("99");

        verifyNoInteractions(activationRepository);
    }

    // ---------- activate ----------

    @Test
    void activate_savesActivationAndFlipsStudentFlag_whenStudentExists() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(sampleStudent));

        ActivationRequest request = new ActivationRequest();
        request.setPhoneNumber("+919876543210");
        request.setPanNumber("EEAPS6789R");
        request.setNameAsInPan("KRISHNA KUMAR SINGH");
        request.setEmail("xyz@gmail.com");

        ActivationResponse response = studentService.activate(1L, request);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.isActivated()).isTrue();

        // Verify the Activation entity was actually persisted with the right data
        ArgumentCaptor<Activation> activationCaptor = ArgumentCaptor.forClass(Activation.class);
        verify(activationRepository).save(activationCaptor.capture());
        Activation savedActivation = activationCaptor.getValue();
        assertThat(savedActivation.getStudentId()).isEqualTo(1L);
        assertThat(savedActivation.getPhoneNumber()).isEqualTo("+919876543210");
        assertThat(savedActivation.getPanNumber()).isEqualTo("EEAPS6789R");
        assertThat(savedActivation.getNameAsInPan()).isEqualTo("KRISHNA KUMAR SINGH");
        assertThat(savedActivation.getEmail()).isEqualTo("xyz@gmail.com");

        // Verify the student's "activated" flag was flipped and saved
        ArgumentCaptor<Student> studentCaptor = ArgumentCaptor.forClass(Student.class);
        verify(studentRepository).save(studentCaptor.capture());
        assertThat(studentCaptor.getValue().isActivated()).isTrue();
    }

    @Test
    void activate_throwsStudentNotFoundException_whenStudentDoesNotExist() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        ActivationRequest request = new ActivationRequest();
        request.setPhoneNumber("+919876543210");
        request.setPanNumber("EEAPS6789R");
        request.setNameAsInPan("KRISHNA KUMAR SINGH");
        request.setEmail("xyz@gmail.com");

        assertThatThrownBy(() -> studentService.activate(99L, request))
                .isInstanceOf(StudentNotFoundException.class);

        verify(activationRepository, never()).save(any());
        verify(studentRepository, never()).save(any());
    }

    @Test
    void activate_throwsStudentAlreadyActivatedException_whenStudentAlreadyActivated() {
        sampleStudent.setActivated(true);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(sampleStudent));

        ActivationRequest request = new ActivationRequest();
        request.setPhoneNumber("+919876543210");
        request.setPanNumber("EEAPS6789R");
        request.setNameAsInPan("KRISHNA KUMAR SINGH");
        request.setEmail("xyz@gmail.com");

        assertThatThrownBy(() -> studentService.activate(1L, request))
                .isInstanceOf(StudentAlreadyActivatedException.class)
                .hasMessageContaining("1");

        // A student who is already activated must never get a second Activation row,
        // enforcing the one-time-activation business rule at the service layer.
        verify(activationRepository, never()).save(any());
        verify(studentRepository, never()).save(any());
    }
}
