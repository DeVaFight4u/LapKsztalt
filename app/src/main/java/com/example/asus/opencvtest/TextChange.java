package com.example.asus.opencvtest;

import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.widget.TextView;

import static android.os.CountDownTimer.*;

/**
 * Created by ASUS on 31.10.2016.
 */

public class TextChange extends CountDownTimer {
    long activityStartTime;

    TextView textView;
    TimeCounting timeCounting;
    public boolean countEnd;

    public TextChange(long millisInFuture, long countDownInterval, long activityStartTime, TextView textView ) {
        super(millisInFuture, countDownInterval);
        this.activityStartTime = activityStartTime;
        this.textView = textView;
        timeCounting = new TimeCounting();
        countEnd = false;
    }

    public void setCountEnd(boolean countEnd) {
        this.countEnd = countEnd;
    }

    public TextView getTextView() {
        return textView;
    }

    public void setTextView(TextView textView) {
        this.textView = textView;
    }

    public boolean isCountEnd() {
        return countEnd;
    }


    @Override
    public void onTick(long l) {



    }

    @Override
    public void onFinish() {
    }
}

