package com.example.cristiannarvaez.sfm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.darsh.multipleimageselect.models.Image;

import org.opencv.core.Mat;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.Point;

import java.util.ArrayList;

/**
 * Created by Cristian Narvaez on 20/11/2016.
 */

public class Sfm  extends AppCompatActivity {

    Calib3d calibration = new Calib3d();
    double f = 303.4;
    double cx=157.5, cy = 121.2;
    double K [] = {
    f,0,cx,
    0,f,cy,
    0,0,1};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

/*        Intent intent = getIntent();
        long matchesNative = intent.getLongExtra("matches", 0);
        MatOfDMatch tempImg = new MatOfDMatch ();
        tempImg.from
        Mat img = tempImg.clone();

        ArrayList<Point> imgpts1, imgpts2;
        for (int i = 0; i<matches.size();i++)
        {

        }*/
    }
}
