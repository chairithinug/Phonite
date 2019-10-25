package com.example.phonite;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {

    /**
     * Variables
     */
    MediaPlayer mp = null;
    String hello = "Hello!";
    String goodbye = "GoodBye!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        /**
         * Talking with the buttonHello
         */
        final Button buttonHello = (Button) findViewById(R.id.idHello);
        buttonHello.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                managerOfSound(goodbye);
            } // END onClick()
        }); // END buttonHello

        /**
         * Talking with the buttonGoodBye
         */
        final Button buttonGoodBye = (Button) findViewById(R.id.idGoodBye);
        buttonGoodBye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // camera.stopPreview();
                managerOfSound(goodbye);
            } // END onClick()
        }); // END buttonGoodBye
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

}
