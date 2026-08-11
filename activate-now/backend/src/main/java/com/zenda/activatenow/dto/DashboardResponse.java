package com.zenda.activatenow.dto;

import java.math.BigDecimal;

/**
 * Shape returned by GET /api/students/{id}/dashboard - everything the
 * Dashboard screen needs: profile card + fee widget, in one call.
 */
public class DashboardResponse {

    private Long studentId;
    private String schoolName;
    private String studentName;
    private String classSection;
    private String profilePhotoUrl;
    private BigDecimal annualFee;
    private Integer interestRatePercent;
    private boolean activated;

    public DashboardResponse() {
    }

    public DashboardResponse(Long studentId, String schoolName, String studentName, String classSection,
                              String profilePhotoUrl, BigDecimal annualFee, Integer interestRatePercent,
                              boolean activated) {
        this.studentId = studentId;
        this.schoolName = schoolName;
        this.studentName = studentName;
        this.classSection = classSection;
        this.profilePhotoUrl = profilePhotoUrl;
        this.annualFee = annualFee;
        this.interestRatePercent = interestRatePercent;
        this.activated = activated;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
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
