package com.attendance.recognition.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FaceMatchResult {
    private boolean isMatch;
    private double confidence;
}
