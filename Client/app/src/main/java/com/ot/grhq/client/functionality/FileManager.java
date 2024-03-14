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

public class FileManager {

    public static void downloadFile(Context context, String url, String filenameWithExtension) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filenameWithExtension);
        // Hide download notification
        request.setVisibleInDownloadsUi(false);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        downloadManager.enqueue(request);
    }

    public static void uploadFile(String path, String uploadUrl) {
        new UploadTask(path, uploadUrl).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public static List<String> listFiles(String path) {
        List<String> files = new ArrayList<>();

        String absolutePath = Environment.getExternalStorageDirectory().toString() + path;
        File directory = new File(absolutePath);
        String[] constituents = directory.list();
        for (String file : constituents)
            files.add(file);

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
            String boundary = Long.toHexString(System.currentTimeMillis()); // Generate unique boundary

            try {
                // Open a connection to the server
                URL url = new URL(uploadUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "multipart/form-data;  boundary=" + boundary);

                OutputStream outputStream = connection.getOutputStream();
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"), true);

                // Write file content
                writer.append("--" + boundary).append("\r\n");
                writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"").append("\r\n");
                writer.append("Content-Type: text/plain").append("\r\n"); // Adjust content type as needed
                writer.append("\r\n");
                writer.flush();

                FileInputStream fileInputStream = new FileInputStream(file);
                int bufferSize = 1024000;
                byte[] buffer = new byte[bufferSize];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);

                    // Dynamically adjust buffer size
                    if (bytesRead == bufferSize) {
                        // If the buffer was filled, increase its size for next iteration
                        bufferSize *= 2; // Doubling the buffer size
                        buffer = new byte[bufferSize];
                    }
                }
                outputStream.flush();
                fileInputStream.close();

                // End of multipart/form-data
                writer.append("\r\n").append("--" + boundary + "--").append("\r\n");
                writer.close();

                // Get the server response code
                int responseCode = connection.getResponseCode();

                // Check if the upload was successful
                return responseCode == HttpURLConnection.HTTP_OK;
            } catch (Exception e) {
                return false;
            }
        }
    }
}
