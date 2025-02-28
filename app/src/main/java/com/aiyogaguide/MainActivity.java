package com.aiyogaguide;

import static com.aiyogaguide.PoseGraphic.correctPose;
import static java.lang.Math.atan2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseDetection;
import com.google.mlkit.vision.pose.PoseDetector;
import com.google.mlkit.vision.pose.PoseLandmark;
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions;

import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import pl.droidsonroids.gif.GifImageView;
public class MainActivity extends AppCompatActivity {
    int step = 1;
    String yogaPose;
    int count = 0;
    private PreviewView previewView;
    private GraphicOverlay graphicOverlay;
    TextView text, title, timing,text_one;
    private static final int REQUEST_CAMERA_PERMISSION = 1001;
    boolean showGraphicOverlay = true;
    ImageView arrow;
    GifImageView successView;
    MediaPlayer mediaPlayer;
    MediaPlayer buzzer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // INIT
        title = findViewById(R.id.title);
        timing = findViewById(R.id.timing);
        previewView = findViewById(R.id.viewFinder);
        graphicOverlay = findViewById(R.id.graphicOverlay);
        arrow=findViewById(R.id.back_arrow);



         //Play the final audio
        mediaPlayer = MediaPlayer.create(this, R.raw.audio);
        buzzer=MediaPlayer.create(this,R.raw.buzzer1);

        successView = findViewById(R.id.successView);

        text = findViewById(R.id.text);

