package com.attendance.recognition.exception;

public class FaceRegistrationException extends RuntimeException {
    
    public FaceRegistrationException(String message) {
        super(message);
    }

    public FaceRegistrationException(String message, Throwable cause) {
        super(message, cause);
    }
}
