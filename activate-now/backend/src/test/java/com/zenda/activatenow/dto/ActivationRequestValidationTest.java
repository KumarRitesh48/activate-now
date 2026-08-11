package com.zenda.activatenow.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Directly validates the Bean Validation rules on ActivationRequest -
 * the source of truth for phone/PAN/email format rules, independent of
 * Spring's web layer or MockMvc.
 */
class ActivationRequestValidationTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void closeFactory() {
        factory.close();
    }

    private ActivationRequest validRequest() {
        ActivationRequest request = new ActivationRequest();
        request.setPhoneNumber("+919876543210");
        request.setPanNumber("EEAPS6789R");
        request.setNameAsInPan("KRISHNA KUMAR SINGH");
        request.setEmail("xyz@gmail.com");
        return request;
    }

    @Test
    void noViolations_whenAllFieldsAreValid() {
        Set<ConstraintViolation<ActivationRequest>> violations = validator.validate(validRequest());
        assertThat(violations).isEmpty();
    }

    // ---------- phoneNumber: +91 followed by exactly 10 digits ----------

    @ParameterizedTest
    @ValueSource(strings = {
            "+919876543210",   // valid
            "+911234567890"    // valid
    })
    void phoneNumber_accepted_whenFormatIsCorrect(String phone) {
        ActivationRequest request = validRequest();
        request.setPhoneNumber(phone);
        assertThat(violationsFor(request, "phoneNumber")).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "9876543210",        // missing +91
            "+91987654321",      // only 9 digits after +91
            "+9198765432100",    // 11 digits after +91
            "+92 9876543210",    // wrong country code
            "+91-9876543210",    // hyphen not allowed
            ""                    // empty
    })
    void phoneNumber_rejected_whenFormatIsIncorrect(String phone) {
        ActivationRequest request = validRequest();
        request.setPhoneNumber(phone);
        assertThat(violationsFor(request, "phoneNumber")).isNotEmpty();
    }

    // ---------- panNumber: 5 letters, 4 digits, 1 letter ----------

    @ParameterizedTest
    @ValueSource(strings = {"EEAPS6789R", "ABCDE1234F"})
    void panNumber_accepted_whenFormatIsCorrect(String pan) {
        ActivationRequest request = validRequest();
        request.setPanNumber(pan);
        assertThat(violationsFor(request, "panNumber")).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "eeaps6789r",     // lowercase - backend expects uppercase (frontend normalizes before sending)
            "EEAPS678R",       // only 3 digits
            "EEAP6789R",       // only 4 letters at start
            "EEAPS6789",       // missing trailing letter
            "123456789A",     // wrong pattern entirely
            ""
    })
    void panNumber_rejected_whenFormatIsIncorrect(String pan) {
        ActivationRequest request = validRequest();
        request.setPanNumber(pan);
        assertThat(violationsFor(request, "panNumber")).isNotEmpty();
    }

    // ---------- email: standard address ending in .com ----------

    @ParameterizedTest
    @ValueSource(strings = {"xyz@gmail.com", "john.doe@example.com", "a_b-c@sub.domain.com"})
    void email_accepted_whenFormatIsCorrect(String email) {
        ActivationRequest request = validRequest();
        request.setEmail(email);
        assertThat(violationsFor(request, "email")).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "xyz@gmail.in",    // wrong TLD per the stated rule (.com required)
            "xyz@gmail",        // no TLD
            "xyz.gmail.com",   // missing @
            "@gmail.com",       // missing local part
            ""
    })
    void email_rejected_whenFormatIsIncorrect(String email) {
        ActivationRequest request = validRequest();
        request.setEmail(email);
        assertThat(violationsFor(request, "email")).isNotEmpty();
    }

    // ---------- nameAsInPan: required ----------

    @Test
    void nameAsInPan_rejected_whenBlank() {
        ActivationRequest request = validRequest();
        request.setNameAsInPan("   ");
        assertThat(violationsFor(request, "nameAsInPan")).isNotEmpty();
    }

    @Test
    void nameAsInPan_rejected_whenTooShort() {
        ActivationRequest request = validRequest();
        request.setNameAsInPan("A");
        assertThat(violationsFor(request, "nameAsInPan")).isNotEmpty();
    }

    private Set<ConstraintViolation<ActivationRequest>> violationsFor(ActivationRequest request, String propertyName) {
        Set<ConstraintViolation<ActivationRequest>> all = validator.validate(request);
        all.removeIf(v -> !v.getPropertyPath().toString().equals(propertyName));
        return all;
    }
}
