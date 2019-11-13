package com.example.phonite;

import android.content.Context;
import android.graphics.Rect;
import android.media.Image;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.common.FirebaseVisionPoint;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;

import java.util.List;

public class ImageAnalyzer implements ImageAnalysis.Analyzer {

    private Context context;
    private int skipper = 0;
    public final String TAG = "ImageAnalyzer";
    static public boolean analyzeFlag = false;
    public static boolean FaceDetected = false;

    public ImageAnalyzer(/*Context c*/) {
        //context = c;
    }

    private int degreesToFirebaseRotation(int degrees) {
        switch (degrees) {
            case 0:
                return FirebaseVisionImageMetadata.ROTATION_0;
            case 90:
                return FirebaseVisionImageMetadata.ROTATION_90;
            case 180:
                return FirebaseVisionImageMetadata.ROTATION_180;
            case 270:
                return FirebaseVisionImageMetadata.ROTATION_270;
            default:
                throw new IllegalArgumentException(
                        "Rotation must be 0, 90, 180, or 270.");
        }
    }

    public void setAnalyzeFlag(boolean val) {
        analyzeFlag = val;
        Log.d(TAG, "Setting analyze flag");
    }


    @Override
    public void analyze(ImageProxy imageProxy, int degrees) {
        if (analyzeFlag) {

            Image mediaImage = imageProxy.getImage();
            int rotation = degreesToFirebaseRotation(degrees);
            FirebaseVisionImage image =
                    FirebaseVisionImage.fromMediaImage(mediaImage, rotation);

            // High-accuracy landmark detection and face classification
            FirebaseVisionFaceDetectorOptions highAccuracyOpts =
                    new FirebaseVisionFaceDetectorOptions.Builder()
                            .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
                            .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                            .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                            .build();

            try {
                FirebaseVisionFaceDetector detector = FirebaseVision.getInstance()
                        .getVisionFaceDetector(highAccuracyOpts);

                detector.detectInImage(image)
                        .addOnSuccessListener(
                                new OnSuccessListener<List<FirebaseVisionFace>>() {
                                    @Override
                                    public void onSuccess(List<FirebaseVisionFace> faces) {
                                        Log.d("IMGA", "YAY");
                                        // Task completed successfully
                                        for (FirebaseVisionFace face : faces) {
                                            FaceDetected = true;
                                            // If landmark detection was enabled (mouth, ears, eyes, cheeks, and
                                            // nose available):
                                            FirebaseVisionFaceLandmark leftEar = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EAR);
                                            if (leftEar != null) {
                                                FirebaseVisionPoint leftEarPos = leftEar.getPosition();
                                                Log.d("YAY", "left ear pos" + leftEarPos.toString());
                                            }

                                            // If classification was enabled:
                                            if (face.getSmilingProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                                                float smileProb = face.getSmilingProbability();
                                                Log.d("YAY", "smile prob: " + String.valueOf(smileProb));
                                            }
                                            if (face.getRightEyeOpenProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                                                float rightEyeOpenProb = face.getRightEyeOpenProbability();
                                                Log.d("YAY", "right eye open: " + String.valueOf(rightEyeOpenProb));
                                            }

                                            // If face tracking was enabled:
                                            if (face.getTrackingId() != FirebaseVisionFace.INVALID_ID) {
                                                int id = face.getTrackingId();
                                            }
                                        }
                                    }
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("IMGA", "NAY");

                                    }
                                });

            } catch (Exception e) {
                e.printStackTrace();
            }
            analyzeFlag = false;
        }

        skipper++;
    }


