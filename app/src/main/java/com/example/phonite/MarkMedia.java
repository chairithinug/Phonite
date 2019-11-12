package com.example.phonite;

import android.content.Context;
import android.media.MediaPlayer;

public class MarkMedia {

    private MediaPlayer mp = null;
    Context context;

    //Constructor
    MarkMedia(Context contextIn) {
        context = contextIn;
    }

    public void setSound(int theSound){
        if (mp != null) {
            mp.reset();
            mp.release();
        }
        mp = MediaPlayer.create(context, theSound);
    }

    public void playSound(){
        mp.start();
    }

    public void stopSound(){
        mp.reset();
        mp.release();
    }
}
