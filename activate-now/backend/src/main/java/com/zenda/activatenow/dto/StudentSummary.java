package com.zenda.activatenow.dto;

/**
 * Lightweight shape for GET /api/students - just enough to populate
 * a student picker/switcher in the frontend, without the full fee-widget data.
 */
public class StudentSummary {

    private Long id;
    private String studentName;
    private String schoolName;
    private boolean activated;

    public StudentSummary() {
    }

    public StudentSummary(Long id, String studentName, String schoolName, boolean activated) {
        this.id = id;
        this.studentName = studentName;
        this.schoolName = schoolName;
        this.activated = activated;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }
}
