package com.dkondratov.opengame.util;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import com.dkondratov.opengame.R;
import com.dkondratov.opengame.activities.SplashScreenActivity;

class NotificationUtil {

	public static void showParseNotification(Context context, String head, String body) {
        Intent notificationIntent = new Intent(context, SplashScreenActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationManagerCompat nm = NotificationManagerCompat.from(context);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        // TODO: sound
        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.app_logo)
                .setTicker(context.getString(R.string.app_name)).setWhen(System.currentTimeMillis())
                .setAutoCancel(true).setContentTitle(head)
                .setContentText(body)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(body))
                .setVibrate(new long[]{500, 500});//.extend(wearableExtender);
        nm.notify(105, builder.build());

	}

}