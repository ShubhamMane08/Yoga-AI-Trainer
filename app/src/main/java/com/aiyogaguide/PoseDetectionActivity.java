
package com.aiyogaguide;

import static com.aiyogaguide.YogaPoseChecker.checkPose;
import static java.lang.Math.atan2;
import static com.aiyogaguide.PoseGraphic.correctPose;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseDetection;
import com.google.mlkit.vision.pose.PoseDetector;
import com.google.mlkit.vision.pose.PoseLandmark;
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PoseDetectionActivity extends AppCompatActivity {

    String yogaPose;
    int count = 0;
    private PreviewView previewView;
    private GraphicOverlay graphicOverlay;
    TextView text, title, timing;
    private static final int REQUEST_CAMERA_PERMISSION = 1001;
    boolean showGraphicOverlay = true;

    @Override
    protected void onStart() {
        super.onStart();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            startCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pose_detection);

        // INIT
        title = findViewById(R.id.title);
        timing = findViewById(R.id.timing);
        previewView = findViewById(R.id.viewFinder);
        graphicOverlay = findViewById(R.id.graphicOverlay);

        text = findViewById(R.id.text);

        Intent intent = getIntent();
        yogaPose = intent.getStringExtra("poseName");
        title.setText(yogaPose);

        startCamera();
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                // Preview
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                // Camera Selector
                CameraSelector cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;

                // Image Analysis
                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), this::analyzeImage);

                // Unbind all use cases before rebinding
                cameraProvider.unbindAll();

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);

            } catch (ExecutionException | InterruptedException e) {
                Toast.makeText(this, "Error starting camera: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    @SuppressLint("SetTextI18n")
    private void analyzeImage(ImageProxy imageProxy) {
        // Get rotation degrees
        int rotationDegrees = imageProxy.getImageInfo().getRotationDegrees();
        boolean isImageFlipped = true;

        // Set image source info based on rotation
        if (rotationDegrees == 0 || rotationDegrees == 180) {
            graphicOverlay.setImageSourceInfo(imageProxy.getWidth(), imageProxy.getHeight(), isImageFlipped);
        } else {
            graphicOverlay.setImageSourceInfo(imageProxy.getHeight(), imageProxy.getWidth(), isImageFlipped);
        }

        // Process image using ML Kit Pose Detection
        @SuppressLint("UnsafeOptInUsageError")
        InputImage image = InputImage.fromMediaImage(Objects.requireNonNull(imageProxy.getImage()), rotationDegrees);

        // Initialize Pose Detector with options
        PoseDetectorOptions options = new PoseDetectorOptions.Builder()
                .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
                .build();
        PoseDetector poseDetector = PoseDetection.getClient(options);

        // Process the image on a background thread
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> poseDetector.process(image)
                .addOnSuccessListener(pose -> {
                    // Update UI on the main thread
                    runOnUiThread(() -> {
                        // Check if any pose landmarks are detected
                        // Retrieve key points and update the graphic overlay
                        if (showGraphicOverlay) {
                            graphicOverlay.clear();
                            graphicOverlay.add(new PoseGraphic(graphicOverlay, pose, false, false, false));
                            correctPose = false;
                        }

                        if (pose.getAllPoseLandmarks().isEmpty()) {
                            text.setText("No pose detected");
                        } else {
                            anglePrediction(pose);
                        }
                    });
                    // Close the image proxy
                    imageProxy.close();
                })
                .addOnFailureListener(e -> {
                    // Log error and close the image proxy
                    Log.e("PoseDetection", "Pose detection failed: " + e.getMessage());
                    imageProxy.close();
                }));
    }


    static double getAngle(PoseLandmark firstPoint, PoseLandmark midPoint, PoseLandmark lastPoint) {
        double result =
                Math.toDegrees(
                        atan2(lastPoint.getPosition().y - midPoint.getPosition().y,
                                lastPoint.getPosition().x - midPoint.getPosition().x)
                                - atan2(firstPoint.getPosition().y - midPoint.getPosition().y,
                                firstPoint.getPosition().x - midPoint.getPosition().x));
        result = Math.abs(result); // Angle should never be negative
        if (result > 180) {
            result = (360.0 - result); // Always get the acute representation of the angle
        }
        return result;
    }

    String temp = "";
    @SuppressLint("SetTextI18n")
    protected void anglePrediction(Pose pose) {

        Handler handler = new Handler();
        handler.post(() -> {

            int leftShoulderAngle = (int) getAngle(
                    Objects.requireNonNull(pose.getPoseLandmark(PoseLandmark.LEFT_HIP)),
                    Objects.requireNonNull(pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)),
                    Objects.requireNonNull(pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)));

            int rightShoulderAngle = (int) getAngle(
                    Objects.requireNonNull(pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)),
                    Objects.requireNonNull(pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)),
                    Objects.requireNonNull(pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)));

            int leftElbowAngle = (int) getAngle(
                    Objects.requireNonNull(pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)),
                    Objects.requireNonNull(pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)),
                    Objects.requireNonNull(pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)));

            int rightElbowAngle = (int) getAngle(
                    Objects.requireNonNull(pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)),
                    Objects.requireNonNull(pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)),
                    Objects.requireNonNull(pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST)));

            int leftHipAngle = (int) getAngle(
                    Objects.requireNonNull(pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)),
                    Objects.requireNonNull(pose.getPoseLandmark(PoseLandmark.LEFT_HIP)),
                    Objects.requireNonNull(pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)));

            int rightHipAngle = (int) getAngle(
                    Objects.requireNonNull(pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)),
                    Objects.requireNonNull(pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)),
                    Objects.requireNonNull(pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE)));

            int leftKneeAngle = (int) getAngle(
                    Objects.requireNonNull(pose.getPoseLandmark(PoseLandmark.LEFT_HIP)),
                    Objects.requireNonNull(pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)),
                    Objects.requireNonNull(pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE)));

            int rightKneeAngle = (int) getAngle(
                    Objects.requireNonNull(pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)),
                    Objects.requireNonNull(pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE)),
                    Objects.requireNonNull(pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE)));

            int templeftElbowAngle = leftElbowAngle / 10;
            int temprightElbowAngle = rightElbowAngle / 10;

            int templeftHip = leftHipAngle / 10;
            int temprightHip = rightHipAngle / 10;

            int templeftShoulderAngle = leftShoulderAngle / 10;
            int temprightShoulderAngle = rightShoulderAngle / 10;

            int templeftKneeAngle = leftKneeAngle / 10;
            int temprightKneeAngle = rightKneeAngle / 10;

            // int temptorsoAngle = torsoAngle / 10;

            temp = checkPose(yogaPose, templeftShoulderAngle, temprightShoulderAngle, templeftElbowAngle, temprightElbowAngle, templeftKneeAngle, temprightKneeAngle, templeftHip, temprightHip);

            if(temp.equals(yogaPose))
                updateAngleBuffer(result, 1);
            else
                updateAngleBuffer(result, 0);

            if(calculateAverageAngle(result) == 1) {
                correctPose = true;
                count = count + 1;
                int tempvar = count / 30;
                String progress = convertSecondsToHMSTime(tempvar);
                timing.setText("\nProgress : " + progress);
            }

            text.setText(temp);
        });
    }

    private static final int BUFFER_SIZE = 20; // Number of frames to average over

    private final Queue<Integer> result = new LinkedList<>();

    private void updateAngleBuffer(Queue<Integer> buffer, int newAngle) {
        if (buffer.size() >= BUFFER_SIZE) {
            buffer.poll(); // Remove the oldest angle
        }
        buffer.add(newAngle); // Add the newest angle
    }

    private int calculateAverageAngle(Queue<Integer> buffer) {
        int sum = 0;
        for (int angle : buffer) {
            sum += angle;
        }
        return sum / buffer.size();
    }

    @SuppressLint("DefaultLocale")
    public static String convertSecondsToHMSTime(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}