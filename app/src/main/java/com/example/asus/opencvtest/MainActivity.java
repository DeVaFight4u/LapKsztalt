package com.example.asus.opencvtest;


import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;


import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity  {

    private static final String TAG = "OCVSample::Activity";

    public Button but;

    public MainActivity()
    {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        Typeface myTypeface = Typeface.createFromAsset(getAssets(), "8_BIT_WONDER.ttf");
        TextView myTextView = (TextView)findViewById(R.id.TITLE);
        myTextView.setTypeface(myTypeface);
        but = (Button)findViewById(R.id.START);
        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AppActivity.class);
                startActivity(intent);
            }
        });

    }


}

