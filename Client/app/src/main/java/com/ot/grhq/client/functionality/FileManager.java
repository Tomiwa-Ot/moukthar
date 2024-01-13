package com.ot.grhq.client.functionality;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FileManager {

    public static boolean downloadFile(Context context, String url) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, null);
        // Hide download notification
        request.setVisibleInDownloadsUi(false);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        long id = downloadManager.enqueue(request);

        // polling for download complete?
        return false;
    }

    public static void uploadFile(String path, String uploadUrl) {
        new UploadTask(path, uploadUrl).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public static List<String> listFiles(String path) {
        List<String> files = new ArrayList<String>();

        return files;
    }

    private static class UploadTask extends AsyncTask<Void, Void, Boolean> {

        private String filePath;
        private String uploadUrl;

        public UploadTask(String filePath, String uploadUrl) {
            this.filePath = filePath;
            this.uploadUrl = uploadUrl;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            HttpURLConnection connection = null;
            DataOutputStream dataOutputStream = null;
            FileInputStream fileInputStream = null;

            try {
                // Open a connection to the server
                URL url = new URL(uploadUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "multipart/form-data");

                // Create a data output stream to write to the server
                dataOutputStream = new DataOutputStream(connection.getOutputStream());

                // Read the file and write it to the server
                File file = new File(filePath);
                fileInputStream = new FileInputStream(file);
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    dataOutputStream.write(buffer, 0, bytesRead);
                }

                // Get the server response code
                int responseCode = connection.getResponseCode();

                // Check if the upload was successful
                return responseCode == HttpURLConnection.HTTP_OK;
            } catch (IOException e) {
                return false;
            } finally {
                // Close the streams and connections
                try {
                    if (dataOutputStream != null) {
                        dataOutputStream.close();
                    }
                    if (fileInputStream != null) {
                        fileInputStream.close();
                    }
                    if (connection != null) {
                        connection.disconnect();
                    }
                } catch (IOException e) {
                }
            }
        }
    }
}
