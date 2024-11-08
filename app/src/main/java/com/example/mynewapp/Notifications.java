package com.example.mynewapp;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class Notifications {

    private final String NOTIFYTAG = "new_request";

    public void notify(Context context, String message, int number) {
        Intent intent = new Intent(context, LoginActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFYTAG)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentTitle("New Request")
                .setContentText(message)
                .setNumber(number)
                .setSmallIcon(R.drawable.tictac)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) {
            nm.notify(NOTIFYTAG.hashCode(), builder.build());
        }
    }
}
