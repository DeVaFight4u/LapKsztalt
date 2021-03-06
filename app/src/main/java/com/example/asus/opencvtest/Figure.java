package com.example.asus.opencvtest;

import android.media.MediaPlayer;

import org.opencv.core.Scalar;

/**
 * Created by ASUS on 29.08.2016.
 */
public class Figure {
    MediaPlayer sound;
    MediaPlayer questSound;
    int verticles;

    public boolean isStartedPlay() {
        return startedPlay;
    }

    boolean startedPlay;
    public  Scalar color1;
    public  Scalar color2;
    boolean detected;


    Figure(MediaPlayer sound, Scalar col1, Scalar col2) {
        this(sound, 0, col1, col2);
    }

    Figure(MediaPlayer sound, int verticles, Scalar col1, Scalar col2)
    {
        this.sound = sound;
        this.verticles = verticles;
        this.color1 = col1;
        this.color2 = col2;
        detected = false;
        startedPlay = false;
    }
    void SoundPlay()
    {
        if(!sound.isPlaying()&& detected)
            sound.start();
    }
    void questSoundPlay()
    {
        if(!startedPlay&&!questSound.isPlaying())
        {
            startedPlay = true;
            questSound.start();
        }
    }

}
