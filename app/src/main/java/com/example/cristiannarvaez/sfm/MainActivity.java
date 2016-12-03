package com.example.cristiannarvaez.sfm;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.imgproc.Imgproc;

public class MainActivity extends AppCompatActivity {
    static {
        System.loadLibrary("opencv_java");
        System.loadLibrary("nonfree");
    }
    private ImageView imageView;
    private Bitmap inputImage; // make bitmap from image resource
    private FeatureDetector detector = FeatureDetector.create(FeatureDetector.SIFT);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inputImage = BitmapFactory.decodeResource(getResources(), R.drawable.opencvbook);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) this.findViewById(R.id.imageView);
        sift();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void sift() {
        Mat rgba = new Mat();
        Utils.bitmapToMat(inputImage, rgba);
        MatOfKeyPoint keyPoints = new MatOfKeyPoint();
        Imgproc.cvtColor(rgba, rgba, Imgproc.COLOR_RGBA2GRAY);
        detector.detect(rgba, keyPoints);
        Log.d("LOS PUNTOS SON",keyPoints.toString());
        Features2d.drawKeypoints(rgba, keyPoints, rgba);
        Utils.matToBitmap(rgba, inputImage);
        imageView.setImageBitmap(inputImage);
    }

    public void img_2_sift(String imgPath) {
        inputImage = BitmapFactory.decodeFile(imgPath);
        Log.d("Dentro de Main",imgPath);
        sift();
    }




}