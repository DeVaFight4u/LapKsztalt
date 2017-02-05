package com.example.asus.opencvtest;

import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class Counting extends AppCompatActivity {

    TextView counting;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counting);
        counting = (TextView) findViewById(R.id.counting);

    }

    @Override
    protected void onResume() {
        super.onResume();
        CountDownTimer countDownTimer = new CountDownTimer(6000, 1000) {
            @Override
            public void onTick(long l) {
                counting.setText("" + (l)/1000);
            }

            @Override
            public void onFinish() {
                intent = new Intent(Counting.this, AppActivity.class);
                finish();
                startActivity(intent);
            }
        };
        countDownTimer.start();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intentt = new Intent(Counting.this, MainActivity.class);
        finish();;
        startActivity(intent);
    }
}
