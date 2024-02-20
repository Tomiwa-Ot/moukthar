package com.ot.grhq.client.functionality;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.LocationManager;
import android.util.Base64;

public class Location {

    public static double getLatitude(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        @SuppressLint("MissingPermission") android.location.Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        return lastLocation.getLatitude();
    }

    public static double getLongitude(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        @SuppressLint("MissingPermission") android.location.Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        return lastLocation.getLongitude();
    }

    public static double getAltitude(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        @SuppressLint("MissingPermission") android.location.Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        return lastLocation.getAltitude();
    }
}
