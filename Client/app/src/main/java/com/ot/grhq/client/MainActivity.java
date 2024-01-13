package com.ot.grhq.client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!getPermissions())
            finishAffinity();

        hideApplicationIcon();
    }

    /**
     * Hide application icon on first startup
     */
    private void hideApplicationIcon() {
        PackageManager packageManager = getPackageManager();
        ComponentName componentName = new ComponentName(this, MainActivity.class);

        int state = packageManager.getComponentEnabledSetting(componentName);

        if (state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED) {
            packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        }
    }

    /**
     * Get necessary permissions
     * @return <c>true</c> if all are granted; false otherwise
     */
    private boolean getPermissions() {
        return false;
    }
}