package com.example.phonite;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReceiveFragment extends Fragment {

    TextView textLight;
    TextView hit;
    SensorManager sensorManager;
    Sensor sensor;
    String sx;
    float prevBrightness;

    public final String TAG = "Analyze";

    public TextView modeTextR;
    public Button btnAnalyze;
    public Button btnFaceDetect;
    private Context context;

    public ReceiveFragment() {
        // empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_receive, container, false);

        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        textLight = view.findViewById(R.id.textLight);
        hit = view.findViewById(R.id.hit);
        hit.setText("");


        modeTextR = view.findViewById(R.id.modeTextR);
        btnAnalyze = view.findViewById(R.id.btnAnalyze);
        btnFaceDetect = view.findViewById(R.id.btnFaceDetect);
        context = getActivity().getApplicationContext();

        btnAnalyze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Camera click");
                ImageAnalyzer imageAnalyzer = new ImageAnalyzer();
                imageAnalyzer.analyze(null, 0);
            }
        });

        btnFaceDetect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Camera click");
                ImageAnalyzer imageAnalyzer = new ImageAnalyzer(context);
                imageAnalyzer.detectFace(null,0);
            }
        });

        return view;
    }

    public void onResume() {
        super.onResume();
        sensorManager.registerListener(lightListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void onStop() {
        super.onStop();
        sensorManager.unregisterListener(lightListener);
    }

    public SensorEventListener lightListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            if (x > 2 * prevBrightness && x > 150) {
                Log.d("onSensorChanged FLS", String.valueOf(x));
                hit.setTextColor(Color.parseColor("#ff0000"));
                hit.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "printed HIT?");
                        hit.setText("HIT");
                    }
                }, 600);
            }
            hit.post(new Runnable() {
                @Override
                public void run() {
                    hit.setText("");
                }
            });

            sx = String.valueOf(x);

            textLight.post(new Runnable() {
                @Override
                public void run() {
                    textLight.setText(sx);
                }
            });
            prevBrightness = x;
        }

        public void onAccuracyChanged(Sensor sensor, int acc) {
        }
    };

}
