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
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    public final String TAG = "MainActivity";
    public Button btnFire;
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
    private FireRunnable fireRunnable;
    private ScanRunnable scanRunnable;
    private MarkMedia hitSound;
    private MarkMedia missSound;
    private ImageView blood_img;
    private RunMeOnHit runMeOnHit;
    private static Context appContext;
    private int scanCount = 0;
    public ImageView crossHair;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hitSound = new MarkMedia(getApplicationContext());
        missSound = new MarkMedia(getApplicationContext());
        hitSound.setSound(R.raw.hitmarker);
        missSound.setSound(R.raw.laser);
        blood_img = (ImageView) findViewById(R.id.blood);
        btnFire = (Button) findViewById(R.id.btnFire);
        fireRunnable = new FireRunnable();
        scanRunnable = new ScanRunnable();
        runMeOnHit = new RunMeOnHit();
        appContext = getApplicationContext();
        btnFire.setText("Scan");
        crossHair = (ImageView) findViewById(R.id.crosshair);
        crossHair.setVisibility(View.INVISIBLE);

        btnFire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (scanCount > 2) {

                    Log.d(TAG, "Camera click");
                    try {
                        new Thread(fireRunnable).start();
                    } catch (Exception e) {
                    }
                    btnFire.cancelPendingInputEvents();
                } else {
                    try {
                        new Thread(scanRunnable).start();
                    } catch (Exception e) {
                    }
                    scanCount++;
                    if (scanCount > 2) {
                        btnFire.setText("FIRE");
                        crossHair.setVisibility(View.VISIBLE);
                    }
                }

            }
        });

        viewFinder = findViewById(R.id.view_finder);

        if (allPermissionsGranted()) {
            Log.d(TAG, "STARTING CAMERA!!!!!!");
            CameraStreamer.startCamera(this, viewFinder, runMeOnHit); //start camera if permission has been granted by user
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        textLight = findViewById(R.id.textLight);
        textLight.setTextColor(Color.WHITE);
        hit = findViewById(R.id.hit);
        hit.setText("");

        sensorManager.registerListener(lightListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void bloodSplatter(ImageView blood_img) {
        blood_img.setVisibility(View.VISIBLE);
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(2000);

        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation animation) {
                blood_img.setVisibility(View.GONE);
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationStart(Animation animation) {
            }
        });

        blood_img.startAnimation(fadeOut);
    }

    @Override
    public void onStop() {
        super.onStop();
        sensorManager.unregisterListener(lightListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 10 number comes from here https://codelabs.developers.google.com/codelabs/camerax-getting-started/#4
        if (requestCode == 10) {
            if (allPermissionsGranted()) {
                CameraStreamer.startCamera(this, viewFinder, runMeOnHit);
                Log.d(TAG, "WE GET PERMISSIONS GRANTED");
            } else {
                Toast.makeText(this,
                        "Permissions not granted by the user.",
                        Toast.LENGTH_SHORT).show();

                Log.d(TAG, "WE DIDNT GET PERMISSIONS GRANTED");
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


    public class FireRunnable implements Runnable {
        @Override
        public void run() {
            Log.d("HITS", "Inside FireRunnable");
            //CameraStreamer.startTorch(); // start torch
            final boolean NOT_SCANNING = false;
            CameraStreamer.buttonConnector.setAnalyzeFlag(NOT_SCANNING);
            //CameraStreamer.startTorch(); // stops torch
            missSound.playSound();
        }
    }

    public class ScanRunnable implements Runnable {
        @Override
        public void run() {
            final boolean SCANNING = true;
            CameraStreamer.buttonConnector.setAnalyzeFlag(SCANNING);
            missSound.playSound();
        }
    }

    public class RunMeOnHit implements Runnable {
        @Override
        public void run() {
            // IF YOU COMMENT ME IN THE SOUND WILL FLIP FLOP IN BETWEEN THE TWO
//            hitSound.playSound();
            bloodSplatter(blood_img);
        }
    }

    public static Context getAppContext() {
        return appContext;
    }

}