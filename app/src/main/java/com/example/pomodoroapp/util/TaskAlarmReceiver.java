package com.example.pomodoroapp.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.example.pomodoroapp.util.NotificationHelper;

public class TaskAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationHelper notificationHelper = new NotificationHelper(context);
        NotificationCompat.Builder noti_builder = notificationHelper.getChannelNotification();
        notificationHelper.getManager().notify(1, noti_builder.build());
    }
}
