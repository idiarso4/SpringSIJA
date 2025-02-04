package com.attendance.recognition.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FaceData {
    private byte[] faceBytes;
    private double[] features;
}
