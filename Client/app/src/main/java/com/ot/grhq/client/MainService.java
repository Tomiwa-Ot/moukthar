package com.ot.grhq.client;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.net.URI;
import java.net.URISyntaxException;

public class MainService extends Service {

    private final String SERVICE_RESTART_INTENT = "com.ot.grhq.receiver.restartservice";
    private final String SERVER_URI = "ws://";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            WebSocketClient client = new WebSocketClient(new URI(SERVER_URI));
            client.connect();

        } catch (URISyntaxException e) {}

        Log.d("eeee", "First");
        final Handler handler = new Handler();
        final int delay = 1000;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("eeee", "The service is working");
                handler.postDelayed(this, delay);
            }
        }, delay);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Intent restartService = new Intent(SERVICE_RESTART_INTENT);
        sendBroadcast(restartService);
    }
}
