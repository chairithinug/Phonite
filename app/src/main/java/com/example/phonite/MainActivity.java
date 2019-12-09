package com.example.phonite;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.FlashMode;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageProxy;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.PersonGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import org.json.JSONArray;

public class MainActivity extends AppCompatActivity {

    public final String TAG = "MainActivity";
    public Button btnFire;
    public Button btnDetect;
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
    private FireSoundRunnable fireSoundRunnable;
    private HitSoundRunnable hitSoundRunnable;
    private ScanRunnable scanRunnable;
    private MarkMedia hitSound;
    private MarkMedia missSound;
    private ImageView blood_img;
    private RunMeOnHit runMeOnHit;
    private static Context appContext;
    private int scanCount = 0;
    public ImageView crossHair;
    private FaceServiceClient sFaceServiceClient;
    public PersonGroup players;
    private ImageCapture imgCap;
    private EditText editUsername;
    private RequestQueue queue;

    // The image selected to detect.
    private Bitmap mBitmap;
    private String currentFaceId;

    // IMGUR
    private String authorization = "Client-ID 546c25a59c58ad7";
    private String accessToken = "77ba3d75255e7e41a2c7898c6d03fb4028383a7e";

    // Azure
    private String AzureSubscriptionKey = "f6e89adaa6c34d22b3db1a42ae7278d9";

    private ArrayList<String> hashDelete = new ArrayList<>();

    private void imgurAPI(ImageProxy image) {
        String ApiURL = "https://api.imgur.com/3/upload";
        //  Map<String, String> params = new HashMap<>();
        //params.put("url", url);

        String TAG = "IMGUR";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, ApiURL, new JSONObject(), new Response.Listener<JSONObject>() {
            public void onResponse(JSONObject response) {
                try {
                    Log.d(TAG, response.toString());
                    JSONObject nested = (JSONObject) response.get("data");
                    hashDelete.add(nested.get("deletehash").toString()); // Add to "to be deleted list" after finish
                    Log.d(TAG, hashDelete.toString());
                    String[] arr = nested.get("link").toString().split("/");
                    String ending = arr[arr.length - 1];
                    String url = "https://i.imgur.com/" + ending;
                    currentFaceId = detect(url);
                    if(currentFaceId == null){

                    }
                    Log.d("fffff", "current Face ID: " + currentFaceId);
                } catch (Exception exception) {
                    Log.d(TAG, "JSON EXCEPTION for request");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: " + error.toString());
                try {
//                    int frontIndex = error.getMessage().indexOf("link");
//                    int backIndex = error.getMessage().indexOf("success");
//                    String link = error.getMessage().substring(frontIndex + 7, backIndex - 4);
//                    Log.d(TAG, link);
                    //Call Detect here with the link
                    try {
//                        String[] arr = link.split("/");
//                        String ending = arr[arr.length - 1];
//                        String url = "https://i.imgur.com/" + ending;
//                        currentFaceId = detect(url);
                    } catch (Exception e) {
                    }

                } catch (Exception e) {

                }
            }
        }) {
            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
//                headers.put("Authorization", authorization);
                headers.put("Authorization", "Bearer " + accessToken);
                return headers;
            }

            @Override
            public byte[] getBody() {
                ByteBuffer bb;
                try {
                    bb = image.getPlanes()[0].getBuffer();
                } catch (Exception e) {
                    return null;
                }
                byte[] buf = new byte[bb.remaining()];
                bb.get(buf);

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                //       yuvImage.compressToJpeg(new Rect(49, 7, 273, 231), 74, out);
                byte[] imageBytes = out.toByteArray();
                try {
                    image.close();
                } catch (Exception e) {
                    Log.d(TAG, "failed to close image");
                }
                return buf;
//                return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            }

        };
        queue = Volley.newRequestQueue(MainActivity.getAppContext());
        queue.add(jsonObjectRequest);

    }

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
        fireSoundRunnable = new FireSoundRunnable();
        hitSoundRunnable = new HitSoundRunnable();
        scanRunnable = new ScanRunnable();
        runMeOnHit = new RunMeOnHit();
        appContext = getApplicationContext();
        //btnFire.setText("Scan");
        crossHair = (ImageView) findViewById(R.id.crosshair);
        crossHair.setVisibility(View.INVISIBLE);
        editUsername = findViewById(R.id.editUsername);
        sFaceServiceClient = new FaceServiceRestClient(getString(R.string.endpoint), getString(R.string.subscription_key));

