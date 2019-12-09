package com.example.phonite;

import android.content.Context;
import android.util.Log;
import android.util.Size;
import android.view.TextureView;
import android.view.ViewGroup;

import androidx.camera.core.CameraX;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageAnalysisConfig;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureConfig;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.lifecycle.LifecycleOwner;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OurCamera implements Runnable {

    public static final String TAG = "OurCamera";
    private static Context context;
    private static ExecutorService executor = Executors.newSingleThreadExecutor();
    private static boolean cameraStarted;
    public static Preview preview;
    public static ImageAnalyzer buttonConnector;

    public OurCamera(Context context) {
        OurCamera.context = context;
        cameraStarted = false;
    }

    public static void startTorch() {
        preview.enableTorch(!preview.isTorchOn());
    }



    public static ImageCapture startCamera(LifecycleOwner lifecycleOwner, TextureView viewFinder, Runnable runThisOnHit) {
        cameraStarted = true;

        Log.d(TAG, "in start Camera");

        PreviewConfig previewConfig = new PreviewConfig.Builder()
                .setTargetResolution(new Size(1440, 2560))
                .build();

        preview = new Preview(previewConfig);

        // Every time the viewfinder is updated, recompute layout
        preview.setOnPreviewOutputUpdateListener(output -> {
            Log.d("OurCamera", "onUpdated is being called!!! ---------------");

            ViewGroup parent = (ViewGroup) viewFinder.getParent();
            parent.removeView(viewFinder);
            parent.addView(viewFinder, 0);

            viewFinder.setSurfaceTexture(output.getSurfaceTexture());

        });
        ImageCaptureConfig config = new ImageCaptureConfig.Builder().build();

        ImageCapture imageCapture = new ImageCapture(config);

        CameraX.bindToLifecycle(lifecycleOwner, imageCapture, preview); //, preview, imageCapture)

        return imageCapture;
    }

    public ImageAnalyzer getImageAnalyzer() {
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