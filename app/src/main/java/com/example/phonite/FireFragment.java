package com.example.phonite;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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
    private Context context;
    public TextureView viewFinder;
    private final String[] PERMISSIONS_NEEDED = {Manifest.permission.CAMERA};
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};
    private int REQUEST_CODE_PERMISSIONS = 101;
    TextureView textureView;

    public FireFragment() {
        // Required empty public constructor
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 10 number comes from here https://codelabs.developers.google.com/codelabs/camerax-getting-started/#4
        if (requestCode == 10) {
            if (allPermissionsGranted()) {
                CameraStreamer.startCamera(this, viewFinder);
                Log.d("MATIN ACT", "WE GET PERMISSIONS GRANTED");
            } else {
                Toast.makeText(getContext(),
                        "Permissions not granted by the user.",
                        Toast.LENGTH_SHORT).show();

                Log.d("MATIN ACT", "WE DIDNT GET PERMISSIONS GRANTED");
                getActivity().finish();
            }
        }
    }

    private boolean allPermissionsGranted() {
        for (String thisPermission : PERMISSIONS_NEEDED) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(getContext(), thisPermission)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //  super.onCreate(savedInstanceState);
        //  setContentView(R.layout.activity_main);
        View view = inflater.inflate(R.layout.fragment_fire, container, false);
        modeText = view.findViewById(R.id.modeText);
        btnCamera = view.findViewById(R.id.btnCamera);
        context = getActivity().getApplicationContext();

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                btnCamera.setEnabled(false);
                Log.d(TAG, "Camera click");
                //CameraManager cameraManager = (CameraManager) context.getSystemService(context.CAMERA_SERVICE);
                try {
                    //  String cameraId = cameraManager.getCameraIdList()[0];
                    managerOfSound(goodbye);
                    //  cameraManager.setTorchMode(cameraId, true);
                    CameraStreamer.startTorch();
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        //System.out.println("got interrupted!");
                    }
                    CameraStreamer.startTorch();
                    //cameraManager.setTorchMode(cameraId, false);
                } catch (Exception e) {
                }/*
                 */
//                btnCamera.setEnabled(true);
                btnCamera.cancelPendingInputEvents();
            }
        });
        // Inflate the layout for this fragment

        viewFinder = view.findViewById(R.id.view_finder);


        if (allPermissionsGranted()) {
            Log.d(TAG, "STARTING CAMERA!!!!!!");

            CameraStreamer.startCamera(getActivity(), viewFinder); //start camera if permission has been granted by user
        } else {
            ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
        return view;
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