        // Create an empty PersonGroup with "recognition_02" model
        String personGroupId = "mypersongroupid";
        players = new PersonGroup();
        players.name = "players";
        //await faceClient.PersonGroup.CreateAsync(personGroupId, "My Person Group Name", recognitionModel: "recognition_02");

        // Button that test the face detection and recognition
        btnDetect = (Button) findViewById(R.id.detectButton);
        btnDetect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.d(TAG, "Detect click");
//                try {
//                    currentFaceId = detect("https://i.imgur.com/SFjMNiW.jpg");
//                } catch (Exception e) {
//                }
                if (!KappaServerRequest.created) {
                    KappaServerRequest ksr = new KappaServerRequest(editUsername.getText().toString());
                    ksr.createPlayer();
                    editUsername.setEnabled(false);
                }
            }
        });


        viewFinder = findViewById(R.id.view_finder);

        while (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
        imgCap = OurCamera.startCamera(this, viewFinder, runMeOnHit); //start camera if permission has been granted by user
        imgCap.setFlashMode(FlashMode.OFF);

        sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        textLight = findViewById(R.id.textLight);
        textLight.setTextColor(Color.WHITE);
        hit = findViewById(R.id.hit);
        hit.setText("");

        sensorManager.registerListener(lightListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);


        btnFire.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                try{
                    new Thread(fireSoundRunnable).start();
                } catch(Exception e){

                }
                Executor ex = new Executor() {
                    @Override
                    public void execute(Runnable runnable) {
                        runnable.run();
                    }
                };
                imgCap.takePicture(ex, new ImageCapture.OnImageCapturedListener() {
                    @Override
                    public void onCaptureSuccess(ImageProxy image, int rotationDegrees) {
                        imgurAPI(image); //Start Imgur API
                    }
                });
                /*
                if (scanCount >= 5) {

                    Log.d(TAG, "Camera click");
                    try {
                        new Thread(fireRunnable).start();
                    } catch (Exception e) {
                    }
                    btnFire.cancelPendingInputEvents();
                } else {
                    ImageAnalyzer.setUsername(editUsername.getText().toString());
                    try {
                        new Thread(scanRunnable).start();
                    } catch (Exception e) {
                    }
                    scanCount++;
                    if (scanCount == 3) {
                        //Transition to ready up stage
                        btnFire.setText("Everybody Ready?");
                    } else if (scanCount == 4) {
                        //Get other players here
                        getRatios();
                        crossHair.setVisibility(View.VISIBLE);
                        btnFire.setText("FIRE");
                    }
                }
                  */
            }
        });

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
                OurCamera.startCamera(this, viewFinder, runMeOnHit);
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


    public class FireSoundRunnable implements Runnable {
        @Override
        public void run() {
            Log.d("HITS", "Inside FireRunnable");
            //OurCamera.startTorch(); // start torch
            //final boolean NOT_SCANNING = false;
           // OurCamera.buttonConnector.setAnalyzeFlag(NOT_SCANNING);
            //OurCamera.startTorch(); // stops torch
            missSound.playSound();

        }
    }

    public class HitSoundRunnable implements Runnable {
        @Override
        public void run() {
            Log.d("HITS", "Inside FireRunnable");
            //OurCamera.startTorch(); // start torch
            //final boolean NOT_SCANNING = false;
            // OurCamera.buttonConnector.setAnalyzeFlag(NOT_SCANNING);
            //OurCamera.startTorch(); // stops torch
            hitSound.playSound();

        }
    }

    public class ScanRunnable implements Runnable {
        @Override
        public void run() {
            final boolean SCANNING = true;
            OurCamera.buttonConnector.setAnalyzeFlag(SCANNING);
            //     missSound.playSound();
        }
    }

    public class RunMeOnHit implements Runnable {
        @Override
        public void run() {
            // IF YOU COMMENT ME IN THE SOUND WILL FLIP FLOP IN BETWEEN THE TWO
//            hitSound.playSound();
        }
    }

    public static Context getAppContext() {
        return appContext;
    }

    /* Face Recognition Stuff */

    // PersonGroup id: phonite_gang
    // sub key1: f6e89adaa6c34d22b3db1a42ae7278d9

    // Alex person id:
    //    "personId":           "db583e27-7b91-4550-b894-ed0713cef004"
    //    "persistedFaceId":    "9f375bcc-baff-47d6-8c4c-7a35dc86020a"
    //    "persistedFaceId":    "f1c34521-1d73-4586-89a0-f71c29b13f52"
    //     Tested face id:      "1bf28436-41c1-464f-bc8c-d24fc3e40d02"

    // Returns a face id
    private String detect(String url) {
        String ApiURL = "https://centralus.api.cognitive.microsoft.com/face/v1.0/detect?returnFaceId=true&returnFaceLandmarks=false&recognitionModel=recognition_02&returnRecognitionModel=false&detectionModel=detection_02";
        Map<String, String> params = new HashMap<>();
        params.put("url", url);
        Log.d("URL: ", url);
        String TAG = "Azure";
        CustomJsonRequest jsonObjectRequest = new CustomJsonRequest(Request.Method.POST, ApiURL, new JSONObject(params), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
//                Log.d(TAG, response.toString());
                try {
                    Log.d(TAG, response.get(0).toString());
                    JSONObject obj = response.getJSONObject(0);

                    currentFaceId = obj.getString("faceId");
                    bloodSplatter(blood_img);

                    try{
                        new Thread(hitSoundRunnable).start();
                    } catch(Exception e){

                    }

                    identify(currentFaceId);
                } catch (Exception exception) {
                    Log.d(TAG, "JSON EXCEPTION for request");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, error.toString());

            }
        }) {
            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Ocp-Apim-Subscription-Key", AzureSubscriptionKey);
                return headers;
            }

        };
        queue = Volley.newRequestQueue(MainActivity.getAppContext());
        queue.add(jsonObjectRequest);

        return currentFaceId;
    }

    private void identify(String id) {
        String ApiURL = "https://centralus.api.cognitive.microsoft.com/face/v1.0/identify";
        JSONObject jsonBody = new JSONObject();
        try {
            Log.d("trying ID", id);
            ArrayList<String> list = new ArrayList<String>();
            list.add(id);
            JSONArray jsArray = new JSONArray(list);
            jsonBody.put("personGroupId", "phonite_gang");
            jsonBody.put("faceIds", jsArray);
            jsonBody.put("maxNumOfCandidatesReturned", 1);
            jsonBody.put("confidenceThreshold", 0.5);
        } catch (Exception e) {
            Log.d(TAG, "OOPS - creating body of request");
        }
        CustomJsonRequest jsonObjectRequest = new CustomJsonRequest(Request.Method.POST, ApiURL, jsonBody, new Response.Listener<JSONArray>() {
            public void onResponse(JSONArray response) {
                try {
                    Log.d(TAG, response.toString());
                    currentFaceId = response.toString();
                } catch (Exception exception) {
                    Log.d(TAG, "JSON EXCEPTION for request");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: " + error.toString());
            }
        }) {
            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Ocp-Apim-Subscription-Key", "f6e89adaa6c34d22b3db1a42ae7278d9");
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        queue = Volley.newRequestQueue(MainActivity.getAppContext());
        queue.add(jsonObjectRequest);
    }
}
