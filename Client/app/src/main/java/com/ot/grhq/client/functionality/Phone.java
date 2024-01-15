package com.ot.grhq.client.functionality;

import android.accounts.AccountManager;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;

import java.util.ArrayList;

public class Phone {

    public static void call(Context context, String number) {
        Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
        callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(callIntent);
    }

    /**
     * Create contact
     * @param context application context
     * @param name contact name
     * @param number contact number
     */
    public static void addContact(Context context, String name, String number) {
        try {
            // Create a new contact operation
            ArrayList<ContentProviderOperation> operations = new ArrayList<>();
            int rawContactInsertIndex = operations.size();

            operations.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, AccountManager.KEY_ACCOUNT_TYPE)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, AccountManager.KEY_ACCOUNT_NAME)
                    .build());

            // Add display name
            operations.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                    .withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.IN_VISIBLE_GROUP, true)
                    .build());

            operations.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number)
                    .build());

            // Apply operations
            context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, operations);
        } catch (Exception e) {}
    }

    public static void deleteContact(String name, String number) {

    }

    public static Object readContacts() {
        return null;
    }
}