// THIS BELLOW IS THE FIRST ITERATION
//    @Override
//    public void analyze(ImageProxy imageProxy, int degrees) {
////        if (imageProxy == null || imageProxy.getImage() == null) {
////            return;
////        }
//        Image mediaImage = imageProxy.getImage();
//        int rotation = degreesToFirebaseRotation(degrees);
//        FirebaseVisionImage image =
//                FirebaseVisionImage.fromMediaImage(mediaImage, rotation);
////        FirebaseVisionImage image;
//        try {
//////            Uri resUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
//////                    "://" + context.getResources())
////            image = FirebaseVisionImage.fromFilePath(context, Uri.parse("android.resource://com.example.phonite/drawable/nice"));
//            FirebaseVisionImageLabeler labeler = FirebaseVision.getInstance()
//                    .getOnDeviceImageLabeler();
//
//            labeler.processImage(image)
//                    .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
//                        @Override
//                        public void onSuccess(List<FirebaseVisionImageLabel> labels) {
//                            // Task completed successfully
//                            // ...
//                            Log.d("IMGA", "asdfghjkl;'");
//                            for (FirebaseVisionImageLabel label : labels) {
//                                String text = label.getText();
//                                String entityId = label.getEntityId();
//                                float confidence = label.getConfidence();
//                                Log.d("IMGA", "onSuccess: " + text + " " + entityId + " " + confidence);
//                            }
//
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            // Task failed with an exception
//                            // ...
//                        }
//                    });
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }

    // This is the Detection Module Alex wrote for face Detection
    public void detectFace(ImageProxy imageProxy, int degrees) {

        FirebaseVisionImage image;
        try {
            image = FirebaseVisionImage.fromFilePath(context, Uri.parse("android.resource://com.example.phonite/drawable/nice"));
            FirebaseVisionImageLabeler labeler = FirebaseVision.getInstance()
                    .getOnDeviceImageLabeler();

            Log.d("IMGA", "in detect face");

            FirebaseVisionFaceDetector detector = FirebaseVision.getInstance()
                    .getVisionFaceDetector();

            detector.detectInImage(image)
                    .addOnSuccessListener(
                            new OnSuccessListener<List<FirebaseVisionFace>>() {
                                @Override
                                public void onSuccess(List<FirebaseVisionFace> faces) {
                                    Log.d("IMGA", "YAY");
                                    // Task completed successfully
                                    for (FirebaseVisionFace face : faces) {
                                        Rect bounds = face.getBoundingBox();
                                        float rotY = face.getHeadEulerAngleY();  // Head is rotated to the right rotY degrees
                                        float rotZ = face.getHeadEulerAngleZ();  // Head is tilted sideways rotZ degrees

                                        // If landmark detection was enabled (mouth, ears, eyes, cheeks, and
                                        // nose available):
                                        FirebaseVisionFaceLandmark leftEar = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EAR);
                                        if (leftEar != null) {
                                            FirebaseVisionPoint leftEarPos = leftEar.getPosition();
                                            Log.d("LEFT EAR", leftEarPos.toString());
                                        }

                                        // If contour detection was enabled:
                                        List<FirebaseVisionPoint> leftEyeContour =
                                                face.getContour(FirebaseVisionFaceContour.LEFT_EYE).getPoints();
                                        List<FirebaseVisionPoint> upperLipBottomContour =
                                                face.getContour(FirebaseVisionFaceContour.UPPER_LIP_BOTTOM).getPoints();

                                        // If classification was enabled:
                                        if (face.getSmilingProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                                            float smileProb = face.getSmilingProbability();
                                            Log.d("SMILE PROB", String.valueOf(smileProb));
                                        }
                                        if (face.getRightEyeOpenProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                                            float rightEyeOpenProb = face.getRightEyeOpenProbability();
                                            Log.d("RIGHT EYE OPEN PROB", String.valueOf(rightEyeOpenProb));
                                        }

                                        // If face tracking was enabled:
                                        if (face.getTrackingId() != FirebaseVisionFace.INVALID_ID) {
                                            int id = face.getTrackingId();
                                        }
                                    }
                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("IMGA", "NAY");
                                    // Task failed with an exception
                                    // ...
                                }
                            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}