package com.example.phonite;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    public final String TAG = "MAIN";
    public Button btnCamera;
    public TextureView viewFinder;
    private MediaPlayer mp = null;
    private String hello = "Hello!";
    private String goodbye = "GoodBye!";
    private final String[] PERMISSIONS_NEEDED = {Manifest.permission.CAMERA};
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};
    private int REQUEST_CODE_PERMISSIONS = 101;
    private float prevBrightness;
    private String sx;
    private TextView textLight;
    private TextView hit;
    private SensorManager sensorManager;
    private Sensor sensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCamera = (Button) findViewById(R.id.btnCamera);

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Camera click");
                try {
                    CameraStreamer.buttonConnector.setAnalyzeFlag();
                    managerOfSound(goodbye);
                    CameraStreamer.startTorch();
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                    }
                    CameraStreamer.startTorch();
                } catch (Exception e) {
                }
                btnCamera.cancelPendingInputEvents();
            }
        });

        viewFinder = findViewById(R.id.view_finder);

        if (allPermissionsGranted()) {
            Log.d(TAG, "STARTING CAMERA!!!!!!");

            CameraStreamer.startCamera(this, viewFinder); //start camera if permission has been granted by user
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        textLight = findViewById(R.id.textLight);
        hit = findViewById(R.id.hit);
        hit.setText("");

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
                Toast.makeText(this,
                        "Permissions not granted by the user.",
                        Toast.LENGTH_SHORT).show();

                Log.d("MATIN ACT", "WE DIDNT GET PERMISSIONS GRANTED");
                finish();
            }
        }
    }

    private boolean allPermissionsGranted() {
        for (String thisPermission : PERMISSIONS_NEEDED) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, thisPermission)) {
                return false;
            }
        }
        return true;
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
            mp = MediaPlayer.create(this, R.raw.laser);
        else if (theText == goodbye)
            mp = MediaPlayer.create(this, R.raw.laser);
        else
            mp = MediaPlayer.create(this, R.raw.laser);
        mp.start();
    }

    // For brightness analyzing
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
