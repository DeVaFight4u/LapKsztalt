package com.example.asus.opencvtest;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import java.util.ArrayList;
import java.util.List;


import static com.example.asus.opencvtest.ShapeState.*;

public class AppActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {
    private static final String TAG = "OCVSample::Activity";
    private CameraBridgeViewBase mOpenCvCameraView;
    TimeCounting timeCounting;
    TimeCounting catchingShapeTime;
    Mat imgDestination;
    Mat imgOriginal;
    Mat intermediate;
    List<MatOfPoint> List_points;
    Scalar colors[];
    boolean readToDraw[];
    boolean detectEnd;
    boolean chosenShape;
    int bgr_hsv, bgr_gray;
    MediaPlayer sound;
    long time1;
    Figure shape;
    boolean isFound;
    boolean catchShape;
    long detectedTime;
    android.graphics.Point screenSize;
    Display display;
    int widthdp;
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch(status){
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();

                } break;

                default:
                {
                    super.onManagerConnected(status);

                } break;
            }

        }
    };
    public AppActivity()
    {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.show_camera);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        loadBools();
       // time1 = System.currentTimeMillis();
    }
    @Override
   /* public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
    }*/
    //@Override
    public void onPause()
    {
        super.onPause();
        if(mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }
    @Override
    public void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);
    }
    public void onDestroy()
    {
        super.onDestroy();
        if(mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }
    public void onCameraViewStarted(int width, int height)
    {

        imgDestination = new Mat();
        intermediate = new Mat();
        colors = new Scalar[]{new Scalar(110, 150, 125), new Scalar(130,255,255), new Scalar(80, 120, 140),
                new Scalar (100, 255, 255), new Scalar(0, 130, 130), new Scalar(20, 255, 255), new Scalar(80, 100, 100), new Scalar(100,255,255)};
        readToDraw = new boolean[]{false, false, false, false, false};
        List_points = new ArrayList<MatOfPoint>();
        bgr_gray = Imgproc.COLOR_BGR2GRAY;
        bgr_hsv = Imgproc.COLOR_BGR2HSV;
        catchingShapeTime = new TimeCounting();
        screenSize = new android.graphics.Point();
        display = this.getWindowManager().getDefaultDisplay();
        width = display.getWidth();
        widthdp = (int)(width/getResources().getDisplayMetrics().density);
        timeCounting = new TimeCounting();
        chosenShape = false;
        catchShape = false;
    }

    public void onCameraViewStopped()
    {
       imgOriginal.release();
        imgDestination.release();
        sound.release();
    }
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame)
    {
        imgOriginal = inputFrame.rgba();
        List<MatOfPoint> List_contours = new ArrayList<MatOfPoint>();
        if(!detectEnd) {
            if(!chosenShape) {
                chooseShape();
                time1 = System.currentTimeMillis();
                chosenShape = true;
            }
            if (!isFound) {
                convertMat(imgOriginal, imgDestination, shape.color1, shape.color2);
                if(LoadingActivity.getShapeState()==KOLO)
                {
                    findCircles(imgOriginal, imgDestination);
                }
                else
                    findAndDrawPoints(List_contours, imgDestination, imgOriginal);
            }
            timeCounting.counting(time1, 30);
            if(shape.detected)
            {
                if(!catchShape) {
                    detectedTime = System.currentTimeMillis();
                    catchShape = true;
                }

            }
            if(catchShape)
            {
                detectedTime();
            }
            Imgproc.putText(imgOriginal, "POZOSTALO: " + (timeCounting.getWaitTime()), new Point(0, 40), Core.FONT_HERSHEY_SIMPLEX, 2, new Scalar(255, 255, 255));
            isEndTime(timeCounting);
        }
        Restart();

        return imgOriginal;
    }

    void convertMat(Mat img, Mat dst, Scalar color1, Scalar color2) // konwertuje macierze kolorow
    {
        Mat medianBlur = new Mat();
        Imgproc.medianBlur(img, medianBlur, 3);
        Imgproc.cvtColor(medianBlur, dst, Imgproc.COLOR_BGR2HSV);
        Core.inRange(dst, color1, color2, dst);
        Imgproc.GaussianBlur(dst, dst, new Size(9,9), 2, 2);

        if(LoadingActivity.getShapeState() != KOLO)
            Imgproc.Canny(dst, dst, 200, 100);


    }
    void findAndDrawPoints(List<MatOfPoint> contours, Mat dst, Mat img) // znajduje punkty figury
    {
        Mat hierarchy = new Mat();
        Imgproc.findContours(dst, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0,0));
        hierarchy.release();
        double maxArena = 0;
        for(int i =0; i<contours.size(); i++)
        {
            double sizeContour = Imgproc.contourArea(contours.get(i));
            Log.i("rozmiar areny", "Rozmiar areny = " + sizeContour);
            MatOfPoint2f approxCurve = new MatOfPoint2f();
            MatOfPoint2f tempContour = new MatOfPoint2f(contours.get(i).toArray());
            if(sizeContour>maxArena&& sizeContour>Math.pow(widthdp/3.0,2.0)) {
                maxArena=sizeContour;
                double approxDist = Imgproc.arcLength(tempContour, true) * 0.02;
                Imgproc.approxPolyDP(tempContour, approxCurve, approxDist, true);
                List<MatOfPoint> pt2 = new ArrayList<MatOfPoint>();
                MatOfPoint points = new MatOfPoint(approxCurve.toArray());
                pt2.add(points);
                try {
                    if(points.height()==0)
                        shape.detected=false;
                    drawShapes(img, pt2, points.height(), points);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
        //return detected;
    }
    void findCircles(Mat img, Mat dst)
    {
        Mat circ = new Mat(img.width(), img.height(), CvType.CV_8UC1);
        Imgproc.HoughCircles(dst,circ,Imgproc.HOUGH_GRADIENT,1,dst.rows()/8,150,50,0,0);

        int numberOfCircles = (circ.rows() == 0)? 0 : circ.cols();
        drawCircle(numberOfCircles, img, circ, new Scalar(255,255,0));
    }
    void drawCircle(int n, Mat img, Mat dst, Scalar color)
    {
        boolean circleFound = false;
        for(int i=0; i<n; i++)
        {
            double[] circleCoordinates = dst.get(0,i);
            int x = (int)circleCoordinates[0];
            int y = (int)circleCoordinates[1];
            Point center = new Point(x,y);
            int radius = (int)circleCoordinates[2];
            if(Math.PI*Math.pow(radius,2)>Math.pow(widthdp/3.0,2.0)) {
                Imgproc.circle(img, center, radius, color, 4);
                shape.detected = true;
            }
        }

    }

    void drawShapes(Mat img, List<MatOfPoint> point, int verticles, MatOfPoint pts) throws InterruptedException // ile wierzcholkow taka figure rysuje
    {
             switch (verticles)
             {
                 case 3:
                 {
                     if(verticles==shape.verticles) {
                            normalDraw(img, point, new Scalar(255, 255, 0), pts);

                        }

                        break;
                 }
                 case 4: {
                     if(verticles==shape.verticles)
                     {
                         normalDraw(img, point, new Scalar(255, 255, 0), pts);

                     }
                     break;
                 }
                 default:
                     shape.detected = false;
                     break;
                 }
    }
       // }

    void normalDraw(Mat img, List<MatOfPoint> pt, Scalar color, MatOfPoint pts) // rysowanie figur
    {
        shape.detected = true;
        if(LoadingActivity.getShapeState() == KWADRAT) {
            Rect rect = Imgproc.boundingRect(pts);
            Imgproc.rectangle(img, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), color, 4);
        }
        Imgproc.polylines(img, pt, true, color, 4);
        pt.clear();


    }

    void chooseShape() // wybieranie co ma byc losowane
    {
        ShapeState figure = null;
        figure = LoadingActivity.getShapeState();
        Log.i("czy idziesz tu", "ok");
        switch (figure)
        {
            case TROJKAT: {

                sound = MediaPlayer.create(this, R.raw.trojkat);
                shape= new Figure(sound, 3, colors[4], colors[5]);
                Log.i("czy idziesz tu", "ok1");
                break;
            }
            case KWADRAT: {

                sound = MediaPlayer.create(this, R.raw.kwadrat);
                shape = new Figure(sound, 4, colors[0], colors[1]);
                Log.i("czy idziesz tu", "ok2");

                break;
            }
            case KOLO: {

                sound = MediaPlayer.create(this, R.raw.kolo);
                shape = new Figure(sound, colors[2], colors[3]);
                Log.i("czy idziesz tu", "ok3");
                break;
            }
        }

    }
    void detectedTime() {

        catchingShapeTime.counting(detectedTime, 5);
        if(!shape.detected&&detectedTime-System.currentTimeMillis()>3000)
        {
            catchShape = false;
        }
        else {
            isEndTime(catchingShapeTime);
        }
    }
    void isEndTime(TimeCounting timeCounting)
    {
        if(timeCounting.getWaitTime()<=0) {
            if (!detectEnd) {
                isFound = true;
                detectEnd = true;
            }
        }
        else if(isFound) {
            detectEnd = true;
            shape.SoundPlay();
        }
    }
    void loadBools()
    {
        detectEnd = false;
        isFound =false;
        chosenShape = false;
    }
    void Restart()
    {
        if(!shape.sound.isPlaying()&&(detectEnd ||isFound))
        {
            Intent intent = new Intent(AppActivity.this, MainActivity.class);
            finish();
            startActivity(intent);
        }
    }
}
