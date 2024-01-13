package com.ot.grhq.client.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

public class Call extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals("android.intent.action.PHONE_STATE")) {
            // Get the phone state from the intent
            String phoneState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

            if (phoneState != null) {
                if (phoneState.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                    // Incoming call is ringing
                    String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

                } else if (phoneState.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                    // Call is in progress (either incoming or outgoing)

                } else if (phoneState.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                    // Call has ended

                }
            }
        }
    }
}
