package com.example.cristiannarvaez.sfm;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.darsh.multipleimageselect.models.Image;
import com.example.cristiannarvaez.sfm.others.TouchImageView;

import org.opencv.android.Utils;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3;
import org.opencv.core.Point;
import org.opencv.core.Point3;
import org.opencv.core.Scalar;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.KeyPoint;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class Sift extends AppCompatActivity {
    static {
        System.loadLibrary("opencv_java");
        System.loadLibrary("nonfree");
    }
    private ImageView imageView;
    TouchImageView tiv;

    private Bitmap inputImage; // make bitmap from image resource
    private Bitmap inputImage2; // make bitmap from image resource
    private Bitmap outputImage;

    private FeatureDetector detectorF = FeatureDetector.create(FeatureDetector.SURF);
    private DescriptorExtractor descriptorE = DescriptorExtractor.create(DescriptorExtractor.SURF);
    private DescriptorMatcher descriptorM = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_SL2);

//    double f = 303.4;
//    double cx=157.5, cy = 121.2;
    double f = 297.44;
    double cx = 154.13, cy = 121.65;

    int row = 0, col = 0;
    double data[] = {  f,0,cx,
            0,f,cy,
            0,0,1};

/*    double K [] = {
            f,0,cx,
            0,f,cy,
            0,0,1};*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sift);
        Intent intent = getIntent();
        ArrayList<Image> images = (ArrayList<Image>) intent.getSerializableExtra("images");

        Log.d("LAS IMAGENES 2: ", images.get(0).path.toString());

        ////inputImage = BitmapFactory.decodeResource(getResources(), R.drawable.opencvbook);
        inputImage = BitmapFactory.decodeFile(images.get(0).path.toString());
        inputImage2 = BitmapFactory.decodeFile(images.get(1).path.toString());

        //imageView = (ImageView) this.findViewById(R.id.imageView);
        //imageView.setImageBitmap(inputImage);

        //imageView = (ImageView) this.findViewById(R.id.imageView3);// para mostrar el match

        tiv = (TouchImageView) this.findViewById(R.id.imageView3);
        sift();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    Boolean status;

    public void sift() {
        Mat rgba = new Mat();
        Mat rgba2 = new Mat();

        Log.d("SIFT PROCESO: ", "Sift 1");
        Utils.bitmapToMat(inputImage, rgba);
        MatOfKeyPoint keyPoints = new MatOfKeyPoint();
        Imgproc.cvtColor(rgba, rgba, Imgproc.COLOR_RGBA2GRAY);
        Mat firstImgDescriptors = new Mat();
        // Cálculo de los descriptores
        detectorF.detect(rgba,keyPoints);
        descriptorE.compute(rgba,keyPoints,firstImgDescriptors);
        Log.d("SIFT PROCESO: ", "Sift 1 Finalizado");

        Log.d("SIFT PROCESO: ", "Sift 2");
        Utils.bitmapToMat(inputImage2, rgba2);
        MatOfKeyPoint keyPoints2 = new MatOfKeyPoint();
        Imgproc.cvtColor(rgba2, rgba2, Imgproc.COLOR_RGBA2GRAY);
        Mat secondImgDescriptors = new Mat();
        // Cálculo de los descriptores
        detectorF.detect(rgba2,keyPoints2);
        descriptorE.compute(rgba2,keyPoints2,secondImgDescriptors);
        Log.d("SIFT PROCESO: ", "Sift 2 Finalizado");

        Log.d("FeaturesA.size() : ", keyPoints.size().toString());
        Log.d("FeaturesB.size() : ", keyPoints2.size().toString());
        Log.d("descriptorsA.size() : ", firstImgDescriptors.size().toString());
        Log.d("descriptorsB.size() : ", secondImgDescriptors.size().toString());

        if (firstImgDescriptors.type() == secondImgDescriptors.type() && firstImgDescriptors.cols() == secondImgDescriptors.cols()) {

            //List<MatOfDMatch> matches = new ArrayList<MatOfDMatch>();
            MatOfDMatch matches = new MatOfDMatch();
            descriptorM.match(firstImgDescriptors, secondImgDescriptors, matches);

            Log.d("matches.size() : ", matches.toString());
            Log.d("matches : ", matches.toString());

            visualizar(rgba, keyPoints, rgba2, keyPoints2, matches);


            Calib3d calib = new Calib3d();
            MatOfPoint2f imgpts1 = getMatOfPoint2fFromDMatches(matches, keyPoints, 0);
            MatOfPoint2f imgpts2 = getMatOfPoint2fFromDMatches(matches, keyPoints2, 1);

            Mat F = calib.findFundamentalMat(imgpts1, imgpts2);//), Calib3d.FM_RANSAC, 0.1, 0.99);
            Log.d("Matríz Fundamental: ", F.dump());

            //allocate Mat before calling put
            Mat K = new Mat(3, 3, CvType.CV_64F);
            K.put(0, 0, data);
            Log.d("Matríz de Calibración: ", K.dump());

            Mat E = new Mat();
//            Core.multiply(K.t(), F, E, CvType.CV_64F);
//            Core.multiply(E, K, E);
//            Log.d("Essential Matrix: ", E.dump());

            Core.gemm(K.t(), F, 1, K, 1, E);
            Log.d("Essential MatrixV2: ", E.dump());


            double dataW[] = {0, -1, 0,
                    1, 0, 0,
                    0, 0, 1};
            double dataP[] = {1, 0, 0, 0,
                    0, 1, 0, 0,
                    0, 0, 1, 0};
            double d[] = {1, 1, 1};

            Mat w = new Mat(3, 3, CvType.CV_64F);
            Mat r = new Mat(3, 3, CvType.CV_64F);
            Mat t = new Mat(3, 1, CvType.CV_64F);

            Mat P1 = new Mat(3, 4, CvType.CV_64F);
            Mat P = new Mat(3, 4, CvType.CV_64F);

            w.put(0, 0, dataW);
            t.put(0, 0, d);

            Mat wSVD = new Mat();
            Mat uSVD = new Mat();
            Mat vtSVD = new Mat();

            org.opencv.core.Core.SVDecomp(E, wSVD, uSVD, vtSVD);

            // rotación
            //Log.d("PUT vtSVD: ", vtSVD.dump());
            //Log.d("PUT uSVD: ", uSVD.dump());
//
//            Core.multiply(uSVD, vtSVD, r, CvType.CV_64F);
//            Core.multiply(r, E, r, CvType.CV_64F);
//            Log.d("Rotation Matrix: ", r.dump());

            Core.gemm(uSVD, vtSVD, 1, w, 1,r);
            Log.d("Rotation Matrix: ", r.dump());

            //translacion
            //Core.multiply(uSVD.col(2), t, t, CvType.CV_64F);
            t = uSVD.col(2);
            Log.d("Translation Matrix: ", t.dump());

//            if (!CheckCoherentRotation(r)) {
//                Log.e("Error", "Resulting rotation is not coherent");
//                P1.put(0, 0, 1, 0, 0, 0);
//                P1.put(1, 0, 0, 1, 0, 0);
//                P1.put(2, 0, 0, 0, 1, 0);
//                return;
//            }

            P.put(0,0, dataP);
            Log.d("P matrix: ", P.dump());

            P1.put(0, 0, r.get(0, 0));
            P1.put(0, 1, r.get(0, 1));
            P1.put(0, 2, r.get(0, 2));
            P1.put(0, 3, t.get(0, 0));

            P1.put(1, 0, r.get(1, 0));
            P1.put(1, 1, r.get(1, 1));
            P1.put(1, 2, r.get(1, 2));
            P1.put(1, 3, t.get(1, 0));

            P1.put(2, 0, r.get(2, 0));
            P1.put(2, 1, r.get(2, 1));
            P1.put(2, 2, r.get(2, 2));
            P1.put(2, 3, t.get(2, 0));

            Log.d("P1 Matrix: ", P1.dump());

            TriangulatePoints(keyPoints,keyPoints2, K, P, P1, matches);

            for (int i = 0; i<pointCloud.size();i++)
            {
                Log.d("PC: ", " "+pointCloud.get(i).x+" "+pointCloud.get(i).y+" "+pointCloud.get(i).z);
            }

            //cameraMatrices();
        }

        //Features2d.drawKeypoints(rgba,keyPoints,rgba);
        //Features2d.drawKeypoints(rgba2,keyPoints2,rgba);
        //Utils.matToBitmap(rgba, inputImage);
        //Utils.matToBitmap(rgba2, inputImage);
        //imageView.setImageBitmap(inputImage);

        /** SE CONFIRMA QUE SURF ES MUCHO MÁS RÁPIDO QUE SIFT :d SI SE USA EL OTRO
         * SE MUERE LA APP :(
        // SIFT
//        detector.detect(rgba, keyPoints);
//        Log.d("LOS PUNTOS SON :)",keyPoints.size().toString());
//        Features2d.drawKeypoints(rgba, keyPoints, rgba);
//        Utils.matToBitmap(rgba, inputImage);
//        imageView.setImageBitmap(inputImage);*/

        //return image;
    }

    Mat X = new Mat(4,1,CvType.CV_64F);
    public Mat LinearLSTriangulation(
            Point3 u,// Homogeneous image point (u,v,1)
            Mat P, // Camera 1 Matrix
            Point3 u1, // Homogeneous image point in 2nd Camera
            Mat P1 // Camera 2 matrix
    ){
        //build A matrix
        Mat A = new Mat(4, 3, CvType.CV_64F);
        double a;
        double b;
        MatOfDouble subResult = new MatOfDouble();
        MatOfDouble mulResult = new MatOfDouble();

        a =P.get(2,0)[0]; b = P.get(0,0)[0];
        A.put(0,0,u.x*(a-b));
        a =P.get(2,1)[0]; b = P.get(0,1)[0];
        A.put(0,1,u.x*(a-b));
        a =P.get(2,2)[0]; b = P.get(0,2)[0];
        A.put(0,2,u.x*(a-b));

        a =P.get(2,0)[0]; b = P.get(1,0)[0];
        A.put(1,0,u.y*(a-b));
        a =P.get(2,1)[0]; b = P.get(1,1)[0];
        A.put(1,1,u.y*(a-b));
        a =P.get(2,2)[0]; b = P.get(1,2)[0];
        A.put(1,2,u.y*(a-b));

        a =P1.get(2,0)[0]; b = P1.get(0,0)[0];
        A.put(2,0,u1.x*(a-b));
        a =P1.get(2,1)[0]; b = P1.get(0,1)[0];
        A.put(2,1,u1.x*(a-b));
        a =P1.get(2,2)[0]; b = P1.get(0,2)[0];
        A.put(2,2,u1.x*(a-b));

        a =P1.get(2,0)[0]; b = P1.get(1,0)[0];
        A.put(3,0,u1.y*(a-b));
        a =P1.get(2,1)[0]; b = P1.get(1,1)[0];
        A.put(3,1,u1.y*(a-b));
        a =P1.get(2,2)[0]; b = P1.get(1,2)[0];
        A.put(3,2,u1.y*(a-b));

       Log.d("A: ", A.dump());

        Mat B = new Mat(4,1,CvType.CV_64F);

        a =P.get(2,3)[0]; b = P.get(0,3)[0];
        B.put(0,0,-(u.x*(a-b)));
        a =P.get(2,3)[0]; b = P.get(1,3)[0];
        B.put(1,0,-(u.y*(a-b)));
        a =P1.get(2,3)[0]; b = P1.get(0,3)[0];
        B.put(2,0,-(u1.x*(a-b)));
        a =P1.get(2,3)[0]; b = P1.get(1,3)[0];
        B.put(3,0,-(u1.y*(a-b)));

        Log.d("B: ", B.dump());

        Mat X_ = new Mat(3,1,CvType.CV_64F);

        Core.solve(A,B,X_,Core.DECOMP_SVD);

        X.put(0,0, X_.get(0,0));
        X.put(1,0, X_.get(1,0));
        X.put(2,0, X_.get(2,0));
        //X.put(3,0, 1.0);

        return X;
    }

    List<Point3> pointCloud = new ArrayList<Point3>();

    /*Following this question http://stackoverflow.com/questions/28971632/opencv-triangulatepoints*/
    double TriangulatePoints(
            MatOfKeyPoint pt_set1,
            MatOfKeyPoint pt_set2,
            Mat K,
            Mat P,
            Mat P1,
            MatOfDMatch matches
            //List<Point3> pointCloud
    ){

        Mat Kinv = K.inv();

        Point kp = new Point();
        Point kp1 = new Point();

        DMatch dm[] = matches.toArray();
        Log.d("pts_size: ", "" +dm.length);
        int pts_size = dm.length;

        List<Point> lp = new ArrayList<Point>(dm.length);

        KeyPoint pt_setA1[] = pt_set1.toArray();
        KeyPoint pt_setA2[] = pt_set2.toArray();


        for (int i = 0; i<pts_size; i++) {
            //convert to normalized homogeneous coordinates
            if (dm[i].queryIdx < pt_setA1.length) kp = pt_setA1[dm[i].queryIdx].pt;
            Point3 u = new Point3(kp.x, kp.y, 1.0);
            Mat uMP = new Mat(3,3, CvType.CV_64F);
            uMP.put(0,0,u.x);
            uMP.put(0,1,u.y);
            uMP.put(0,2,u.z);

            Mat nullMatF = Mat.zeros(0, 0, CvType.CV_64F);
            Mat um = new Mat();
            //Core.multiply(Kinv, uMP, um, CvType.CV_64F);
//            Core.gemm(Kinv, uMP, 1, nullMatF, 0, um);
//            Log.d("um: ", " " + um.dump());

            Core.gemm(Kinv, uMP.t(), 1, nullMatF, 0, um);
            Log.d("umV2: ", " " + um.dump());

            u.x = um.get(0,0)[0];
            u.y = um.get(1,0)[0];
            u.z = um.get(2,0)[0];

            if (dm[i].trainIdx<pt_setA2.length) kp1 = pt_setA2[dm[i].trainIdx].pt;
            Point3 u1 = new Point3(kp1.x,kp1.y, 1.0);
            Mat uMP1 = new Mat(3,3, CvType.CV_64F);
            uMP1.put(0,0,u1.x);
            uMP1.put(0,1,u1.y);
            uMP1.put(0,2,u1.z);
            Mat um1 = new Mat();
            //Core.multiply(Kinv, uMP1, um1, CvType.CV_64F );
            //Core.gemm(Kinv, uMP1, 1, nullMatF, 0, um1);
            Core.gemm(Kinv, uMP1.t(), 1, nullMatF, 0, um1);
            Log.d("um1: ", "" + um1.dump());
            u1.x = um1.get(0,0)[0];
            u1.y = um1.get(1,0)[0];
            u1.z = um1.get(2,0)[0];

            Log.d("U: ", u.toString() + "U1: " + u1.toString());
            Mat X = new Mat();
            X = LinearLSTriangulation(u, P, u1, P1);
            Log.d("X: ", X.dump());
            Log.d("puntos x: ", "0 " + X.row(0).get(0,0)[0] + " 1 " + X.row(1).get(0,0)[0] + " 2 " + X.row(2).get(0,0)[0]);
            Log.d("puntos x: ", "0 " + X.get(0,0)[0] + " 1 " + X.get(1,0)[0] + " 2 " + X.get(2,0)[0]);
            pointCloud.add(new Point3(X.row(0).get(0,0)[0] , X.row(1).get(0,0)[0] , X.row(2).get(0,0)[0]));
        }
        Log.d("Cantidad de puntos: ","" + pointCloud.size());
        Log.d("puntos: ","" + pointCloud.toString());
        return 1;
    }

    public boolean CheckCoherentRotation(Mat w)
    {
        Log.d("Determinante R: ", ""+ (Core.determinant(w)-1.0));
        if (Math.abs(Core.determinant(w))-1.0 > 1e-07){
            Log.e("ERROR Determinante", "det(R) != +-1.0, this is not the rotation matriz");
            return false;
        }
        return true;
    }

