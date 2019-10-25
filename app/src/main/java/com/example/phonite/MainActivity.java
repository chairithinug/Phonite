package com.example.phonite;

import androidx.appcompat.app.AppCompatActivity;
import android.hardware.camera2;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    public final String TAG = "MAIN";
    public TextView tvTime;
    public Button btnSt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvTime = (TextView) findViewById(R.id.tvTime);
        btnSt = (Button) findViewById(R.id.btnSt);


    }
}
