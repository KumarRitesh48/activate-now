package com.zenda.activatenow.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Represents the student/parent dashboard profile shown on the Dashboard screen:
 * school name, student name, class/section, profile photo, and the annual fee
 * that backs the "Activate Now" fee-financing widget.
 */
@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String schoolName;

    @Column(nullable = false)
    private String studentName;

    // e.g. "FS1 Acacia" as shown under the student name in Figma
    @Column(nullable = false)
    private String classSection;

    private String profilePhotoUrl;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal annualFee;

    @Column(nullable = false)
    private Integer interestRatePercent = 0;

    // Whether the "Activate Now" flow has already been completed for this student
    @Column(nullable = false)
    private boolean activated = false;

    public Student() {
    }

    public Student(String schoolName, String studentName, String classSection,
                   String profilePhotoUrl, BigDecimal annualFee, Integer interestRatePercent) {
        this.schoolName = schoolName;
        this.studentName = studentName;
        this.classSection = classSection;
        this.profilePhotoUrl = profilePhotoUrl;
        this.annualFee = annualFee;
        this.interestRatePercent = interestRatePercent;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getClassSection() {
        return classSection;
    }

    public void setClassSection(String classSection) {
        this.classSection = classSection;
    }

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }

    public BigDecimal getAnnualFee() {
        return annualFee;
    }

    public void setAnnualFee(BigDecimal annualFee) {
        this.annualFee = annualFee;
    }

    public Integer getInterestRatePercent() {
        return interestRatePercent;
    }

    public void setInterestRatePercent(Integer interestRatePercent) {
        this.interestRatePercent = interestRatePercent;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }
}
