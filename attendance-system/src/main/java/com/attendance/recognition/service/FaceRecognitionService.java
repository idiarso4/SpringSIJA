package com.attendance.recognition.service;

import com.attendance.recognition.model.FaceData;
import com.attendance.recognition.model.FaceMatchResult;
import lombok.extern.slf4j.Slf4j;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.face.LBPHFaceRecognizer;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class FaceRecognitionService {

    private CascadeClassifier faceDetector;
    private LBPHFaceRecognizer faceRecognizer;
    private static final double CONFIDENCE_THRESHOLD = 80.0;
    private static final String TEMP_DIR = "temp";
    private static final String CASCADE_FILE = "haarcascade_frontalface_default.xml";

    @PostConstruct
    public void init() {
        // Load OpenCV native library
        nu.pattern.OpenCV.loadLocally();

        // Initialize face detector
        String cascadePath = getClass().getResource("/" + CASCADE_FILE).getPath();
        faceDetector = new CascadeClassifier(cascadePath);
        
        // Initialize face recognizer
        faceRecognizer = LBPHFaceRecognizer.create();

        // Create temp directory if it doesn't exist
        new File(TEMP_DIR).mkdirs();
    }

    public FaceData processFaceImage(MultipartFile file) throws IOException {
        String tempFilePath = saveToTemp(file);
        Mat image = Imgcodecs.imread(tempFilePath);
        
        if (image.empty()) {
            throw new IOException("Failed to read image");
        }

        // Convert to grayscale
        Mat grayImage = new Mat();
        Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);

        // Detect faces
        MatOfRect faces = new MatOfRect();
        faceDetector.detectMultiScale(grayImage, faces);

        if (faces.empty()) {
            throw new IllegalArgumentException("No face detected in the image");
        }

        if (faces.toArray().length > 1) {
            throw new IllegalArgumentException("Multiple faces detected. Please provide an image with a single face");
        }

        // Get the detected face
        Rect faceRect = faces.toArray()[0];
        Mat face = new Mat(grayImage, faceRect);

        // Resize face to standard size
        Mat resizedFace = new Mat();
        Size size = new Size(150, 150);
        Imgproc.resize(face, resizedFace, size);

        // Convert face matrix to byte array for storage
        MatOfByte mob = new MatOfByte();
        Imgcodecs.imencode(".jpg", resizedFace, mob);
        byte[] faceBytes = mob.toArray();

        // Cleanup
        cleanupTempFile(tempFilePath);

        return new FaceData(faceBytes, extractFeatures(resizedFace));
    }

    public FaceMatchResult matchFace(byte[] storedFaceData, MultipartFile newImage) throws IOException {
        // Process new image
        FaceData newFaceData = processFaceImage(newImage);

        // Compare faces
        double confidence = compareFaces(storedFaceData, newFaceData.getFaceBytes());

        boolean isMatch = confidence >= CONFIDENCE_THRESHOLD;
        return new FaceMatchResult(isMatch, confidence);
    }

    private double compareFaces(byte[] face1Data, byte[] face2Data) {
        // Convert byte arrays back to Mat objects
        Mat face1 = Imgcodecs.imdecode(new MatOfByte(face1Data), Imgcodecs.IMREAD_GRAYSCALE);
        Mat face2 = Imgcodecs.imdecode(new MatOfByte(face2Data), Imgcodecs.IMREAD_GRAYSCALE);

        // Extract and compare features
        Mat hist1 = calculateHistogram(face1);
        Mat hist2 = calculateHistogram(face2);

        // Compare histograms using correlation method
        double correlation = Imgproc.compareHist(hist1, hist2, Imgproc.HISTCMP_CORREL);
        
        // Convert correlation to percentage (0-100)
        return correlation * 100;
    }

    private Mat calculateHistogram(Mat image) {
        Mat hist = new Mat();
        MatOfInt histSize = new MatOfInt(256);
        MatOfFloat ranges = new MatOfFloat(0f, 256f);
        MatOfInt channels = new MatOfInt(0);

        Imgproc.calcHist(
            List.of(image), 
            channels, 
            new Mat(), 
            hist, 
            histSize, 
            ranges
        );

        Core.normalize(hist, hist, 0, 1, Core.NORM_MINMAX);
        return hist;
    }

    private double[] extractFeatures(Mat face) {
        Mat hist = calculateHistogram(face);
        double[] features = new double[(int) (hist.total() * hist.channels())];
        hist.get(0, 0, features);
        return features;
    }

    private String saveToTemp(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID().toString() + ".jpg";
        Path tempPath = Path.of(TEMP_DIR, fileName);
        Files.copy(file.getInputStream(), tempPath);
        return tempPath.toString();
    }

    private void cleanupTempFile(String filePath) {
        try {
            Files.deleteIfExists(Path.of(filePath));
        } catch (IOException e) {
            log.error("Failed to delete temp file: " + filePath, e);
        }
    }
}
