package com.example.phonite;


import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.objects.FirebaseVisionObject;
import com.google.firebase.ml.vision.objects.FirebaseVisionObjectDetector;
import com.google.firebase.ml.vision.objects.FirebaseVisionObjectDetectorOptions;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReceiveFragment extends Fragment {

    public final String TAG = "Analyze";

    public TextView modeTextR;
    public Button btnAnalyze;
    private Context context;

    public ReceiveFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_receive, container, false);


        modeTextR = view.findViewById(R.id.modeTextR);
        btnAnalyze = view.findViewById(R.id.btnAnalyze);
        context = getActivity().getApplicationContext();

        btnAnalyze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Camera click");
                ImageAnalyzer imageAnalyzer = new ImageAnalyzer(context);
                imageAnalyzer.analyze(null,0);
            }
        });

        return view;
    }

}
