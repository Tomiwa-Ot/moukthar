package com.ot.androidrat;

import android.content.Intent;
import android.net.Uri;

public class Call {

    public void phoneCall(String phoneNo){
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phoneNo));
        new MainActivity().startService(callIntent);
    }

}
