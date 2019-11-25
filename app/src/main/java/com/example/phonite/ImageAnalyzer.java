package com.example.phonite;

import android.content.Context;
import android.graphics.Rect;
import android.media.FaceDetector;
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

import java.util.ArrayList;
import java.util.List;

public class ImageAnalyzer implements ImageAnalysis.Analyzer {

    private Context context;
    private int skipper = 0;
    public final String TAG = "ImageAnalyzer";
    static private boolean analyzeFlag = false;
    public static boolean FaceDetected = false;
    private Runnable  doThisOnHit;

    public ImageAnalyzer(Runnable doThisOnHit) {
        this.doThisOnHit = doThisOnHit;
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

    public void setAnalyzeFlag() {
        analyzeFlag = true;
        Log.d(TAG, "Setting analyze flag");
        while (analyzeFlag){
            try {
                Thread.currentThread().sleep(250);
            }catch( Exception ex){

            }
        }
    }

    public boolean getAnalyzeFlag(){
        return analyzeFlag;
    }

    private static ArrayList<ArrayList<Double>> ratios;

    @Override
    public void analyze(ImageProxy imageProxy, int degrees) {

        // TESTING START
        if (ratios == null){
            ratios = new  ArrayList<ArrayList<Double>>();
        }
        // TESTING END

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
                            .setMinFaceSize(0.15f)
                            .enableTracking()
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


                                            if (face.getTrackingId() != FirebaseVisionFace.INVALID_ID) {
                                                int faceId = face.getTrackingId();
                                                Log.d("YAY", "FaceID: " + faceId);
                                            }

                                            FirebaseVisionPoint leftEyePos = null;
                                            FirebaseVisionPoint rightEyePos = null;
                                            FirebaseVisionPoint leftEarPos = null;
                                            FirebaseVisionPoint rightEarPos = null;
                                            FirebaseVisionPoint leftCheekPos = null;
                                            FirebaseVisionPoint rightCheekPos = null;
                                            FirebaseVisionPoint mouthLeftPos = null;
                                            FirebaseVisionPoint mouthRightPos = null;
                                            FirebaseVisionPoint mouthBottomPos = null;
                                            FirebaseVisionPoint noseBasePos = null;

                                            //Left Side
                                            FirebaseVisionFaceLandmark leftEye = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EYE);
                                            if(leftEye != null){
                                                leftEyePos = leftEye.getPosition();
                                                Log.d("YAY", "Left Eye Pos" + leftEyePos.toString());
                                            }
                                            FirebaseVisionFaceLandmark rightEye = face.getLandmark(FirebaseVisionFaceLandmark.RIGHT_EYE);
                                            if (rightEye != null) {
                                                rightEyePos = rightEye.getPosition();
                                                Log.d("YAY", "left ear pos" + rightEyePos.toString());
                                            }

                                            FirebaseVisionFaceLandmark leftEar = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EAR);
                                            if (leftEar != null) {
                                                leftEarPos = leftEar.getPosition();
                                                Log.d("YAY", "left ear pos" + leftEarPos.toString());
                                            }
                                            FirebaseVisionFaceLandmark rightEar = face.getLandmark(FirebaseVisionFaceLandmark.RIGHT_EAR);
                                            if (rightEar != null) {
                                                rightEarPos = rightEar.getPosition();
                                                Log.d("YAY", "left ear pos" + rightEarPos.toString());
                                            }

