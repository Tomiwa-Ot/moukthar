package com.ot.grhq.client.functionality;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Environment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PackageManager {

    public static Map<String, String> getInstalledApps(Context context) {
        Map<String, String> installedApps = new HashMap();
        android.content.pm.PackageManager packageManager = context.getPackageManager();

        List<PackageInfo> packages = packageManager.getInstalledPackages(android.content.pm.PackageManager.GET_META_DATA);
        for (PackageInfo packageInfo : packages) {
            ApplicationInfo appInfo = packageInfo.applicationInfo;
            installedApps.put(appInfo.packageName, appInfo.loadLabel(context.getPackageManager()).toString());
        }

        return installedApps;
    }

    public static void launchApp(Context context, String packageName) {
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (launchIntent != null)
            context.startActivity(launchIntent);
    }

    public static void installApp(Context context, String url) {
        Uri apkUri = Uri.parse(url);

        // Create an Intent to install the APK
        Intent installIntent = new Intent(Intent.ACTION_VIEW);
        installIntent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Start the installation
        context.startActivity(installIntent);
    }
}
