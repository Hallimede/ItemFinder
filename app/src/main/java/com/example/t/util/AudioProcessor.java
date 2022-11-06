package com.example.t.util;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

public class AudioProcessor {
    public void process(Context context, String path) {

        try {
            InputStream open = context.getAssets().open("iattest.wav");
            byte[] buff = new byte[1280];
            while (open.available() > 0) {
                int read = open.read(buff);
                //mIat.writeAudio(buff, 0, read);
            }
        } catch (IOException e) {

        }

    }
}