                                            FirebaseVisionFaceLandmark leftCheek = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_CHEEK);
                                            if (leftCheek != null) {
                                                leftCheekPos = leftCheek.getPosition();
                                                Log.d("YAY", "left ear pos" + leftCheekPos.toString());
                                            }
                                            FirebaseVisionFaceLandmark rightCheek = face.getLandmark(FirebaseVisionFaceLandmark.RIGHT_CHEEK);
                                            if (rightCheek != null) {
                                                rightCheekPos = rightCheek.getPosition();
                                                Log.d("YAY", "left ear pos" + rightCheekPos.toString());
                                            }

                                            FirebaseVisionFaceLandmark mouthLeft = face.getLandmark(FirebaseVisionFaceLandmark.MOUTH_LEFT);
                                            if (mouthLeft != null) {
                                                mouthLeftPos = mouthLeft.getPosition();
                                                Log.d("YAY", "left ear pos" + mouthLeftPos.toString());
                                            }
                                            FirebaseVisionFaceLandmark mouthRight = face.getLandmark(FirebaseVisionFaceLandmark.MOUTH_RIGHT);
                                            if (mouthRight != null) {
                                                mouthRightPos = mouthRight.getPosition();
                                                Log.d("YAY", "left ear pos" + mouthRightPos.toString());
                                            }

                                            FirebaseVisionFaceLandmark mouthBottom = face.getLandmark(FirebaseVisionFaceLandmark.MOUTH_BOTTOM);
                                            if (mouthBottom != null) {
                                                mouthBottomPos = mouthBottom.getPosition();
                                                Log.d("YAY", "left ear pos" + mouthBottomPos.toString());
                                            }

                                            FirebaseVisionFaceLandmark noseBase = face.getLandmark(FirebaseVisionFaceLandmark.NOSE_BASE);
                                            if (noseBase != null) {
                                                noseBasePos = noseBase.getPosition();
                                                Log.d("YAY", "left ear pos" + noseBasePos.toString());
                                            }



                                            FirebaseVisionPoint[] positions = {leftEyePos, rightEyePos, leftEarPos,
                                                    rightEarPos, leftCheekPos, rightCheekPos, mouthLeftPos, mouthRightPos,
                                                    mouthBottomPos, noseBasePos};

                                            ArrayList<Double> currentRatios = new ArrayList<Double>();

                                            for(int m = 0; m< positions.length; m++) {
                                                for (int i = 0; i < positions.length; i++) {
                                                    for (int j = 0; j < positions.length; j++) {
                                                        //RATIOS
                                                        try{
                                                            double ratio = getRatio(positions[m], positions[i], positions[j]);
                                                            currentRatios.add(ratio);


                                                        } catch(Exception e){

                                                        }
                                                    }
                                                }
                                            }
                                            ratios.add(currentRatios);

                                            int totalTally = 0;
                                            Double totalDiff = 0.0;
                                            double threshhold = 0.1;
                                            try{
                                                if(ratios.size() > 1){
                                                    for(int i=0; i<ratios.get(ratios.size()-1).size(); i++){

                                                        double currentDifferez = Math.abs(ratios.get(ratios.size()-1).get(i)-ratios.get(0).get(i));;

                                                        if(currentDifferez < 1 && currentDifferez > 0){
                                                            totalDiff += currentDifferez;
                                                        }

                                                        if(  currentDifferez < threshhold){
                                                            totalTally++;
                                                        }
                                                    }
                                                }
                                            }catch (Exception e){

                                            }

                                            Log.d("RATIOS", "RATIO #" + (ratios.size()) + " : " + totalTally);
                                            Log.d("RATIOS", "TALLY TALLY #" + (ratios.size()) + " : " + totalDiff.toString());

                                            int MARK_THRESHOLD = 90;

                                            if(MARK_THRESHOLD - totalDiff > 0) {
                                                Log.d("RATIOS", " OH HI MARK");
                                            }

                                            new Thread(doThisOnHit).run();

                                            break;
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
        }

        analyzeFlag = false;
        skipper++;
    }

    private static double getRatio(FirebaseVisionPoint pntOne, FirebaseVisionPoint pmtTwo,
                                   FirebaseVisionPoint pntThree){

        double eye2eyeX = Math.pow((pntOne.getX() - pmtTwo.getX()), 2);
        double eye2eyeY = Math.pow((pntOne.getY() - pmtTwo.getY()), 2);
        double eye2eye = Math.sqrt(eye2eyeX + eye2eyeY);

        double leftEye2MouthX = Math.pow((pntThree.getX() - pntOne.getX()), 2);
        double leftEye2MouthY = Math.pow((pntThree.getY() - pntOne.getY()), 2);
        double leftEye2Mouth = Math.sqrt(leftEye2MouthX + leftEye2MouthY);

        return eye2eye / leftEye2Mouth;



    }


}