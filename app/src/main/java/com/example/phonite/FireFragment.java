package com.example.phonite;


import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import static android.content.ContentValues.TAG;
import static android.content.Context.CAMERA_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class FireFragment extends Fragment {
    /**
     * Variables
     */
    MediaPlayer mp = null;
    String hello = "Hello!";
    String goodbye = "GoodBye!";

    public final String TAG = "Camera";
    public TextView modeText;
    public Button btnCamera;
    Context context = getContext();


    public FireFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
      //  super.onCreate(savedInstanceState);
      //  setContentView(R.layout.activity_main);

        View view = inflater.inflate(R.layout.fragment_fire, container, false);
        modeText = view.findViewById(R.id.modeText);
        btnCamera = view.findViewById(R.id.btnCamera);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnCamera.setEnabled(false);


                Log.d(TAG, "Camera click");
                CameraManager cameraManager = (CameraManager) context.getSystemService(context.CAMERA_SERVICE);

                try {
                    String cameraId = cameraManager.getCameraIdList()[0];
                    managerOfSound(goodbye);
                    cameraManager.setTorchMode(cameraId, true);
                    try {

                        Thread.sleep(500);

                    } catch (InterruptedException e) {
                        //System.out.println("got interrupted!");
                    }
                    cameraManager.setTorchMode(cameraId, false);


                } catch (CameraAccessException e) {
                }
                btnCamera.setEnabled(true);
                btnCamera.cancelPendingInputEvents();
            }
        });



        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fire, container, false);
    }

    /**
     * Manager of Sounds
     */
    protected void managerOfSound(String theText) {
        if (mp != null) {
            mp.reset();
            mp.release();
        }
        if (theText == hello)
            mp = MediaPlayer.create(context, R.raw.laser);
        else if (theText == goodbye)
            mp = MediaPlayer.create(context, R.raw.laser);
        else
            mp = MediaPlayer.create(context, R.raw.laser);
        mp.start();
    }

}
