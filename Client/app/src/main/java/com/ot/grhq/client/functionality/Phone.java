package com.ot.grhq.client.functionality;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class Phone {

    public static void call(Context context, String number) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + number));
        context.startService(callIntent);
    }

    public static void addContact(String name, String number) {

    }

    public static void deleteContact(String name, String number) {

    }

    public static Object readContacts() {
        return null;
    }
}
