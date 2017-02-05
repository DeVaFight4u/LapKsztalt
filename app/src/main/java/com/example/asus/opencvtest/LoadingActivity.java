package com.example.asus.opencvtest;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

public class LoadingActivity extends AppCompatActivity {

    TextView shapeText;
    TextChange loading;
    long timeActivity;


    ImageView imageView;
    static ShapeState shapeState;
    MediaPlayer questSound;
    Thread thread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        timeActivity = System.currentTimeMillis();
        imageView = (ImageView) findViewById(R.id.shape);
        shapeText = (TextView) findViewById(R.id.text_figura);
        chooseShape();
        questSound.start();
    }

    public static ShapeState getShapeState() {
        return shapeState;
    }
    @Override
    protected void onResume() {
        super.onResume();
         thread = new Thread(new Runnable() {
             @Override
             public void run() {
                 Intent intent = new Intent(LoadingActivity.this,Counting.class);
                 while(questSound.isPlaying())
                 {
                     Log.i("Sound play: ", ""+questSound.isPlaying());
                     if(!questSound.isPlaying()) {
                         finish();
                         startActivity(intent);
                     }
                 }
             }
         });
        thread.start();
    }
    void chooseShape() // wybieranie co ma byc losowane
    {

        switch (randomGenerator())
        {
            case 0: {
                shapeState = ShapeState.TROJKAT;
                questSound = MediaPlayer.create(this, R.raw.quest_trojkat);
                shapeText.setTextColor(Color.rgb(0,208,255));
                shapeText.setText(" NIEBIESKI TRÓJKĄT");
                imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.blue_traingle));
                break;
            }
            case 1: {
                shapeState = ShapeState.KWADRAT;
                questSound = MediaPlayer.create(this, R.raw.quest_kwadrat);
                shapeText.setTextColor(Color.rgb(255,10,10));
                shapeText.setText(" CZERWONY KWADRAT");
                imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.red_square));
                break;
            }
            case 2: {
                shapeState = ShapeState.KOLO;
                questSound = MediaPlayer.create(this, R.raw.quest_kolo);
                shapeText.setTextColor(Color.rgb(255,249,10));
                shapeText.setText(" ŻÓŁTE KOŁO");
                imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.yellow_circle));
                break;
            }
        }
    }
    private int randomGenerator()
    {
        Random generator = new Random();
        int figure = generator.nextInt(3);
        return figure;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        questSound.stop();
        Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
        finish();
        startActivity(intent);
    }
}

