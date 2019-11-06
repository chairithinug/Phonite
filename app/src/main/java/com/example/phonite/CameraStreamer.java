    package com.example.phonite;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.media.ImageReader;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import static android.content.Context.CAMERA_SERVICE;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CameraStreamer implements Runnable {

    private boolean deviceHasCamera = false;
    private Context context;
    private CameraManager cameraManager;
    private CameraDevice frontFacingCam;
    private CameraDevice backFacingCam;
    private ImageReader flashStreamFront;
    private ImageReader flashStreamBack;
    private ImageReader faceStream;

    private final int pixHeight = 200; // TODO: find out what this is
    private final int pixWidth = 200; // TODO: find out what this is

    // Have questions on how a camera works? look at this source:
    // https://medium.com/androiddevelopers/understanding-android-camera-capture-sessions-and-requests-4e54d9150295
    // https://docs.microsoft.com/en-us/dotnet/api/android.hardware.camera2.cameramanager.opencamera?view=xamarin-android-sdk-9
    public CameraStreamer(Context context) {
        this.context = context;
        cameraManager = (CameraManager) context.getSystemService(CAMERA_SERVICE);

        // TODO
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }

        // Get front facing and back facing camera
        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
                int facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing == CameraCharacteristics.LENS_FACING_FRONT) {

                    // TODO: remove line bellow this is not the way you start a camera device
                    cameraManager.openCamera(cameraId, frontFacingCameraCallback, null);
                } else if (facing == CameraCharacteristics.LENS_FACING_BACK){

                    // TODO: remove line bellow this is not the way you start a camera device
                    cameraManager.openCamera(cameraId, backFacingCameraCallback, null);
                }

            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private int checkSelfPermission(String camera) {
        // TODO this is supposed to check the camera permission
        return 1;
    }

    private CameraDevice.StateCallback frontFacingCameraCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            // TODO: This may have to be moved. this is supposed to be linked to the camera device with start stream
            // check the first link
            flashStreamFront = ImageReader.newInstance(pixWidth, pixHeight, PixelFormat.RGBA_8888, 5);
            faceStream = ImageReader.newInstance(pixWidth, pixHeight, PixelFormat.RGBA_8888, 5);

        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            // TODO retry
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int i) {
            throw new RuntimeException("CAMERA FRONT POOPED THE BED");
        }
    };

    private CameraDevice.StateCallback backFacingCameraCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            flashStreamBack = ImageReader.newInstance(pixWidth, pixHeight, PixelFormat.RGBA_8888, 5);
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            // TODO retry
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int i) {
            throw new RuntimeException("CAMERA BACK POOPED THE BED");
        }
    };

    @Override
    public void run() {
        // If device does not have camera do not run anything.
        if(!deviceHasCamera){
            return;
        }
        // TODO this may not need to be a runnable thing


    }

    private void checkCameraHardware() {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            deviceHasCamera = true;
        } else {
            // no camera on this device
            deviceHasCamera = false;
        }
    }



}
