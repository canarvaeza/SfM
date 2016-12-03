package com.example.cristiannarvaez.sfm;

/**
 * Created by Cristian Narvaez on 19/11/2016.
 */

/**Se usa

        Copyright 2015 Darshan Dorai

        Licensed under the Apache License, Version 2.0 (the "License");
        you may not use this file except in compliance with the License.
        You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.*/


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.darsh.multipleimageselect.activities.AlbumSelectActivity;
import com.darsh.multipleimageselect.helpers.Constants;
import com.darsh.multipleimageselect.models.Image;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.calib3d.Calib3d;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class CamaraCalibration extends AppCompatActivity {

    static {
        System.loadLibrary("opencv_java");
        System.loadLibrary("nonfree");
    }

    private int numberOfImagesToSelect = 15;

    ArrayList<Image> images;
    ImageView img;
    Boolean imgsSelected = false;

    //Sift siftActivity = new Sift();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibration);

        Button btn_image_button = (Button) findViewById(R.id.button);
        Button btn_sift_button = (Button) findViewById(R.id.button2);
        img = (ImageView) findViewById(R.id.imageView);

        btn_image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CamaraCalibration.this, AlbumSelectActivity.class);
                //set limit on number of images that can be selected, default is 10
                intent.putExtra(Constants.INTENT_EXTRA_LIMIT, numberOfImagesToSelect);
                startActivityForResult(intent, Constants.REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            //The array list has the image paths of the selected images
            images = data.getParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES);
            //Log.d("LAS IMAGENES 1: ", images.get(0).path.toString());
            Bitmap inputImage = BitmapFactory.decodeFile( images.get(0).path.toString());
            img.setImageBitmap(inputImage);
            imgsSelected = true;
        }
    }

    // Envia a sift
    public void toSift(View view){
        if (imgsSelected) {
            Intent toSift = new Intent(this, Sift.class);
            toSift.putParcelableArrayListExtra("images", images);
            startActivity(toSift);
        }
        else{
            AlertDialog alertDialog = new AlertDialog.Builder(CamaraCalibration.this).create();
            alertDialog.setTitle("Alerta");
            alertDialog.setMessage("Debes seleccionar más de una imagen");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Seguir",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
            Log.d("NO ELIGE IMÁGENES", "REPETIR");
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
    }
}
