package com.example.phonite;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.media.Image;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.google.firebase.ml.vision.objects.FirebaseVisionObject;
import com.google.firebase.ml.vision.objects.FirebaseVisionObjectDetector;
import com.google.firebase.ml.vision.objects.FirebaseVisionObjectDetectorOptions;

import java.io.IOException;
import java.util.List;

public class ImageAnalyzer implements ImageAnalysis.Analyzer {

    private Context context;

    public ImageAnalyzer (Context c){
        context = c;
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

    @Override
    public void analyze(ImageProxy imageProxy, int degrees) {
//        if (imageProxy == null || imageProxy.getImage() == null) {
//            return;
//        }
//        Image mediaImage = imageProxy.getImage();
        int rotation = degreesToFirebaseRotation(degrees);
//        FirebaseVisionImage image =
//                FirebaseVisionImage.fromMediaImage(mediaImage, rotation);

        FirebaseVisionImage image;
        try {
//            Uri resUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
//                    "://" + context.getResources())
            image = FirebaseVisionImage.fromFilePath(context, Uri.parse("android.resource://com.example.phonite/drawable/nice"));
            FirebaseVisionImageLabeler labeler = FirebaseVision.getInstance()
                    .getOnDeviceImageLabeler();

            Log.d("IMGA", "HELLLOOOOOOO");

            labeler.processImage(image)
                    .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
                        @Override
                        public void onSuccess(List<FirebaseVisionImageLabel> labels) {
                            // Task completed successfully
                            // ...
                            Log.d("IMGA", "asdfghjkl;'");
                            for (FirebaseVisionImageLabel label: labels) {
                                String text = label.getText();
                                String entityId = label.getEntityId();
                                float confidence = label.getConfidence();
                                Log.d("IMGA", "onSuccess: " + text + " " + entityId + " " + confidence);
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Task failed with an exception
                            // ...
                        }
                    });

        } catch (IOException e) {
            e.printStackTrace();
        }
//        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(Bitmap.createBitmap(100,100,Bitmap.Config.ARGB_8888));
        // Pass image to an ML Kit Vision API
        // ...

//        // Live detection and tracking
//        FirebaseVisionObjectDetectorOptions options =
//                new FirebaseVisionObjectDetectorOptions.Builder()
//                        .setDetectorMode(FirebaseVisionObjectDetectorOptions.STREAM_MODE)
//                        .enableClassification()  // Optional
//                        .build();

//        FirebaseVisionObjectDetector objectDetector =
//                FirebaseVision.getInstance().getOnDeviceObjectDetector();
//        objectDetector.processImage(image)
//                .addOnSuccessListener(
//                        new OnSuccessListener<List<FirebaseVisionObject>>() {
//                            @Override
//                            public void onSuccess(List<FirebaseVisionObject> detectedObjects) {
//                                // Task completed successfully
//                                // ...
//                                Log.d("IMGA", "onSuccess: ");
//                                // The list of detected objects contains one item if multiple object detection wasn't enabled.
//                                for (FirebaseVisionObject obj : detectedObjects) {
//                                    Integer id = obj.getTrackingId();
//                                    Rect bounds = obj.getBoundingBox();
//
//                                    // If classification was enabled:
//                                    int category = obj.getClassificationCategory();
//                                    Float confidence = obj.getClassificationConfidence();
//                                    Log.d("IMGA", "onSuccess: " + id + " " + category + " " + confidence);
//                                }
//                            }
//                        })
//                .addOnFailureListener(
//                        new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                // Task failed with an exception
//                                // ...
//                                Log.d("IMGA", "onFailure:");
//                            }
//                        });
    }
}