        text_one = findViewById(R.id.text1);
        //check which yoga pose user has selected
        Intent intent = getIntent();
        //retrive pose name
        yogaPose = intent.getStringExtra("poseName");
        title.setText(yogaPose);
        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,YogaPoseStepsActivity.class));
            }
        });

        startCamera();
    }
    //camera access
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
    //Imageproxy is binary data of image

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

        // Initialize Pose Detector with options  Machine learning code
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


    @SuppressLint("DefaultLocale")
    public static String convertSecondsToHMSTime(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

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

            // 1 Steps for Vrikshasana
            if (yogaPose.equals("Vrikshasana")) {
                if (step == 1) {
                    text.setText("Step1 : Stand Straight in front of Camera, Hold for 3 sec.");
                    if (between(templeftKneeAngle, 16, 17) && between(temprightKneeAngle, 16, 17)) {
                        if (between(templeftElbowAngle, 17, 17) && between(temprightElbowAngle, 17, 17)) {
                            correctPose = true;
                            count = count + 1;
                            int tempvar = count / 30;
                            String progress = convertSecondsToHMSTime(tempvar);
                            timing.setText("Progress : " + progress);
                            if (progress.compareTo("00:00:03") > 0) {
                                step = 2;
                                count = 0;
                                progress = convertSecondsToHMSTime(0);
                                timing.setText("Progress : " + progress);
                                buzzer.start();
                            }
                        }
                        else {
                            text_one.setText("Ensure Elbow angle 17 ");
                        }

                    } else {
                        text_one.setText("Ensure Knee angle between 16-17 ");
                    }
                }

                if (step == 2) {
                    text.setText("Step2 :Bend right leg placing the right foot on the inner left thigh.Hold for 2 sec.");
                    if (between(templeftKneeAngle, 16, 18) && between(temprightKneeAngle, 4, 5)) {
                        if (between(templeftShoulderAngle, 0, 2) && between(temprightShoulderAngle, 0, 2)) {
                            correctPose = true;
                            count = count + 1;
                            int tempvar = count / 30;
                            String progress = convertSecondsToHMSTime(tempvar);
                            timing.setText("Progress : " + progress);
                            if (progress.compareTo("00:00:03") > 0) {
                                step = 3;
                                count = 0;
                                progress = convertSecondsToHMSTime(0);
                                timing.setText("Progress : " + progress);
                                buzzer.start();
                            }
                        } else {
                            text_one.setText("Ensure Shoulder angle between 0-2");
                        }
                    } else {
                        text_one.setText("Ensure Left Knee angle between 16-18 & Right Knee angle between 4-5 ");
                    }
                }
                if (step == 3) {
                    text.setText("Step 3: Join your hands in Namaskar position.Hold pose for 2 sec");
                    if (between(templeftKneeAngle, 16, 18) && between(temprightKneeAngle, 4, 5)) {
                        if (between(temprightShoulderAngle, 3, 4) && between(templeftShoulderAngle, 3, 4)) {
                            if (between(templeftElbowAngle, 3, 4) && between(temprightElbowAngle, 3, 4)) {
                                correctPose = true;
                                count = count + 1;
                                int tempvar = count / 30;
                                String progress = convertSecondsToHMSTime(tempvar);
                                timing.setText("Progress : " + progress);
                                if (progress.compareTo("00:00:03") > 0) {
                                    step = 4;
                                    count = 0;
                                    progress = convertSecondsToHMSTime(0);
                                    timing.setText("Progress : " + progress);
                                    buzzer.start();
                                }
                            } else {
                                text_one.setText("Ensure Elbow angle between 3-4");
                            }
                        } else {
                            text_one.setText("Ensure Shoulder angle between 3-4");
                        }
                    }
                }
                if (step == 4) {
                    text.setText("Step 4:Stretch your arms up towards the ceiling.Hold for 2 sec.");
                    if (between(templeftKneeAngle, 16, 18) && between(temprightKneeAngle, 4, 5)) {
                        if (between(temprightShoulderAngle, 15, 17) && between(templeftShoulderAngle, 15, 17)) {
                            if (between(templeftElbowAngle, 11, 12) && between(temprightElbowAngle, 11, 12)) {
                                correctPose = true;
                                count = count + 1;
                                int tempvar = count / 30;
                                String progress = convertSecondsToHMSTime(tempvar);
                                timing.setText("Progress : " + progress);
                                if (progress.compareTo("00:00:03") > 0) {
                                    step = 5;
                                    count = 0;
                                    progress = convertSecondsToHMSTime(0);
                                    timing.setText("Progress : " + progress);
                                    buzzer.start();

                                }
                            } else {
                                text_one.setText("Ensure Elbow angle between 11-12");
                            }
                        } else {
                            text_one.setText("Ensure Shoulder angle between 15-17");
                        }
                    } else {
                        text_one.setText("Ensure Left Knee angle between 16-18 & Right Knee angle between 4-5 ");
                    }
                }
                if (step == 5) {
                    text.setText("Step 5:Hold for 10 sec.");
                    if (between(templeftKneeAngle, 16, 18) && between(temprightKneeAngle, 4, 5)) {
                        if (between(temprightShoulderAngle, 15, 17) && between(templeftShoulderAngle, 15, 17)) {
                            if (between(templeftElbowAngle, 11, 12) && between(temprightElbowAngle, 11, 12)) {
                                correctPose = true;
                                count = count + 1;
                                int tempvar = count / 30;
                                String progress = convertSecondsToHMSTime(tempvar);
                                timing.setText("Progress : " + progress);
                                if (progress.compareTo("00:00:10") > 0) {
                                    step = 6;
                                    successView.setVisibility(View.VISIBLE);
                                    Toast.makeText(MainActivity.this, "Great! You have perform this pose correctly.", Toast.LENGTH_LONG).show();
                                    mediaPlayer.start();
                                }
                            } else {
                                text_one.setText("Ensure Elbow angle between 11-12");
                            }
                        } else {
                            text_one.setText("Ensure Shoulder angle between 15-17");
                        }
                    } else {
                        text_one.setText("Ensure Left Knee angle between 16-18 & Right Knee angle between 4-5 ");
                    }
                }
            }

            // 2 Steps for Baddha-Konasana
            if (yogaPose.equals("Baddha-Konasana")) {

                if (step == 1) {
                    text.setText("Step 1: Start by sitting on floor with your legs extended in front of you, Hold for 3 sec.");
                    if (between(templeftKneeAngle, 12,17) && between(temprightKneeAngle, 12, 17)) {
                        if (between(templeftHip, 6, 11) && between(temprightHip, 6, 11)) {
                            correctPose = true;
                            count = count + 1;
                            int tempvar = count / 30;
                            String progress = convertSecondsToHMSTime(tempvar);
                            timing.setText("Progress : " + progress);
                            if (progress.compareTo("00:00:03") > 0) {
                                step = 2;
                                count = 0;
                                progress = convertSecondsToHMSTime(0);
                                timing.setText("Progress : " + progress);
                                buzzer.start();
                            }
                        }
                        else {
                            text_one.setText("Ensure Hip angle between 6-9");
                        }
                    } else {
                        text_one.setText("Ensure Knee angle between 13-15");
                    }
                }

                if (step == 2) {
                    text.setText("Step 2: Bend right & left knee, Bring heels close to the area between thighs so soles of both feet touch.Hold for 3 sec.");
                    if (between(templeftKneeAngle, 1, 2) && between(temprightKneeAngle, 1, 2)) {
                        if (between(templeftHip, 6, 7) && between(temprightHip, 6, 7)) {
                            correctPose = true;
                            count = count + 1;
                            int tempvar = count / 30;
                            String progress = convertSecondsToHMSTime(tempvar);
                            timing.setText("Progress : " + progress);
                            if (progress.compareTo("00:00:03") > 0) {
                                step = 3;
                                count = 0;
                                progress = convertSecondsToHMSTime(0);
                                timing.setText("Progress : " + progress);
                                buzzer.start();
                            }
                        } else {
                            text_one.setText("Ensure Hip angle between 6-7");
                        }

                    } else {
                        text_one.setText("Ensure Knee angle between 1-2");
                    }
                }

                if (step == 3) {
                    text.setText("Step 3: Hold feet with hands & try to keep thighs as close to the ground for 3 sec.");
                    if (between(templeftKneeAngle, 1, 2) && between(temprightKneeAngle, 1, 2)) {
                        if (between(templeftHip, 6, 7) && between(temprightHip, 6, 7)) {
                            if (between(templeftShoulderAngle, 0, 2) && between(temprightShoulderAngle, 0, 2)) {
                                if (between(templeftElbowAngle, 16, 17) && between(temprightElbowAngle, 16, 18)) {
                                    correctPose = true;
                                    count = count + 1;
                                    int tempvar = count / 30;
                                    String progress = convertSecondsToHMSTime(tempvar);
                                    timing.setText("Progress : " + progress);
                                    if (progress.compareTo("00:00:03") > 0) {
                                        step = 4;
                                        count = 0;
                                        progress = convertSecondsToHMSTime(0);
                                        timing.setText("Progress : " + progress);
                                        buzzer.start();
                                    }
                                }
                                else {
                                    text_one.setText("Ensure Elbow angle between 16-18");
                                }
                            } else {
                                text_one.setText("Ensure Shoulder angle between 0-2");
                            }
                        } else {
                            text_one.setText("Ensure Hip angle between 6-8");
                        }
                    } else {
                        text_one.setText("Ensure Knee angle between 1-3");
                    }
                }

                if (step == 4) {
                    text.setText("Step 4: Straighten up your spine & Hold this posture for 10 sec.");
                    if (between(templeftKneeAngle, 1, 3) && between(temprightKneeAngle, 1, 3)) {
                        if (between(templeftHip, 6, 8) && between(temprightHip, 6, 8)) {
                            if (between(templeftShoulderAngle, 0, 2) && between(temprightShoulderAngle, 0, 2)) {
                                if (between(templeftElbowAngle, 16, 18) && between(temprightElbowAngle, 16, 17)) {
                                    correctPose = true;
                                    count = count + 1;
                                    int tempvar = count / 30;
                                    String progress = convertSecondsToHMSTime(tempvar);
                                    timing.setText("Progress : " + progress);
                                    if (progress.compareTo("00:00:10") > 0) {
                                        step = 5;
                                        successView.setVisibility(View.VISIBLE);
                                        Toast.makeText(MainActivity.this, "Great! You have performed this pose correctly.", Toast.LENGTH_LONG).show();
                                        mediaPlayer.start();
                                    }
                                }
                                else {
                                    text_one.setText("Ensure Elbow angle between 16-18");
                                }
                            } else {
                                text_one.setText("Ensure Shoulder between angle 0-2");
                            }
                        } else {
                            text_one.setText("Ensure Hip angle between 6-8");
                        }
                    } else {
                        text_one.setText("Ensure Knee angle between 1-3");
                    }
                }
            }
            // 3 steps Veerabhadrasana-1
            if (yogaPose.equals("Veerabhadrasana-1")) {
                if (step == 1) {
                    text.setText("Step1 : Stand Straight with your arm on your sides facing to your right side. ");
                    if (between(templeftKneeAngle, 17, 17) && between(temprightKneeAngle, 17, 17)) {
                        if (between(temprightShoulderAngle, 0, 0) && between(templeftShoulderAngle, 0, 0)) {
                            if (between(templeftElbowAngle, 16, 17) && between(temprightElbowAngle, 16, 17)) {
                                correctPose = true;
                                count = count + 1;
                                int tempvar = count / 30;
                                String progress = convertSecondsToHMSTime(tempvar);
                                timing.setText("Progress : " + progress);
                                if (progress.compareTo("00:00:03") > 0) {
                                    step = 2;
                                    count = 0;
                                    progress = convertSecondsToHMSTime(0);
                                    timing.setText("Progress : " + progress);
                                    buzzer.start();
                                }
                            } else {
                                text_one.setText("Ensure Elbow angle between 16-17");
                            }
                        } else {
                            text_one.setText("Ensure Shoulder angle between 0-0");
                        }

                    } else {
                        text_one.setText("Ensure Left Knee angle between 16-17 & Right Knee angle between 16-17");
                    }
                }
                if (step == 2) {
                    text.setText("Step 2 : Step the left leg back into a lunge where the knee aligns over the ankle, bring the hands to the hips.");
                    if (between(temprightShoulderAngle, 0, 2) && between(templeftShoulderAngle, 0, 2)) {
                        if (between(templeftElbowAngle, 13, 16) && between(temprightElbowAngle, 13, 16)) {
                            if (between(templeftHip, 14, 16) && between(temprightHip, 10, 12)) {
                                if (between(templeftKneeAngle, 16, 17) && between(temprightKneeAngle, 10, 12)) {

                                    correctPose = true;
                                    count = count + 1;
                                    int tempvar = count / 30;
                                    String progress = convertSecondsToHMSTime(tempvar);
                                    timing.setText("Progress : " + progress);
                                    if (progress.compareTo("00:00:03") > 0) {
                                        step = 3;
                                        count = 0;
                                        progress = convertSecondsToHMSTime(0);
                                        timing.setText("Progress : " + progress);
                                        buzzer.start();
                                    }
                                } else {
                                    text_one.setText("Ensure Left Knee angle between 16-17 & Right Knee angle between 10-12");
                                }
                            } else {
                                text_one.setText("Ensure LeftHip angle between 10-12 and RightHip 14-16");
                            }
                        } else {
                            text_one.setText("Ensure Elbow angle 13-16 ");
                        }
                    } else {
                        text_one.setText("Ensure Shoulder angle between 0-2");
                    }

                }
                if (step == 3) {
                    text.setText("Step 3 : Reach the arms fordward wrap the triceps under. ");
                    if (between(temprightShoulderAngle, 4, 9) && between(templeftShoulderAngle, 4, 9)) {
                        if (between(templeftElbowAngle, 15, 17) && between(temprightElbowAngle, 15, 17)) {
                            if (between(templeftHip, 14, 16) && between(temprightHip, 10, 12)) {
                                if (between(templeftKneeAngle, 16, 17) && between(temprightKneeAngle, 10, 12)) {

                                    correctPose = true;
                                    count = count + 1;
                                    int tempvar = count / 30;
                                    String progress = convertSecondsToHMSTime(tempvar);
                                    timing.setText("Progress : " + progress);
                                    if (progress.compareTo("00:00:03") > 0) {
                                        step = 4;
                                        count = 0;
                                        progress = convertSecondsToHMSTime(0);
                                        timing.setText("Progress : " + progress);
                                        buzzer.start();
                                    }
                                } else {
                                    text_one.setText("Ensure Left Knee angle between 16-17 & Right Knee angle between 10-12");
                                }
                            } else {
                                text_one.setText("Ensure LeftHip angle between 10-12 and RightHip 14-16");
                            }
                        } else {
                            text_one.setText("Ensure Elbow angle 15-17 ");
                        }
                    } else {
                        text_one.setText("Ensure Shoulder between 6-9");
                    }

                }
                if (step == 4) {
                    text.setText("Step 4 : Reach the arms fordward wrap the triceps under. ");
                    if (between(temprightShoulderAngle, 14, 17) && between(templeftShoulderAngle, 14, 17)) {
                        if (between(templeftElbowAngle, 16, 17) && between(temprightElbowAngle, 12, 17)) {
                            if (between(templeftHip, 14, 16) && between(temprightHip, 10, 12)) {
                                if (between(templeftKneeAngle, 16, 17) && between(temprightKneeAngle, 10, 12)) {

                                    correctPose = true;
                                    count = count + 1;
                                    int tempvar = count / 30;
                                    String progress = convertSecondsToHMSTime(tempvar);
                                    timing.setText("Progress : " + progress);
                                    if (progress.compareTo("00:00:03") > 0) {
                                        step = 5;
                                        count = 0;
                                        progress = convertSecondsToHMSTime(0);
                                        timing.setText("Progress : " + progress);
                                        buzzer.start();
                                    }
                                } else {
                                    text_one.setText("Ensure Left Knee angle between 16-17 & Right Knee angle between 10-12");
                                }
                            } else {
                                text_one.setText("Ensure LeftHip angle between 10-12 and RightHip 14-16");
                            }
                        } else {
                            text_one.setText("Ensure Elbow angle 16-17 ");
                        }
                    } else {
                        text_one.setText("Ensure Shoulder angle between 14-17");
                    }

                }
                if (step == 5) {
                    text.setText("Step 5: Hold pose for 10 sec");
                    if (between(temprightShoulderAngle, 14, 16) && between(templeftShoulderAngle, 14, 16)) {
                        if (between(templeftElbowAngle, 16, 17) && between(temprightElbowAngle, 12, 17)) {
                            if (between(templeftHip, 14, 16) && between(temprightHip, 10, 12)) {
                                if (between(templeftKneeAngle, 16, 17) && between(temprightKneeAngle, 10, 12)) {

                                    correctPose = true;
                                    count = count + 1;
                                    int tempvar = count / 30;
                                    String progress = convertSecondsToHMSTime(tempvar);
                                    timing.setText("Progress : " + progress);
                                    if (progress.compareTo("00:00:10") > 0) {
                                        step = 6;
                                        successView.setVisibility(View.VISIBLE);
                                        Toast.makeText(MainActivity.this, "Great! You have perform this pose correctly.", Toast.LENGTH_LONG).show();
                                        mediaPlayer.start();

                                    }
                                } else {
                                    text_one.setText("Ensure Left Knee angle between 16-17 & Right Knee angle between 10-12");
                                }
                            } else {
                                text_one.setText("Ensure LeftHip angle between 10-12 and RightHip 14-16");
                            }
                        } else {
                            text_one.setText("Ensure Elbow angle 16-17 ");
                        }
                    } else {
                        text_one.setText("Ensure Shoulder angle between 16-17");
                    }

                }
            }
          // 4 steps Veerabhadrasana
            if (yogaPose.equals("Veerabhadrasana-2")) {
                if (step == 1) {
                    text.setText("Step 1 : Stand Straight in front of Camera, Hold for 3 sec.");
                    if (between(templeftKneeAngle, 16, 17) && between(temprightKneeAngle, 16, 17)) {
                        if (between(templeftElbowAngle, 17, 17) && between(temprightElbowAngle, 17, 17)) {
                            correctPose = true;
                            count = count + 1;
                            int tempvar = count / 30;
                            String progress = convertSecondsToHMSTime(tempvar);
                            timing.setText("Progress : " + progress);
                            if (progress.compareTo("00:00:03") > 0) {
                                step = 2;
                                count = 0;
                                progress = convertSecondsToHMSTime(0);
                                timing.setText("Progress : " + progress);
                                buzzer.start();
                            }
                        }
                        else {
                            text_one.setText("Ensure Elbow angle 17 ");
                        }

                    } else {
                        text_one.setText("Ensure Knee angle between 16-17 ");
                    }
                }
                if (step == 2) {
                    text.setText("Step2 : Stand in a wide position with your feet parallel and approximately three feet apart. Extend your arms straight out from your sides. ");
                    if (between(temprightShoulderAngle, 8, 9) && between(templeftShoulderAngle, 8, 9)) {
                        if (between(templeftElbowAngle, 16, 17) && between(temprightElbowAngle, 16, 17)) {
                            if (between(templeftHip, 15, 16) && between(temprightHip, 15, 16)) {
                                if (between(templeftKneeAngle, 17, 17) && between(temprightKneeAngle, 17, 17)) {
                                    correctPose = true;
                                    count = count + 1;
                                    int tempvar = count / 30;
                                    String progress = convertSecondsToHMSTime(tempvar);
                                    timing.setText("Progress : " + progress);
                                    if (progress.compareTo("00:00:03") > 0) {
                                        step = 3;
                                        count = 0;
                                        progress = convertSecondsToHMSTime(0);
                                        timing.setText("Progress : " + progress);
                                        buzzer.start();
                                    }
                                }
                                else {
                                    text_one.setText("Ensure Knee angle between 17-17");
                                }
                            }
                            else {
                                text_one.setText("Ensure Hip angle between 15-16");
                            }
                        }
                        else {
                            text_one.setText("Ensure Elbow angle between 16-17");
                        }

                    }  else {
                        text_one.setText("Ensure Shoulder between angle 8-9");
                    }
                }

                if (step == 3) {
                    text.setText("Step 3: Inhale , lift arms in front, levelling up to your shoulders Lock the fingers of hands then rotate wrist outwards Hold pose for 3 sec");
                    if (between(templeftKneeAngle, 16, 18) && between(temprightKneeAngle, 10, 12)) {
                        if (between(temprightShoulderAngle, 9, 10) && between(templeftShoulderAngle, 9, 10)) {
                            if (between(templeftElbowAngle, 16, 18) && between(temprightElbowAngle, 16, 18)) {
                                correctPose = true;
                                count = count + 1;
                                int tempvar = count / 30;
                                String progress = convertSecondsToHMSTime(tempvar);
                                timing.setText("Progress : " + progress);
                                if (progress.compareTo("00:00:03") > 0) {
                                    step = 4;
                                    count = 0;
                                    progress = convertSecondsToHMSTime(0);
                                    timing.setText("Progress : " + progress);
                                    buzzer.start();

                                }
                            } else {
                                text_one.setText("Ensure Elbow angle between 16-18");
                            }
                        } else {
                            text_one.setText("Ensure Shoulder angle between 9-10");
                        }
                    }
                    else {
                        text_one.setText("Ensure LeftKnee angle between 16-18,RightKnee angle between 10-12");
                    }
                      }
                if (step == 4) {
                    text.setText("Step 4: Hold pose for 10 sec");
                    if (between(templeftKneeAngle, 16, 18) && between(temprightKneeAngle, 10, 12)) {
                        if (between(temprightShoulderAngle, 9, 10) && between(templeftShoulderAngle, 9, 10)) {
                            if (between(templeftElbowAngle, 16, 18) && between(temprightElbowAngle, 16, 18)) {
                                correctPose = true;
                                count = count + 1;
                                int tempvar = count / 30;
                                String progress = convertSecondsToHMSTime(tempvar);
                                timing.setText("Progress : " + progress);
                                if (progress.compareTo("00:00:10") > 0) {
                                    step = 5;
                                    successView.setVisibility(View.VISIBLE);
                                    Toast.makeText(MainActivity.this, "Great! You have perform this pose correctly.", Toast.LENGTH_LONG).show();
                                    mediaPlayer.start();

                                }
                            } else {
                                text_one.setText("Ensure Elbow angle between 16-18");
                            }
                        } else {
                            text_one.setText("Ensure Shoulder angle between 9-10");
                        }
                    }
                    else {
                        text_one.setText("Ensure LeftKnee angle between 16-18,RightKnee angle between 10-12");
                    }
                }
            }

            // 5 steps Veerabhadrasana-3
            if (yogaPose.equals("Veerabhadrasana-3")) {
                if (step == 1) {
                    text.setText("Step1 : Stand Straight with your arm on your sides facing to your left side. ");
                    if (between(templeftKneeAngle, 17, 17) && between(temprightKneeAngle, 17, 17)) {
                        if (between(temprightShoulderAngle, 0, 0) && between(templeftShoulderAngle, 0, 0)) {
                            if (between(templeftElbowAngle, 16, 17) && between(temprightElbowAngle, 16, 17)) {
                                correctPose = true;
                                count = count + 1;
                                int tempvar = count / 30;
                                String progress = convertSecondsToHMSTime(tempvar);
                                timing.setText("Progress : " + progress);
                                if (progress.compareTo("00:00:03") > 0) {
                                    step = 2;
                                    count = 0;
                                    progress = convertSecondsToHMSTime(0);
                                    timing.setText("Progress : " + progress);
                                    buzzer.start();
                                }
                            }
                            else {
                                text_one.setText("Ensure Elbow angle between 16-17");
                            }
                        }else {
                            text_one.setText("Ensure Shoulder angle between 0-0");
                        }

                    } else {
                        text_one.setText("Ensure Left Knee angle between 16-17 & Right Knee angle between 16-17");
                    }
                }
                if (step == 2) {
                    text.setText("Step 2:Rise front leg sole of the foot and lift back leg up in 90 degree,left arm fingertips are underneath shoulder.");
                    if (between(templeftKneeAngle, 16, 17) && between(temprightKneeAngle, 16, 17)) {
                        if (between(temprightShoulderAngle, 1, 3) && between(templeftShoulderAngle, 1, 3)) {
                            if (between(templeftElbowAngle, 16, 17) && between(temprightElbowAngle, 16, 17)) {
                                if (between(templeftHip, 16, 17) && between(temprightHip, 7, 10)) {
                                    correctPose = true;
                                    count = count + 1;
                                    int tempvar = count / 30;
                                    String progress = convertSecondsToHMSTime(tempvar);
                                    timing.setText("Progress : " + progress);
                                    if (progress.compareTo("00:00:03") > 0) {
                                        step = 3;
                                        count = 0;
                                        progress = convertSecondsToHMSTime(0);
                                        timing.setText("Progress : " + progress);
                                        buzzer.start();
                                    }
                                }
                                else {
                                    text_one.setText("Ensure Left Hip angle between 16-17 & Right Hip angle between 7-10 ");
                                }
                            } else {
                                text_one.setText("Ensure Elbow angle between 16-17");
                            }
                        } else {
                            text_one.setText("Ensure Shoulder angle between 1-3");
                        }
                    } else {
                        text_one.setText("Ensure Knee angle between 16-17 ");
                    }
                }

                if (step == 2) {
                    text.setText("Step 2 : Begin to fold torso forward as you reach right leg behind, bringing torso and right leg in one straight line, point the toes of lifted leg foot and look down. ");
                    if (between(temprightShoulderAngle, 0, 2) && between(templeftShoulderAngle, 0, 2)) {
                        if (between(templeftElbowAngle, 10, 14) && between(temprightElbowAngle, 10, 14)) {
                            if (between(templeftHip, 14, 16) && between(temprightHip, 8, 10)) {
                                if (between(templeftKneeAngle, 16, 17) && between(temprightKneeAngle, 8, 10)) {
                                    correctPose = true;
                                    count = count + 1;
                                    int tempvar = count / 30;
                                    String progress = convertSecondsToHMSTime(tempvar);
                                    timing.setText("Progress : " + progress);
                                    if (progress.compareTo("00:00:03") > 0) {
                                        step = 3;
                                        count = 0;
                                        progress = convertSecondsToHMSTime(0);
                                        timing.setText("Progress : " + progress);
                                        buzzer.start();
                                    }
                                }
                                else {
                                    text_one.setText("Ensure Left Knee angle between 16-17 and Right Knee angle 8-10");
                                }
                            }
                            else {
                                text_one.setText("Ensure Left Hip angle between 14-16 and Right Hip 8-10");
                            }
                        }
                        else {
                            text_one.setText("Ensure Elbow angle between 10-14");
                        }

                    }  else {
                        text_one.setText("Ensure Shoulder between angle 0-2");
                    }
                }
                if (step == 3) {
                    text.setText("Step 3 : Reach your arm forward and draw your ribs in. ");
                    if (between(temprightShoulderAngle, 12, 14) && between(templeftShoulderAngle, 12, 14)) {
                        if (between(templeftElbowAngle, 16, 17) && between(temprightElbowAngle, 16, 17)) {
                            if (between(templeftHip, 14, 17) && between(temprightHip, 9, 12)) {
                                if (between(templeftKneeAngle, 16, 17) && between(temprightKneeAngle, 16, 17)) {
                                    correctPose = true;
                                    count = count + 1;
                                    int tempvar = count / 30;
                                    String progress = convertSecondsToHMSTime(tempvar);
                                    timing.setText("Progress : " + progress);
                                    if (progress.compareTo("00:00:03") > 0) {
                                        step = 4;
                                        count = 0;
                                        progress = convertSecondsToHMSTime(0);
                                        timing.setText("Progress : " + progress);
                                        buzzer.start();
                                    }
                                }
                                else {
                                    text_one.setText("Ensure Knee angle between 16-17 ");
                                }
                            }
                            else {
                                text_one.setText("Ensure Left Hip angle between 14-17 and Right Hip 9-12");
                            }
                        }
                        else {
                            text_one.setText("Ensure Elbow angle between 16-17");
                        }

                    }  else {
                        text_one.setText("Ensure Shoulder between angle 12-14");
                    }
                }

                if (step == 4) {
                    text.setText("Step 4 : Hold this pose for 10sec. ");
                    if (between(temprightShoulderAngle, 12, 14) && between(templeftShoulderAngle, 12, 14)) {
                        if (between(templeftElbowAngle, 16, 17) && between(temprightElbowAngle, 16, 17)) {
                            if (between(templeftHip, 14, 17) && between(temprightHip, 9, 12)) {
                                if (between(templeftKneeAngle, 16, 17) && between(temprightKneeAngle, 16, 17)) {
                                    correctPose = true;
                                    count = count + 1;
                                    int tempvar = count / 30;
                                    String progress = convertSecondsToHMSTime(tempvar);
                                    timing.setText("Progress : " + progress);
                                    if (progress.compareTo("00:00:10") > 0) {
                                        step = 5;
                                        successView.setVisibility(View.VISIBLE);
                                        Toast.makeText(MainActivity.this, "Great! You have perform this pose correctly.", Toast.LENGTH_LONG).show();
                                        mediaPlayer.start();

                                    }
                                }
                                else {
                                    text_one.setText("Ensure Knee angle between 16-17 ");
                                }
                            }
                            else {
                                text_one.setText("Ensure Left Hip angle between 14-17 and Right Hip 9-12");
                            }
                        }
                        else {
                            text_one.setText("Ensure Elbow angle between 16-17");
                        }

                    }  else {
                        text_one.setText("Ensure Shoulder between angle 12-14");
                    }
                }

            }
            //6 steps for Downward Dog
            if (yogaPose.equals("Downward-Dog")) {

                if (step == 1) {
                    text.setText("Step 1 : Bend your knees, resting your hands and your wrists on the floor underneath the shoulders. ");
                    if (between(templeftShoulderAngle, 7, 9) && between(temprightShoulderAngle, 7, 9)) {
                        if (between(templeftKneeAngle, 7, 9) && between(temprightKneeAngle, 7, 9)) {
                            if (between(templeftElbowAngle, 16, 17) && between(temprightElbowAngle, 16, 17)) {
                                if (between(templeftHip, 9, 10) && between(temprightHip, 9, 10)) {

                                    correctPose = true;
                                    count = count + 1;
                                    int tempvar = count / 30;
                                    String progress = convertSecondsToHMSTime(tempvar);
                                    timing.setText("Progress : " + progress);
                                    if (progress.compareTo("00:00:03") > 0) {
                                        step = 2;
                                        count = 0;
                                        progress = convertSecondsToHMSTime(0);
                                        timing.setText("Progress : " + progress);
                                        buzzer.start();
                                    }
                                } else {
                                    text_one.setText("Ensure hip angle between 9-10 ");
                                }
                            } else {
                                text_one.setText("Ensure elbow angle between 16-17 ");
                            }
                        } else {
                            text_one.setText("Ensure knee angle between 7-9 ");
                        }
                    } else {
                        text_one.setText("Ensure shoulder angle between 7-9");
                    }
                }

                if (step == 2) {
                    text.setText("Step 2: Push your body back using your hands while curling in your toes and place your heels on the floor");
                    if (between(templeftKneeAngle, 16, 17) && between(temprightKneeAngle, 16, 17)) {
                        if (between(temprightShoulderAngle, 13, 17) && between(templeftShoulderAngle, 14, 17)) {
                            if (between(templeftElbowAngle, 16, 17) && between(temprightElbowAngle, 16, 17)) {
                                if (between(templeftHip, 9, 10) && between(temprightHip, 9, 10)) {
                                    correctPose = true;
                                    count = count + 1;
                                    int tempvar = count / 30;
                                    String progress = convertSecondsToHMSTime(tempvar);
                                    timing.setText("Progress : " + progress);
                                    if (progress.compareTo("00:00:03") > 0) {
                                        step = 3;
                                        count = 0;
                                        progress = convertSecondsToHMSTime(0);
                                        timing.setText("Progress : " + progress);
                                        buzzer.start();
                                    }
                                } else {
                                    text_one.setText("Ensure hip angle between 9-10 ");
                                }
                            } else {
                                text_one.setText("Ensure elbow angle between 16-17 ");
                            }
                        } else {
                            text_one.setText("Ensure Shoulder angle between 13-17 ");
                        }
                    } else {
                        text_one.setText("Ensure Knee angle between 16-17");
                    }
                }
                if (step == 3) {
                    text.setText("Step 3: Hold this pose for 10 sec");
                    if (between(templeftKneeAngle, 16, 17) && between(temprightKneeAngle, 16, 17)) {
                        if (between(temprightShoulderAngle, 13, 17) && between(templeftShoulderAngle, 13, 17)) {
                            if (between(templeftElbowAngle, 16, 17) && between(temprightElbowAngle, 16, 17)) {
                                if (between(templeftHip, 9, 10) && between(temprightHip, 9, 10)) {
                                    correctPose = true;
                                    count = count + 1;
                                    int tempvar = count / 30;
                                    String progress = convertSecondsToHMSTime(tempvar);
                                    timing.setText("Progress : " + progress);
                                    if (progress.compareTo("00:00:10") > 0) {
                                        step = 4;
                                        successView.setVisibility(View.VISIBLE);
                                        Toast.makeText(MainActivity.this, "Great! You have perform this pose correctly.", Toast.LENGTH_LONG).show();
                                        mediaPlayer.start();
                                    }
                                } else {
                                    text_one.setText("Ensure hip angle between 9-10 ");
                                }
                            } else {
                                text_one.setText("Ensure elbow angle between 16-17 ");
                            }
                        } else {
                            text_one.setText("Ensure Shoulder angle between 13-17 ");
                        }
                    } else {
                        text_one.setText("Ensure Knee angle between 16-17");
                    }
                }
            }
            //7 Steps for Natarajasana
            //Natarajasana

            if (yogaPose.equals("Natarajasana")) {

                if (step == 1) {
                    text.setText("Step1 : Stand Straight with your arm on your sides facing to your left side. ");
                    if (between(templeftKneeAngle, 17, 17) && between(temprightKneeAngle, 17, 17)) {
                        if (between(temprightShoulderAngle, 0, 0) && between(templeftShoulderAngle, 0, 0)) {
                            if (between(templeftElbowAngle, 16, 17) && between(temprightElbowAngle, 16, 17)) {
                                correctPose = true;
                                count = count + 1;
                                int tempvar = count / 30;
                                String progress = convertSecondsToHMSTime(tempvar);
                                timing.setText("Progress : " + progress);
                                if (progress.compareTo("00:00:03") > 0) {
                                    step = 2;
                                    count = 0;
                                    progress = convertSecondsToHMSTime(0);
                                    timing.setText("Progress : " + progress);
                                    buzzer.start();
                                }
                            }
                            else {
                                text_one.setText("Ensure Elbow angle between 16-17");
                            }
                        }else {
                            text_one.setText("Ensure Shoulder angle between 0-0");
                        }

                    } else {
                        text_one.setText("Ensure Left Knee angle between 16-17 & Right Knee angle between 16-17");
                    }
                }
                if (step == 2) {
                    text.setText("Step 2: While inhaling bend your right leg backward and holds right ankle with right hand.");
                    if (between(templeftKneeAngle, 16, 17) && between(temprightKneeAngle, 8, 10)) {
                        if (between(temprightShoulderAngle, 5, 6) && between(templeftShoulderAngle, 1, 2)) {
                            if (between(templeftElbowAngle, 16, 17) && between(temprightElbowAngle, 16, 17)) {
                                correctPose = true;
                                count = count + 1;
                                int tempvar = count / 30;
                                String progress = convertSecondsToHMSTime(tempvar);
                                timing.setText("Progress : " + progress);
                                if (progress.compareTo("00:00:03") > 0) {
                                    step = 3;
                                    count = 0;
                                    progress = convertSecondsToHMSTime(0);
                                    timing.setText("Progress : " + progress);
                                    buzzer.start();
                                }
                            } else {
                                text_one.setText("Ensure Elbow angle between 16-17");
                            }
                        }
                        else {
                            text_one.setText("Ensure LeftShoulder angle between 5-6 , RightShoulder angle between 1-1 ");
                        }
                    }
                    else {
                        text_one.setText("Ensure Left Knee angle between 16-17 and Right Knee angle 8-10 ");
                    }
                }
                if (step == 3) {
                    text.setText("Step 3: Bend your right leg upward and Extend your left hand straight out front.");
                    if (between(templeftKneeAngle, 16, 17) && between(temprightKneeAngle, 8, 10)) {
                        if (between(temprightShoulderAngle, 5, 7) && between(templeftShoulderAngle, 12, 15)) {
                            if (between(templeftElbowAngle, 16, 17) && between(temprightElbowAngle, 16, 17)) {
                                correctPose = true;
                                count = count + 1;
                                int tempvar = count / 30;
                                String progress = convertSecondsToHMSTime(tempvar);
                                timing.setText("Progress : " + progress);
                                if (progress.compareTo("00:00:03") > 0) {
                                    step = 4;
                                    count = 0;
                                    progress = convertSecondsToHMSTime(0);
                                    timing.setText("Progress : " + progress);
                                    buzzer.start();
                                }
                            } else {
                                text_one.setText("Ensure Elbow angle between 16-17");
                            }
                        } else {
                            text_one.setText("Ensure LeftShoulder angle between 5-7 and RightShoulder angle between 12 to 15");
                        }
                    }
                    else {
                        text_one.setText("Ensure Left Knee angle between 16-17 & Right Knee angle between 8-10 ");
                    }
                }
                if (step == 4) {
                    text.setText("Step 4: Try to bring your right leg upward as much as you can.Hold for 10 sec.");
                    if (between(templeftKneeAngle, 16, 17) && between(temprightKneeAngle, 8, 10)) {
                        if (between(temprightShoulderAngle, 5, 7) && between(templeftShoulderAngle, 12, 15)) {
                            if (between(templeftElbowAngle, 16, 17) && between(temprightElbowAngle, 16, 17)) {
                                correctPose = true;
                                count = count + 1;
                                int tempvar = count / 30;
                                String progress = convertSecondsToHMSTime(tempvar);
                                timing.setText("Progress : " + progress);
                                if (progress.compareTo("00:00:10") > 0) {
                                    step = 5;
                                    successView.setVisibility(View.VISIBLE);
                                    Toast.makeText(MainActivity.this, "Great! You have perform this pose correctly.", Toast.LENGTH_LONG).show();
                                    mediaPlayer.start();
                                }
                            } else {
                                text_one.setText("Ensure Elbow angle between 16-17");
                            }
                        } else {
                            text_one.setText("Ensure LeftShoulder angle between 5-7 and RightShoulder angle between 12 to 15");
                        }
                    }
                    else {
                        text_one.setText("Ensure Left Knee angle between 16-17 & Right Knee angle between 8-10 ");
                    }
                }
            }


            //8 steps for Utkatakonasana
            if (yogaPose.equals("Utkatakonasana")) {

                if (step == 1) {
                    text.setText("Step1 :Stand Straight in front of Camera.");
                    if (between(templeftKneeAngle, 16, 17) && between(temprightKneeAngle, 16, 17)) {
                        if (between(templeftElbowAngle, 17, 17) && between(temprightElbowAngle, 17, 17)) {


                                correctPose = true;
                                count = count + 1;
                                int tempvar = count / 30;
                                String progress = convertSecondsToHMSTime(tempvar);
                                timing.setText("Progress : " + progress);
                                if (progress.compareTo("00:00:03") > 0) {
                                    step = 2;
                                    count = 0;
                                    progress = convertSecondsToHMSTime(0);
                                    timing.setText("Progress : " + progress);
                                    buzzer.start();
                                }


                        }
                    }
                    else {
                        text_one.setText("Ensure knee angle is 16-17");
                    }
                }
                if (step == 2) {
                    text.setText("Step2 : Slowly bend your knees,feet slightly wider than hip-width apart.");
                    if (between(templeftKneeAngle, 10, 11) && between(temprightKneeAngle, 10, 11)) {
                        if (between(templeftHip, 10, 12) && between(temprightHip, 10, 12)) {
                            if (between(templeftShoulderAngle, 0, 2) && between(temprightShoulderAngle, 0, 2)) {

                                correctPose = true;
                                count = count + 1;
                                int tempvar = count / 30;
                                String progress = convertSecondsToHMSTime(tempvar);
                                timing.setText("Progress : " + progress);
                                if (progress.compareTo("00:00:03") > 0) {
                                    step = 3;
                                    count = 0;
                                    progress = convertSecondsToHMSTime(0);
                                    timing.setText("Progress : " + progress);
                                    buzzer.start();
                                }

                            } else {
                                text_one.setText("Ensure shoulder angle between 0-2");
                            }
                        } else {
                            text_one.setText("Ensure hip angle is 10-12 ");
                        }
                    } else {
                        text_one.setText("Ensure knee angle is 10-11");
                    }
                }

                if (step == 3) {
                    text.setText("Step3 :Bending the elbows to form a Namaskar Position in L shape.");
                    if (between(templeftKneeAngle, 10, 11) && between(temprightKneeAngle, 10, 11)) {
                        if (between(templeftElbowAngle, 5, 6) && between(temprightElbowAngle, 5, 6)) {
                            if (between(templeftShoulderAngle, 2, 3) && between(temprightShoulderAngle, 2, 3)) {
                                if (between(templeftHip, 10, 12) && between(temprightHip, 10, 12)) {
                                    correctPose = true;
                                    count = count + 1;
                                    int tempvar = count / 30;
                                    String progress = convertSecondsToHMSTime(tempvar);
                                    timing.setText("Progress : " + progress);
                                    if (progress.compareTo("00:00:03") > 0) {
                                        step = 4;
                                        count = 0;
                                        progress = convertSecondsToHMSTime(0);
                                        timing.setText("Progress : " + progress);
                                        buzzer.start();
                                    }

                                } else {
                                    text_one.setText("Ensure hip angle between 10-12");
                                }
                            } else {
                                text_one.setText("Ensure shoulder angle between 2-3");
                            }
                        } else {
                            text_one.setText("Ensure elbow angle between 5-6");
                        }
                    } else {
                        text_one.setText("Ensure knee angle between 10-11");
                    }
                }


                if (step == 4) {
                    text.setText("Step 4 :Inhale and Hold this pose for 10 sec.");
                    if (between(templeftKneeAngle, 10, 11) && between(temprightKneeAngle, 10, 11)) {
                        if (between(templeftElbowAngle, 5, 6) && between(temprightElbowAngle, 5, 6)) {
                            if (between(templeftShoulderAngle, 2, 3) && between(temprightShoulderAngle, 2, 3)) {
                                if (between(templeftHip, 10, 12) && between(temprightHip, 10, 12)) {
                                    correctPose = true;
                                    count = count + 1;
                                    int tempvar = count / 30;
                                    String progress = convertSecondsToHMSTime(tempvar);
                                    timing.setText("Progress : " + progress);
                                    if (progress.compareTo("00:00:10") > 0) {
                                        step = 5;
                                        successView.setVisibility(View.VISIBLE);
                                        Toast.makeText(MainActivity.this, "Great! You have perform this pose correctly.", Toast.LENGTH_LONG).show();
                                        mediaPlayer.start();
                                    }

                                } else {
                                    text_one.setText("Ensure hip angle between 10-12");
                                }
                            } else {
                                text_one.setText("Ensure shoulder angle between 2-3");
                            }
                        } else {
                            text_one.setText("Ensure elbow angle between 5-6");
                        }
                    } else {
                        text_one.setText("Ensure knee angle between 10-11");
                    }
                }
            }
            // 9 steps for Triangle
            //trikonasana

            if (yogaPose.equals("Trikonasana")) {

                if (step == 1) {
                    text.setText("Step1 : Stand upright on your feet. ");
                    if (between(templeftKneeAngle, 17, 17) && between(temprightKneeAngle, 17, 17))
                    {
                        if (between(templeftElbowAngle, 17, 17) && between(temprightElbowAngle, 17, 17)) {

                            correctPose = true;
                            count = count + 1;
                            int tempvar = count / 30;
                            String progress = convertSecondsToHMSTime(tempvar);
                            timing.setText("Progress : " + progress);
                            if (progress.compareTo("00:00:03") > 0) {
                                step = 2;
                                count = 0;
                                progress = convertSecondsToHMSTime(0);
                                timing.setText("Progress : " + progress);
                                buzzer.start();
                            }
                        }

                    } else {
                        text_one.setText("Ensure Knee angle between 17");
                    }
                }

                if (step == 2) {
                    text.setText("Step 2: Take a deep breath,jump and spread out the legs sideways.");
                    if (between(templeftKneeAngle, 17, 17) && between(temprightKneeAngle, 17, 17)) {
                        if (between(temprightShoulderAngle, 1, 1) && between(templeftShoulderAngle, 1, 1)) {
                            if (between(templeftElbowAngle, 16, 17) && between(temprightElbowAngle, 16, 17)) {
                                if (between(templeftHip, 15, 15) && between(temprightHip, 15, 15)) {
                                    correctPose = true;
                                    count = count + 1;
                                    int tempvar = count / 30;
                                    String progress = convertSecondsToHMSTime(tempvar);
                                    timing.setText("Progress : " + progress);
                                    if (progress.compareTo("00:00:03") > 0) {
                                        step = 3;
                                        count = 0;
                                        progress = convertSecondsToHMSTime(0);
                                        timing.setText("Progress : " + progress);
                                        buzzer.start();

                                    }
                                }
                                else {
                                    text_one.setText("Ensure Hip angle between 15");
                                }
                            }
                            else {
                                text_one.setText("Ensure Elbow angle between 16-17");
                            }
                        }
                    } else {
                        text_one.setText("Ensure Shoulder angle between 1-1");
                    }
                }
                if (step == 3) {
                    text.setText("Step 3:  Raise both the arm at the shoulder level.");
                    if (between(templeftKneeAngle, 16, 17) && between(temprightKneeAngle, 16, 17)) {
                        if (between(temprightShoulderAngle, 8, 9) && between(templeftShoulderAngle, 8, 9)) {
                            if (between(templeftElbowAngle, 16, 17) && between(temprightElbowAngle, 16, 17)) {
                                if (between(templeftHip, 15, 15) && between(temprightHip, 15, 15)) {
                                    correctPose = true;
                                    count = count + 1;
                                    int tempvar = count / 30;
                                    String progress = convertSecondsToHMSTime(tempvar);
                                    timing.setText("Progress : " + progress);
                                    if (progress.compareTo("00:00:03") > 0) {
                                        step = 4;
                                        count = 0;
                                        progress = convertSecondsToHMSTime(0);
                                        timing.setText("Progress : " + progress);
                                        buzzer.start();
                                    }
                                }
                                else {
                                    text_one.setText("Ensure Hip angle between 15");
                                }
                            } else {
                                text_one.setText("Ensure Elbow angle between 16-18");
                            }
                        } else {
                            text_one.setText("Ensure Shoulder angle between 8-9");
                        }
                    }
                }

                if (step == 4) {
                    text.setText("Step 4: Left foot points forward and the right foot points to the right,inhale and extend your torso to the right leading with right hand.");
                    if (between(templeftKneeAngle, 16, 17) && between(temprightKneeAngle, 15, 17)) {
                        if (between(temprightShoulderAngle, 10, 11) && between(templeftShoulderAngle, 8, 10)) {
                            if (between(templeftElbowAngle, 16, 17) && between(temprightElbowAngle, 16, 17)) {
                                if (between(templeftHip, 6, 7) && between(temprightHip, 13, 14)) {
                                    correctPose = true;
                                    count = count + 1;
                                    int tempvar = count / 30;
                                    String progress = convertSecondsToHMSTime(tempvar);
                                    timing.setText("Progress : " + progress);
                                    if (progress.compareTo("00:00:03") > 0) {
                                        step = 5;
                                        count = 0;
                                        progress = convertSecondsToHMSTime(0);
                                        timing.setText("Progress : " + progress);
                                        buzzer.start();
                                    }
                                }
                                else {
                                    text_one.setText("Ensure Left Hip angle between 16-17 ");
                                }
                            } else {
                                text_one.setText("Ensure Elbow angle between 16-17");
                            }
                        } else {
                            text_one.setText("Ensure Right Shoulder angle between 10-11 & Left Shoulder angle between 8-10");
                        }
                    } else {
                        text_one.setText("Ensure Knee angle between 16-17 ");
                    }
                }

                if (step == 5) {
                    text.setText("Step 5: Hold this pose for 10sec.");
                    if (between(templeftKneeAngle, 16, 17) && between(temprightKneeAngle, 15, 17)) {
                        if (between(temprightShoulderAngle, 10, 12) && between(templeftShoulderAngle, 8, 10)) {
                            if (between(templeftElbowAngle, 16, 17) && between(temprightElbowAngle, 16, 17)) {
                                if (between(templeftHip, 14, 15) && between(temprightHip, 4, 6)) {
                                    correctPose = true;
                                    count = count + 1;
                                    int tempvar = count / 30;
                                    String progress = convertSecondsToHMSTime(tempvar);
                                    timing.setText("Progress : " + progress);
                                    if (progress.compareTo("00:00:03") > 0) {
                                        step = 6;
                                        successView.setVisibility(View.VISIBLE);
                                        Toast.makeText(MainActivity.this, "Great! You have perform this pose correctly.", Toast.LENGTH_LONG).show();
                                        mediaPlayer.start();
                                    }
                                }
                                else {
                                    text_one.setText("Ensure Left Hip angle between 14-15 & Right Hip angle between 4-6 ");
                                }
                            } else {
                                text_one.setText("Ensure Elbow angle between 16-17");
                            }
                        } else {
                            text_one.setText("Ensure Right Shoulder angle between 10-12 & Left Shoulder angle between 8-10");
                        }
                    } else {
                        text_one.setText("Ensure Knee angle between 16-17 ");
                    }
                }
            }
         // 10 Ardha Chandrasana
            if (yogaPose.equals("Ardha-Chandrasana")) {

                if (step == 1) {
                    text.setText("Step1 :Stand Straight in front of Camera.");
                    if (between(templeftKneeAngle, 16, 17) && between(temprightKneeAngle, 16, 17)) {
                        if (between(templeftElbowAngle, 17, 17) && between(temprightElbowAngle, 17, 17)) {


                            correctPose = true;
                            count = count + 1;
                            int tempvar = count / 30;
                            String progress = convertSecondsToHMSTime(tempvar);
                            timing.setText("Progress : " + progress);
                            if (progress.compareTo("00:00:03") > 0) {
                                step = 2;
                                count = 0;
                                progress = convertSecondsToHMSTime(0);
                                timing.setText("Progress : " + progress);
                                buzzer.start();
                            }
                        }
                    }
                    else {
                        text_one.setText("Ensure knee angle is 16-17");
                    }

            }

                if (step == 2) {
                    text.setText("Step 2: Come into Parsvakonasana position face towards right side.");
                    if (between(templeftKneeAngle, 16, 17) && between(temprightKneeAngle, 6, 10)) {
                       // if (between(temprightShoulderAngle, 8, 9) && between(templeftShoulderAngle, 6, 10)) {
                            //  if (between(templeftElbowAngle, 16, 17) && between(temprightElbowAngle, 16, 17)) {
                       //     if (between(templeftHip, 15, 15) && between(temprightHip, 15, 15)) {
                                correctPose = true;
                                count = count + 1;
                                int tempvar = count / 30;
                                String progress = convertSecondsToHMSTime(tempvar);
                                timing.setText("Progress : " + progress);
                                if (progress.compareTo("00:00:03") > 0) {
                                    step = 3;
                                    count = 0;
                                    progress = convertSecondsToHMSTime(0);
                                    timing.setText("Progress : " + progress);
                                    buzzer.start();
                                }
                                else {
                                    text_one.setText("Ensure Left Knee angle between 16-17 and Right Knee Angle between 6-10");
                                }
                    }

                }
//                if (step == 3) {
//                    text.setText("Step 3:Take left hand to the back leg hip and reach other hand forward .");
//                    if (between(templeftKneeAngle, 15, 17) && between(temprightKneeAngle, 10, 13)) {
//                        if (between(temprightShoulderAngle, 8, 10) && between(templeftShoulderAngle, 1, 2)) {
//                          //  if (between(templeftElbowAngle, 16, 17) && between(temprightElbowAngle, 16, 17)) {
//                             //   if (between(templeftHip, 15, 15) && between(temprightHip, 15, 15)) {
//                                    correctPose = true;
//                                    count = count + 1;
//                                    int tempvar = count / 30;
//                                    String progress = convertSecondsToHMSTime(tempvar);
//                                    timing.setText("Progress : " + progress);
//                                    if (progress.compareTo("00:00:03") > 0) {
//                                        step = 4;
//                                        count = 0;
//                                        progress = convertSecondsToHMSTime(0);
//                                        timing.setText("Progress : " + progress);
//                                        buzzer.start();
//                                    }
//                                }
//                                else {
//                                    text_one.setText("Ensure Right Shoulder angle between 8-10 and Left Shoulder angle between 1-1");
//                                }
////                            } else {
////                                text_one.setText("Ensure Elbow angle between 16-18");
////                            }
//                        } else {
//                            text_one.setText("Ensure Left Knee angle between 15-17 and Right Knee angle between 6-10");
//                        }
//
//                }

                if (step == 3) {
                    text.setText("Step 3:Rise front leg sole of the foot and lift back leg up in 90 degree,left arm fingertips are underneath shoulder.");
                    if (between(templeftKneeAngle, 16, 17) && between(temprightKneeAngle, 16, 17)) {
                        if (between(temprightShoulderAngle, 10, 11) && between(templeftShoulderAngle, 1, 3)) {
                            if (between(templeftElbowAngle, 14, 16) && between(temprightElbowAngle, 16, 17)) {
                                if (between(templeftHip, 16, 17) && between(temprightHip, 7, 8)) {
                                    correctPose = true;
                                    count = count + 1;
                                    int tempvar = count / 30;
                                    String progress = convertSecondsToHMSTime(tempvar);
                                    timing.setText("Progress : " + progress);
                                    if (progress.compareTo("00:00:03") > 0) {
                                        step = 4;
                                        count = 0;
                                        progress = convertSecondsToHMSTime(0);
                                        timing.setText("Progress : " + progress);
                                        buzzer.start();
                                    }
                                }
                                else {
                                    text_one.setText("Ensure Left Hip angle between 16-17 & Right Hip angle between 7-8 ");
                                }
                            } else {
                                text_one.setText("Ensure Left Elbow angle between 14-16 and Right Elbow angle between 16-17");
                            }
                        } else {
                            text_one.setText("Ensure Right Shoulder angle between 10-12 & Left Shoulder angle between 1-3");
                        }
                    } else {
                        text_one.setText("Ensure Knee angle between 16-17 ");
                    }
                }

                if (step == 4) {
                    text.setText("Step 4:Gaze down and reach your top arm up towards the sky.");
                    if (between(templeftKneeAngle, 16, 17) && between(temprightKneeAngle, 16, 17)) {
                        if (between(temprightShoulderAngle, 10, 11) && between(templeftShoulderAngle, 7, 9)) {
                            if (between(templeftElbowAngle, 16, 17) && between(temprightElbowAngle, 16, 17)) {
                                if (between(templeftHip, 16, 17) && between(temprightHip, 6, 8)) {
                                    correctPose = true;
                                    count = count + 1;
                                    int tempvar = count / 30;
                                    String progress = convertSecondsToHMSTime(tempvar);
                                    timing.setText("Progress : " + progress);
                                    if (progress.compareTo("00:00:03") > 0) {
                                        step = 5;
                                        count = 0;
                                        progress = convertSecondsToHMSTime(0);
                                        timing.setText("Progress : " + progress);
                                        buzzer.start();
                                    }
                                }
                                else {
                                    text_one.setText("Ensure Left Hip angle between 16-17 & Right Hip angle between 7-8 ");
                                }
                            } else {
                                text_one.setText("Ensure Elbow angle between 16-17");
                            }
                        } else {
                            text_one.setText("Ensure Right Shoulder angle between 10-12 & Left Shoulder angle between 2-3");
                        }
                    } else {
                        text_one.setText("Ensure Knee angle between 16-17 ");
                    }
                }

                if (step == 5) {
                    text.setText("Step 5:Hold this pose for 10sec.");
                    if (between(templeftKneeAngle, 16, 17) && between(temprightKneeAngle, 16, 17)) {
                        if (between(temprightShoulderAngle, 10, 11) && between(templeftShoulderAngle, 7, 9)) {
                            if (between(templeftElbowAngle, 16, 17) && between(temprightElbowAngle, 16, 17)) {
                                if (between(templeftHip, 16, 17) && between(temprightHip, 6, 8)) {
                                    correctPose = true;
                                    count = count + 1;
                                    int tempvar = count / 30;
                                    String progress = convertSecondsToHMSTime(tempvar);
                                    timing.setText("Progress : " + progress);
                                    if (progress.compareTo("00:00:10") > 0) {
                                        step = 6;
                                        successView.setVisibility(View.VISIBLE);
                                        Toast.makeText(MainActivity.this, "Great! You have perform this pose correctly.", Toast.LENGTH_LONG).show();
                                        mediaPlayer.start();
                                    }
                                }
                                else {
                                    text_one.setText("Ensure Left Hip angle between 16-17 & Right Hip angle between 6-8 ");
                                }
                            } else {
                                text_one.setText("Ensure Elbow angle between 16-17");
                            }
                        } else {
                            text_one.setText("Ensure Right Shoulder angle between 10-12 & Left Shoulder angle between 2-3");
                        }
                    } else {
                        text_one.setText("Ensure Knee angle between 16-17 ");
                    }
                }
            }
        });
    }
    public static boolean between(double value, double min, double max) {
        return value >= min && value <= max;
    }
}