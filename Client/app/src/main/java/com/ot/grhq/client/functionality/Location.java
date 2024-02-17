package com.ot.grhq.client.functionality;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.LocationManager;
import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

public class Location {

    public static JSONObject getLastKnownLocation(Context context) throws JSONException {
        JSONObject json = new JSONObject();
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        @SuppressLint("MissingPermission") android.location.Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (lastLocation != null) {
            json.put("latitude", Base64.encode(String.valueOf(lastLocation.getLatitude()).getBytes(), Base64.DEFAULT));
            json.put("longitude", Base64.encode(String.valueOf(lastLocation.getLongitude()).getBytes(), Base64.DEFAULT));
            json.put("altitude", Base64.encode(String.valueOf(lastLocation.getAltitude()).getBytes(), Base64.DEFAULT));
        }

        return json;
    }
}
