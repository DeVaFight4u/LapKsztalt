package com.example.asus.opencvtest;

/**
 * Created by ASUS on 25.10.2016.
 */

public class TimeCounting {
    private long waitTime;

    public long getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(long waitTime) {
        this.waitTime = waitTime;
    }

    void counting(long currentTime, int wait)
    {
        long time = wait-(System.currentTimeMillis()/1000-(currentTime/1000));
        isEndTime(time);
        setWaitTime(time);
    }
    void isEndTime(long time)
    {
        if(time<=0)
        {
            time = System.currentTimeMillis()/1000;
        }
    }
}
