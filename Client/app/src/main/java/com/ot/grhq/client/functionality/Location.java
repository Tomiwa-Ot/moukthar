package com.ot.grhq.client.functionality;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.LocationManager;

import org.json.JSONException;
import org.json.JSONObject;

public class Location {

    public static JSONObject getLastKnownLocation(Context context) throws JSONException {
        JSONObject json = new JSONObject();
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        @SuppressLint("MissingPermission") android.location.Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (lastLocation != null) {
            json.put("latitude", lastLocation.getLatitude());
            json.put("longitude", lastLocation.getLongitude());
            json.put("altitude", lastLocation.getAltitude());
        }

        return json;
    }
}
