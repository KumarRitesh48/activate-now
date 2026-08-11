package com.zenda.activatenow.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Represents a single "Activate Now" submission captured from the modal form:
 * Phone Number, PAN Card Number, Name as in PAN Card, Email.
 *
 * One-to-one with Student: a student can only ever have one Activation record
 * (enforced by the unique constraint on student_id), matching the business rule
 * that "Activate Now" is a one-time action per student.
 */
@Entity
@Table(name = "activations")
public class Activation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false, unique = true)
    private Student student;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String panNumber;

    @Column(nullable = false)
    private String nameAsInPan;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private LocalDateTime submittedAt = LocalDateTime.now();

    public Activation() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    // Convenience accessor - avoids callers needing to null-check/traverse the
    // relation just to log or compare the id.
    public Long getStudentId() {
        return student != null ? student.getId() : null;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPanNumber() {
        return panNumber;
    }

    public void setPanNumber(String panNumber) {
        this.panNumber = panNumber;
    }

    public String getNameAsInPan() {
        return nameAsInPan;
    }

    public void setNameAsInPan(String nameAsInPan) {
        this.nameAsInPan = nameAsInPan;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }
}
