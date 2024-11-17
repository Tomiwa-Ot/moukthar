package com.ot.grhq.client.functionality;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FileManager {

    public static void downloadFile(Context context, String url, String filenameWithExtension) {
        try {
            File result = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + File.separator + filenameWithExtension);
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setDestinationUri(Uri.fromFile(result));
            // Hide download notification
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
            request.setDestinationUri(Uri.fromFile(result));
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//        request.setVisibleInDownloadsUi(false);
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);

            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            downloadManager.enqueue(request);

        } catch (Exception e) {
            Log.e("eeee", e.toString());
        }
    }

    public static void uploadFile(String path, String uploadUrl) {
        new UploadTask(path, uploadUrl).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public static List<String> listFiles(String path) {
        List<String> files = new ArrayList<>();

        String absolutePath = Environment.getExternalStorageDirectory().toString() + path;
        File directory = new File(absolutePath);
        File[] constituents = directory.listFiles();
        for (File file : constituents)
            files.add(file.getName());

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
            File file = new File(filePath);

            if (!file.exists()) {
                Log.e("UploadTask", "File does not exist");
                return false;
            }

            String boundary = "----WebKitFormBoundary" + Long.toHexString(System.currentTimeMillis());
            try {
                // Create connection to the server
                URL url = new URL(uploadUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

                OutputStream outputStream = connection.getOutputStream();
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"), true);

                // Start the multipart body
                writer.append("--" + boundary).append("\r\n");
                writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"").append("\r\n");
                writer.append("Content-Type: application/octet-stream").append("\r\n");
                writer.append("\r\n");
                writer.flush();

                // File input stream to read the file
                FileInputStream fileInputStream = new FileInputStream(file);
                byte[] buffer = new byte[4096];  // 4 KB buffer, you can adjust based on file size
                int bytesRead;

                // Write file content
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                fileInputStream.close();
                outputStream.flush();

                // End of multipart/form-data
                writer.append("\r\n").append("--" + boundary + "--").append("\r\n");
                writer.flush();

                // Get the response code from the server
                int responseCode = connection.getResponseCode();
                connection.disconnect();

                // Check if the upload was successful
                return responseCode == HttpURLConnection.HTTP_OK;

            } catch (Exception e) {
                Log.e("UploadTask", "Upload failed: " + e.getMessage());
                return false;
            }
        }
    }

}
