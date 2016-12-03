package com.example.cristiannarvaez.sfm;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

import com.example.cristiannarvaez.sfm.MyGLLibs.MyGLRenderer;
import com.example.cristiannarvaez.sfm.MyGLLibs.MyGLSurfaceView;

/**
 * Created by Cristian Narvaez on 26/11/2016.
 */
public class MyGLActivity extends Activity{

    private GLSurfaceView glView;   // Use GLSurfaceView

    // Call back when the activity is started, to initialize the view
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        glView = new MyGLSurfaceView(this);           // Allocate a GLSurfaceView
        //glView.setRenderer(new MyGLRenderer(this)); // Use a custom renderer
        //this.setContentView(glView);                // This activity sets to GLSurfaceView
        setContentView(glView);
    }

    // Call back when the activity is going into the background
    @Override
    protected void onPause() {
        super.onPause();
        glView.onPause();
    }

    // Call back after onPause()
    @Override
    protected void onResume() {
        super.onResume();
        glView.onResume();
    }
}
