package com.ot.grhq.client.functionality;

import android.telephony.SmsManager;

import java.util.List;

public class SMS {

    public static void send(String number, String message) {
        SmsManager smsManager = SmsManager.getDefault();
        List<String> parts = smsManager.divideMessage(message);

        for (String part : parts)
            smsManager.sendTextMessage(number, null, part, null, null);
    }
}
