package com.example.phonite;

import android.util.Log;

import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BrightnessAnalyzer implements ImageAnalysis.Analyzer {
    public final String TAG = "CameraXApp";
    private long lastAnalyzedTimestamp = 0;

    @Override
    public void analyze(ImageProxy image, int rotationDegrees) {
        long currentTimestamp = System.currentTimeMillis();
        // Calculate the average luma no more often than every second
        if (currentTimestamp - lastAnalyzedTimestamp >= TimeUnit.SECONDS.toMillis(1)) {
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            Byte[] data = toByteArray(buffer);
            // Convert the data into an array of pixel values
            ArrayList<Integer> pixels = new ArrayList<Integer>();
            for (Byte it : data) {
                pixels.add(it.intValue() & 0xFF);
            }
            Integer luma = average(pixels);
            Log.d(TAG, "Average luminosity: " + luma);
            // Update timestamp of last analyzed frame
            lastAnalyzedTimestamp = currentTimestamp;
        }
    }

    private Integer average(List<Integer> a) {
        int size = a.size();
        int sum = 0;
        for (Integer curr : a) {
            sum += curr;
        }
        return sum / size;
    }

    private Byte[] toByteArray(ByteBuffer byteBuffer) {
        byteBuffer.rewind();
        Byte[] toReturn = new Byte[byteBuffer.remaining()];
        for (int i = 0; i < toReturn.length; i++) {
            toReturn[i] = byteBuffer.get(i);
        }
        return toReturn;
    }
}
