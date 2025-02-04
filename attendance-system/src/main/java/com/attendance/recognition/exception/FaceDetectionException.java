package com.attendance.recognition.exception;

public class FaceDetectionException extends RuntimeException {
    
    public FaceDetectionException(String message) {
        super(message);
    }

    public FaceDetectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
