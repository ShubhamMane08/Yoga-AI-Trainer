/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aiyogaguide;

import static java.lang.Math.atan2;
import static java.lang.Math.max;
import static java.lang.Math.min;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.google.mlkit.vision.common.PointF3D;
import com.aiyogaguide.GraphicOverlay.Graphic;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseLandmark;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

/** Draw the detected pose in preview. */
public class PoseGraphic extends Graphic {

  private static final float DOT_RADIUS = 8.0f;
  private static final float IN_FRAME_LIKELIHOOD_TEXT_SIZE = 100.0f;
  private static final float STROKE_WIDTH = 20.0f;
  private static final float POSE_CLASSIFICATION_TEXT_SIZE = 60.0f;
  private final Pose pose;
  private final boolean showInFrameLikelihood;
  private final boolean visualizeZ;
  private final boolean rescaleZForVisualization;
  private float zMin = Float.MAX_VALUE;
  private float zMax = Float.MIN_VALUE;
  private final Paint leftPaint;
  private final Paint rightPaint;
  private final Paint whitePaint;
  private final Paint anglePaint;
  public static boolean correctPose = false;

  PoseGraphic(
          GraphicOverlay overlay,
          Pose pose,
          boolean showInFrameLikelihood,
          boolean visualizeZ,
          boolean rescaleZForVisualization) {
    super(overlay);
    this.pose = pose;
    this.showInFrameLikelihood = showInFrameLikelihood;
    this.visualizeZ = visualizeZ;
    this.rescaleZForVisualization = rescaleZForVisualization;

    Paint classificationTextPaint = new Paint();
    classificationTextPaint.setColor(Color.WHITE);
    classificationTextPaint.setTextSize(POSE_CLASSIFICATION_TEXT_SIZE);
    classificationTextPaint.setShadowLayer(5.0f, 0f, 0f, Color.BLACK);

    whitePaint = new Paint();
    whitePaint.setStrokeWidth(STROKE_WIDTH);
    whitePaint.setColor(Color.WHITE);
    whitePaint.setTextSize(IN_FRAME_LIKELIHOOD_TEXT_SIZE);

    leftPaint = new Paint();
    leftPaint.setStrokeWidth(STROKE_WIDTH);
    leftPaint.setColor(Color.WHITE);

    anglePaint = new Paint();
    anglePaint.setAntiAlias(true);
    anglePaint.setStrokeWidth(STROKE_WIDTH);
    anglePaint.setColor(Color.YELLOW);
    anglePaint.setTextSize(IN_FRAME_LIKELIHOOD_TEXT_SIZE);

    rightPaint = new Paint();
    rightPaint.setStrokeWidth(STROKE_WIDTH);
    rightPaint.setColor(Color.WHITE);

    if(correctPose) {
      rightPaint.setColor(Color.GREEN);
      leftPaint.setColor(Color.GREEN);
      whitePaint.setColor(Color.GREEN);
    }

  }

