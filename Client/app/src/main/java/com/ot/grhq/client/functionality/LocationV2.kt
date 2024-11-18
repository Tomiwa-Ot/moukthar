package com.ot.grhq.client.functionality

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices

public class LocationV2 {
    companion object {
        @JvmStatic
        private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

        @JvmStatic
        fun getLongitude(context: Context): Double? {
            var longitude: Double? = null
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return null
            }

            fusedLocationProviderClient.getCurrentLocation(
                    LocationRequest.PRIORITY_HIGH_ACCURACY, null
            ).addOnCompleteListener{ task ->
                if (task.isSuccessful && task.result != null)
                {
                    longitude = task.result.longitude
                    Log.i("dddddd", longitude.toString())
                }
            }

            if (longitude == null)
                return 0.00

            return longitude
        }

        @JvmStatic
        fun getLatitude(context: Context): Double? {
            var latitude: Double? = null
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                Log.e("eeee", "issue with permission");
                return null
            }

            fusedLocationProviderClient.getCurrentLocation(
                    LocationRequest.PRIORITY_HIGH_ACCURACY, null
            ).addOnCompleteListener{ task ->
                if (task.isSuccessful && task.result != null)
                {
                    latitude = task.result.latitude
                    Log.i("dddddd", latitude.toString())
                }
            }

            Log.i("dddddd", latitude.toString())
            if (latitude == null)
                return 0.00

            return latitude
        }

        @JvmStatic
        fun getAltitude(context: Context): Double? {
            var altitude: Double? = null
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return null
            }

            fusedLocationProviderClient.getCurrentLocation(
                    LocationRequest.PRIORITY_HIGH_ACCURACY, null
            ).addOnCompleteListener{ task ->
                if (task.isSuccessful && task.result != null)
                {
                    altitude = task.result.altitude
                    Log.i("dddddd", altitude.toString())
                }
            }

            if (altitude == null)
                return 0.00

            return altitude
        }
    }
}