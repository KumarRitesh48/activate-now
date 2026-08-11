package com.zenda.activatenow.controller;

import com.zenda.activatenow.dto.ActivationResponse;
import com.zenda.activatenow.dto.DashboardResponse;
import com.zenda.activatenow.exception.StudentAlreadyActivatedException;
import com.zenda.activatenow.exception.StudentNotFoundException;
import com.zenda.activatenow.service.StudentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller-layer tests using MockMvc - verifies HTTP status codes, JSON
 * response shape, and validation error handling, without starting a real server.
 * StudentService is mocked so these tests focus purely on the REST/validation layer.
 */
@WebMvcTest(StudentController.class)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StudentService studentService;

    // ---------- GET /api/students/{id}/dashboard ----------

    @Test
    void getDashboard_returns200AndDashboardJson_whenStudentExists() throws Exception {
        DashboardResponse response = new DashboardResponse(
                1L, "Delhi Public School", "Jessica John Jones", "FS1 Acacia",
                "https://i.pravatar.cc/150?img=47", new BigDecimal("340000.00"), 0, false
        );
        when(studentService.getDashboard(1L)).thenReturn(response);

        mockMvc.perform(get("/api/students/1/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.studentId").value(1))
                .andExpect(jsonPath("$.schoolName").value("Delhi Public School"))
                .andExpect(jsonPath("$.studentName").value("Jessica John Jones"))
                .andExpect(jsonPath("$.classSection").value("FS1 Acacia"))
                .andExpect(jsonPath("$.annualFee").value(340000.00))
                .andExpect(jsonPath("$.activated").value(false));
    }

    @Test
    void getDashboard_returns404_whenStudentDoesNotExist() throws Exception {
        when(studentService.getDashboard(99L)).thenThrow(new StudentNotFoundException(99L));

        mockMvc.perform(get("/api/students/99/dashboard"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Student not found with id: 99"));
    }

    // ---------- POST /api/students/{id}/activate ----------

    @Test
    void activate_returns200_whenPayloadIsValid() throws Exception {
        when(studentService.activate(eq(1L), any()))
                .thenReturn(new ActivationResponse(true, "Activation successful", true));

        String payload = """
                {
                  "phoneNumber": "+919876543210",
                  "panNumber": "EEAPS6789R",
                  "nameAsInPan": "KRISHNA KUMAR SINGH",
                  "email": "xyz@gmail.com"
                }
                """;

        mockMvc.perform(post("/api/students/1/activate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.activated").value(true));
    }

    @Test
    void activate_returns400_whenPhoneNumberIsInvalid() throws Exception {
        String payload = """
                {
                  "phoneNumber": "9876543210",
                  "panNumber": "EEAPS6789R",
                  "nameAsInPan": "KRISHNA KUMAR SINGH",
                  "email": "xyz@gmail.com"
                }
                """;

        mockMvc.perform(post("/api/students/1/activate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errors.phoneNumber").exists());
    }

    @Test
    void activate_returns400_whenPanNumberIsInvalid() throws Exception {
        String payload = """
                {
                  "phoneNumber": "+919876543210",
                  "panNumber": "invalid-pan",
                  "nameAsInPan": "KRISHNA KUMAR SINGH",
                  "email": "xyz@gmail.com"
                }
                """;

        mockMvc.perform(post("/api/students/1/activate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.panNumber").exists());
    }

    @Test
    void activate_returns400_whenEmailIsInvalid() throws Exception {
        String payload = """
                {
                  "phoneNumber": "+919876543210",
                  "panNumber": "EEAPS6789R",
                  "nameAsInPan": "KRISHNA KUMAR SINGH",
                  "email": "not-an-email"
                }
                """;

        mockMvc.perform(post("/api/students/1/activate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.email").exists());
    }

    @Test
    void activate_returns400_whenRequiredFieldsAreMissing() throws Exception {
        String payload = "{}";

        mockMvc.perform(post("/api/students/1/activate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.phoneNumber").exists())
                .andExpect(jsonPath("$.errors.panNumber").exists())
                .andExpect(jsonPath("$.errors.nameAsInPan").exists())
                .andExpect(jsonPath("$.errors.email").exists());
    }

    @Test
    void activate_returns409_whenStudentAlreadyActivated() throws Exception {
        when(studentService.activate(eq(1L), any()))
                .thenThrow(new StudentAlreadyActivatedException(1L));

        String payload = """
                {
                  "phoneNumber": "+919876543210",
                  "panNumber": "EEAPS6789R",
                  "nameAsInPan": "KRISHNA KUMAR SINGH",
                  "email": "xyz@gmail.com"
                }
                """;

        mockMvc.perform(post("/api/students/1/activate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false));
    }
}
