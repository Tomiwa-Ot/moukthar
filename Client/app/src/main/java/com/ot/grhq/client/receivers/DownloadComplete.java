package com.ot.grhq.client.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import com.ot.grhq.client.functionality.PackageManager;

public class DownloadComplete extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String app = intent.getStringExtra("app_path");
        if (!app.isEmpty() && app != null)
            PackageManager.installApp(context, app);
    }
}