  @Override
  public void draw(Canvas canvas) {
    List<PoseLandmark> landmarks = pose.getAllPoseLandmarks();
    if (landmarks.isEmpty()) {
      return;
    }

    // Draw all the points
    for (PoseLandmark landmark : landmarks) {
      drawPoint(canvas, landmark, whitePaint);
      if (visualizeZ && rescaleZForVisualization) {
        zMin = min(zMin, landmark.getPosition3D().getZ());
        zMax = max(zMax, landmark.getPosition3D().getZ());
      }
    }

    PoseLandmark nose = pose.getPoseLandmark(PoseLandmark.NOSE);
    PoseLandmark lefyEyeInner = pose.getPoseLandmark(PoseLandmark.LEFT_EYE_INNER);
    PoseLandmark lefyEye = pose.getPoseLandmark(PoseLandmark.LEFT_EYE);
    PoseLandmark leftEyeOuter = pose.getPoseLandmark(PoseLandmark.LEFT_EYE_OUTER);
    PoseLandmark rightEyeInner = pose.getPoseLandmark(PoseLandmark.RIGHT_EYE_INNER);
    PoseLandmark rightEye = pose.getPoseLandmark(PoseLandmark.RIGHT_EYE);
    PoseLandmark rightEyeOuter = pose.getPoseLandmark(PoseLandmark.RIGHT_EYE_OUTER);
    PoseLandmark leftEar = pose.getPoseLandmark(PoseLandmark.LEFT_EAR);
    PoseLandmark rightEar = pose.getPoseLandmark(PoseLandmark.RIGHT_EAR);
    PoseLandmark leftMouth = pose.getPoseLandmark(PoseLandmark.LEFT_MOUTH);
    PoseLandmark rightMouth = pose.getPoseLandmark(PoseLandmark.RIGHT_MOUTH);

    PoseLandmark leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER);
    PoseLandmark rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER);
    PoseLandmark leftElbow = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW);
    PoseLandmark rightElbow = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW);
    PoseLandmark leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST);
    PoseLandmark rightWrist = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST);
    PoseLandmark leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP);
    PoseLandmark rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP);
    PoseLandmark leftKnee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE);
    PoseLandmark rightKnee = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE);
    PoseLandmark leftAnkle = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE);
    PoseLandmark rightAnkle = pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE);

    PoseLandmark leftPinky = pose.getPoseLandmark(PoseLandmark.LEFT_PINKY);
    PoseLandmark rightPinky = pose.getPoseLandmark(PoseLandmark.RIGHT_PINKY);
    PoseLandmark leftIndex = pose.getPoseLandmark(PoseLandmark.LEFT_INDEX);
    PoseLandmark rightIndex = pose.getPoseLandmark(PoseLandmark.RIGHT_INDEX);
    PoseLandmark leftThumb = pose.getPoseLandmark(PoseLandmark.LEFT_THUMB);
    PoseLandmark rightThumb = pose.getPoseLandmark(PoseLandmark.RIGHT_THUMB);
    PoseLandmark leftHeel = pose.getPoseLandmark(PoseLandmark.LEFT_HEEL);
    PoseLandmark rightHeel = pose.getPoseLandmark(PoseLandmark.RIGHT_HEEL);
    PoseLandmark leftFootIndex = pose.getPoseLandmark(PoseLandmark.LEFT_FOOT_INDEX);
    PoseLandmark rightFootIndex = pose.getPoseLandmark(PoseLandmark.RIGHT_FOOT_INDEX);

    // Face
    drawLine(canvas, nose, lefyEyeInner, whitePaint);
    drawLine(canvas, lefyEyeInner, lefyEye, whitePaint);
    drawLine(canvas, lefyEye, leftEyeOuter, whitePaint);
    drawLine(canvas, leftEyeOuter, leftEar, whitePaint);
    drawLine(canvas, nose, rightEyeInner, whitePaint);
    drawLine(canvas, rightEyeInner, rightEye, whitePaint);
    drawLine(canvas, rightEye, rightEyeOuter, whitePaint);
    drawLine(canvas, rightEyeOuter, rightEar, whitePaint);
    drawLine(canvas, leftMouth, rightMouth, whitePaint);

    drawLine(canvas, leftShoulder, rightShoulder, whitePaint);
    drawLine(canvas, leftHip, rightHip, whitePaint);

    // Left body
    drawLine(canvas, leftShoulder, leftElbow, leftPaint);
    drawLine(canvas, leftElbow, leftWrist, leftPaint);
    drawLine(canvas, leftShoulder, leftHip, leftPaint);
    drawLine(canvas, leftHip, leftKnee, leftPaint);
    drawLine(canvas, leftKnee, leftAnkle, leftPaint);
    drawLine(canvas, leftWrist, leftThumb, leftPaint);
    drawLine(canvas, leftWrist, leftPinky, leftPaint);
    drawLine(canvas, leftWrist, leftIndex, leftPaint);
    drawLine(canvas, leftIndex, leftPinky, leftPaint);
    drawLine(canvas, leftAnkle, leftHeel, leftPaint);
    drawLine(canvas, leftHeel, leftFootIndex, leftPaint);

    // Right body
    drawLine(canvas, rightShoulder, rightElbow, rightPaint);
    drawLine(canvas, rightElbow, rightWrist, rightPaint);
    drawLine(canvas, rightShoulder, rightHip, rightPaint);
    drawLine(canvas, rightHip, rightKnee, rightPaint);
    drawLine(canvas, rightKnee, rightAnkle, rightPaint);
    drawLine(canvas, rightWrist, rightThumb, rightPaint);
    drawLine(canvas, rightWrist, rightPinky, rightPaint);
    drawLine(canvas, rightWrist, rightIndex, rightPaint);
    drawLine(canvas, rightIndex, rightPinky, rightPaint);
    drawLine(canvas, rightAnkle, rightHeel, rightPaint);
    drawLine(canvas, rightHeel, rightFootIndex, rightPaint);


    int leftShoulderAngle = (int) getAngle(
            Objects.requireNonNull(pose.getPoseLandmark(PoseLandmark.LEFT_HIP)),
            Objects.requireNonNull(pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)),
            Objects.requireNonNull(pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW))) / 10;

    int rightShoulderAngle = (int) getAngle(
            Objects.requireNonNull(pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)),
            Objects.requireNonNull(pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)),
            Objects.requireNonNull(pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW))) / 10;

    int leftElbowAngle = (int) getAngle(
            Objects.requireNonNull(pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)),
            Objects.requireNonNull(pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)),
            Objects.requireNonNull(pose.getPoseLandmark(PoseLandmark.LEFT_WRIST))) / 10;

    int rightElbowAngle = (int) getAngle(
            Objects.requireNonNull(pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)),
            Objects.requireNonNull(pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)),
            Objects.requireNonNull(pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST))) / 10;

    int leftHipAngle = (int) getAngle(
            Objects.requireNonNull(pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)),
            Objects.requireNonNull(pose.getPoseLandmark(PoseLandmark.LEFT_HIP)),
            Objects.requireNonNull(pose.getPoseLandmark(PoseLandmark.LEFT_KNEE))) / 10;

    int rightHipAngle = (int) getAngle(
            Objects.requireNonNull(pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)),
            Objects.requireNonNull(pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)),
            Objects.requireNonNull(pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE))) / 10;

    int leftKneeAngle = (int) getAngle(
            Objects.requireNonNull(pose.getPoseLandmark(PoseLandmark.LEFT_HIP)),
            Objects.requireNonNull(pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)),
            Objects.requireNonNull(pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE))) / 10;

    int rightKneeAngle = (int) getAngle(
            Objects.requireNonNull(pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)),
            Objects.requireNonNull(pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE)),
            Objects.requireNonNull(pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE))) / 10;

    int torsoAngle = (int) getAngle(
            Objects.requireNonNull(pose.getPoseLandmark(PoseLandmark.LEFT_HIP)),
            Objects.requireNonNull(pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)),
            Objects.requireNonNull(pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER))) / 10;


    // Draw the angles on the canvas
    drawAngle(canvas, anglePaint, leftShoulder, leftShoulderAngle);
    drawAngle(canvas, anglePaint, rightShoulder, rightShoulderAngle);
    drawAngle(canvas, anglePaint, leftElbow, leftElbowAngle);
    drawAngle(canvas, anglePaint, rightElbow, rightElbowAngle);
    drawAngle(canvas, anglePaint, leftHip, leftHipAngle);
    drawAngle(canvas, anglePaint, rightHip, rightHipAngle);
    drawAngle(canvas, anglePaint, leftKnee, leftKneeAngle);
    drawAngle(canvas, anglePaint, rightKnee, rightKneeAngle);
    // drawAngle(canvas, leftPaint, pose.getPoseLandmark(PoseLandmark.LEFT_HIP), torsoAngle);

    // Draw inFrameLikelihood for all points
    if (showInFrameLikelihood) {
      for (PoseLandmark landmark : landmarks) {
        canvas.drawText(
                String.format(Locale.US, "%.2f", landmark.getInFrameLikelihood()),
                translateX(landmark.getPosition().x),
                translateY(landmark.getPosition().y),
                whitePaint);
      }
    }
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

  // Function to draw angles on the canvas
  void drawAngle(Canvas canvas, Paint paint, PoseLandmark point, int angle) {
    canvas.drawText(
            String.format(Locale.US, "%d°", angle),
            translateX(point.getPosition().x),
            translateY(point.getPosition().y),
            paint);
  }

  void drawPoint(Canvas canvas, PoseLandmark landmark, Paint paint) {
    PointF3D point = landmark.getPosition3D();
    updatePaintColorByZValue(
            paint, canvas, visualizeZ, rescaleZForVisualization, point.getZ(), zMin, zMax);
    canvas.drawCircle(translateX(point.getX()), translateY(point.getY()), DOT_RADIUS, paint);
  }

  void drawLine(Canvas canvas, PoseLandmark startLandmark, PoseLandmark endLandmark, Paint paint) {
    PointF3D start = startLandmark.getPosition3D();
    PointF3D end = endLandmark.getPosition3D();

    // Gets average z for the current body line
    float avgZInImagePixel = (start.getZ() + end.getZ()) / 2;
    updatePaintColorByZValue(
            paint, canvas, visualizeZ, rescaleZForVisualization, avgZInImagePixel, zMin, zMax);

    canvas.drawLine(
            translateX(start.getX()),
            translateY(start.getY()),
            translateX(end.getX()),
            translateY(end.getY()),
            paint);
  }
}
