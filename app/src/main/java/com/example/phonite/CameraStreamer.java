package com.example.phonite;

import android.content.Context;
import android.util.Log;
import android.util.Size;
import android.view.TextureView;
import android.view.ViewGroup;

import androidx.camera.core.CameraX;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageAnalysisConfig;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.lifecycle.LifecycleOwner;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CameraStreamer implements Runnable {


    private static Context context;
    private static ExecutorService executor = Executors.newSingleThreadExecutor();
    private static boolean cameraStarted;
    public static Preview preview;
    public static ImageAnalyzer buttonConnector;

    // Have questions on how a camera works? look at this source:
    // https://medium.com/androiddevelopers/understanding-android-camera-capture-sessions-and-requests-4e54d9150295
    // https://docs.microsoft.com/en-us/dotnet/api/android.hardware.camera2.cameramanager.opencamera?view=xamarin-android-sdk-9
    public CameraStreamer(Context context) {

        CameraStreamer.context = context;
        cameraStarted = false;


    }

    public static void startTorch() {
        preview.enableTorch(!preview.isTorchOn());
    }

    public static void startCamera(LifecycleOwner lifecycleOwner, TextureView viewFinder) {
        cameraStarted = true;

        Log.d("CameraStreamer", "in start Camera");

        PreviewConfig previewConfig = new PreviewConfig.Builder()
                .setTargetResolution(new Size(1280, 720))
                .build();

        preview = new Preview(previewConfig);

        // Every time the viewfinder is updated, recompute layout
        preview.setOnPreviewOutputUpdateListener(output -> {
            Log.d("CameraStreamer", "onUpdated is being called!!! ---------------");

            ViewGroup parent = (ViewGroup) viewFinder.getParent();
            parent.removeView(viewFinder);
            parent.addView(viewFinder, 0);

            viewFinder.setSurfaceTexture(output.getSurfaceTexture());

        });


        // Setup image analysis pipeline that computes average pixel luminance
        ImageAnalysisConfig analyzerConfig = new ImageAnalysisConfig.Builder()
                .setTargetResolution(new Size(1280, 720))
                .setImageReaderMode(ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE)
                .build();

        ImageAnalysis imageAnalysis = new ImageAnalysis(analyzerConfig);

//        imageAnalysis.setAnalyzer(executor, new BrightnessAnalyzer());
        buttonConnector = new ImageAnalyzer();

        imageAnalysis.setAnalyzer(executor, buttonConnector);

        CameraX.bindToLifecycle(lifecycleOwner, imageAnalysis, preview); //, preview, imageCapture)


    }

    public ImageAnalyzer getImageAnalyzer(){
        return this.buttonConnector;
    }

    public void updateTransform() {
        // TODO: Implement camera viewfinder transformations

    }


    @Override
    public void run() {
        if (!cameraStarted) {
            return;
        }


    }
}
