package com.example.routineapplication.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class TimeChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        // When user manually set date time or time zone
        if (action.equals(Intent.ACTION_TIME_CHANGED) ||
                action.equals(Intent.ACTION_TIMEZONE_CHANGED)) {

            // Restart all alarms
            Intent i = new Intent(context, RestartAlarmsService.class);
            context.startService(i);
        }
    }
}
