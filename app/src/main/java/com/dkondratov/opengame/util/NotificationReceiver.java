package com.dkondratov.opengame.util;

import android.content.Context;
import android.content.Intent;

import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

public class NotificationReceiver extends ParsePushBroadcastReceiver {

    public NotificationReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent != null && intent.getExtras() != null) {
            try {
                JSONObject jsonObject = new JSONObject(intent.getExtras().getString("com.parse.Data"));
                String head = jsonObject.getString("head");
                String text = jsonObject.getString("text");

                NotificationUtil.showParseNotification(context, head, text);

            } catch (JSONException ex) {
                ex.printStackTrace();
            } catch (NullPointerException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    protected void onPushOpen(Context context, Intent intent) {
        super.onPushOpen(context, intent);
    }

    @Override
    protected void onPushReceive(Context context, Intent intent) {
        super.onPushReceive(context, intent);
    }
}
