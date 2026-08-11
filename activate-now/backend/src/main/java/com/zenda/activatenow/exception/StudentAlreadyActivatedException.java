package com.zenda.activatenow.exception;

public class StudentAlreadyActivatedException extends RuntimeException {
    public StudentAlreadyActivatedException(Long studentId) {
        super("Student with id " + studentId + " is already activated");
    }
}
