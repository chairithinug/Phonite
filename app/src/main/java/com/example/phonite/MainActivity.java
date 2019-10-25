package com.example.phonite;

import androidx.appcompat.app.AppCompatActivity;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraAccessException;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import android.provider.MediaStore;
import android.content.Intent;
import android.content.Context;


public class MainActivity extends AppCompatActivity {


    public final String TAG = "Camera";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    public TextView randTxt;
    public Button btnCamera;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        randTxt = findViewById(R.id.randTxt);
        btnCamera = findViewById(R.id.btnCamera);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Camera click");
                CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                try {
                    String cameraId = cameraManager.getCameraIdList()[0];
                    cameraManager.setTorchMode(cameraId, true);
                } catch (CameraAccessException e) {
                }

//                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//                }
            }
        });
    }
}