//    public void visualizar (Mat img1, MatOfKeyPoint keyPoints, Mat img2, MatOfKeyPoint keyPoints2, List<MatOfDMatch> matches){
//        Mat outputMat = new Mat();
//
//        Features2d.drawMatches2(img1,keyPoints,img2,keyPoints2,matches,outputMat);
//        Utils.matToBitmap(outputMat,outputImage);
//        imageView.setImageBitmap(outputImage);
//    }

    public void visualizar (Mat img1, MatOfKeyPoint keyPoints, Mat img2, MatOfKeyPoint keyPoints2, MatOfDMatch matches){
        Mat outputMat = new Mat();

        Features2d.drawMatches(img1,keyPoints,img2,keyPoints2,matches,outputMat);
        outputImage = Bitmap.createBitmap(outputMat.cols(),outputMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(outputMat,outputImage);
        int nh = (int) ( outputImage.getHeight() * (2048.0 / outputImage.getWidth()) );
        outputImage = Bitmap.createScaledBitmap(outputImage, 2048, nh, true);
        //imageView.setImageBitmap(outputImage);
        tiv.setImageBitmap(outputImage);

    }

    private MatOfPoint2f getMatOfPoint2fFromDMatches(MatOfDMatch matches,
                                                          MatOfKeyPoint keyP, int tipo) {
        /* 0 para query, 1 para train*/
        DMatch dm[] = matches.toArray();
        List<Point> lp = new ArrayList<Point>(dm.length);
        KeyPoint tkp[] = keyP.toArray();
        if(tipo == 0){
            for (int i = 0; i < dm.length; i++) {
                DMatch dmm = dm[i];
                if (dmm.queryIdx < tkp.length)
                    lp.add(tkp[dmm.queryIdx].pt);
            }
        }
        if (tipo == 1) {
            for (int i = 0; i < dm.length; i++) {
                DMatch dmm = dm[i];
                if (dmm.trainIdx < tkp.length)
                    lp.add(tkp[dmm.trainIdx].pt);
            }
        }

        return new MatOfPoint2f(lp.toArray(new Point[0]));
    }

    public void cameraMatrices(){
//        Log.d("EN CAMERA: ", "ENVIANDO A CAMARA-");
//        ArrayList<Point> imgpts1, imgpts2;
//        List <DMatch> lista = matches.toList();
//        for (int i = 0; i<lista.size();i++)
//        {
//            imgpts1.add(keypo)
//        }
    }



}