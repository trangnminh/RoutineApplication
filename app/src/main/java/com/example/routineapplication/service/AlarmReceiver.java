package com.example.routineapplication.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.routineapplication.MainActivity;
import com.example.routineapplication.R;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {

    public static final String ACTION_SET_ALARM = "ACTION_SET_ALARM";
    public static final String TAG_NOTIFICATION_ID = "notification_id";
    public static final String TAG_TITLE = "title";
    public static final String TAG_MESSAGE = "message";
    public static final String TAG_ENABLED_WEEKDAYS = "weekdays";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String TIME_FORMAT = "HH:mm";
    public static final String CHANNEL_ID = "routine_alarm";
    public static final String CHANNEL_TITLE = "Routine Application";
    public static final String TAG = "AlarmReceiver";
    public static final long[] VIBRATION_PATTERN = {1000, 1000, 1000, 1000, 1000};

    @Override
    public void onReceive(Context context, Intent intent) {
        // Is triggered when alarm goes off, i.e. receiving a system broadcast
        if (intent.getAction().equals(ACTION_SET_ALARM)) {
            int notificationId = intent.getIntExtra(TAG_NOTIFICATION_ID, 0);
            String title = intent.getStringExtra(TAG_TITLE);
            String message = intent.getStringExtra(TAG_MESSAGE);
            ArrayList<Integer> weekdays = intent.getIntegerArrayListExtra(TAG_ENABLED_WEEKDAYS);

            // Show notification if today is enabled
            Calendar calendar = Calendar.getInstance();
            Integer today = calendar.get(Calendar.DAY_OF_WEEK);
            if (weekdays.contains(today))
                showNotification(context, notificationId, title, message);

            // Wake the screen (needs permission WAKELOCK)
            PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "routine:wakelock");
            wakeLock.acquire(5 * 60 * 1000L /*5 minutes*/);

            // Set the next alarm
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(TIME_FORMAT);

            LocalDateTime next = LocalDateTime.now().plusDays(1);
            String nextDate = dateFormatter.format(next);
            String nextTime = timeFormatter.format(next);

            setOneTimeAlarm(context, notificationId, nextDate, nextTime, title, message, weekdays);
        }
    }

    public void showNotification(Context context, int notificationId, String title, String message) {
        // Set up a notification manager
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(
                        Context.NOTIFICATION_SERVICE
                );

        Uri alarmSound = RingtoneManager.getDefaultUri(
                RingtoneManager.TYPE_NOTIFICATION
        );

        // Open app if notification clicked
        Intent resultIntent = new Intent(context, MainActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, notificationId, resultIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        // Build the notification layout
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setSmallIcon(R.drawable.ic_loop_small)
                        .setColor(ContextCompat.getColor(context, android.R.color.transparent))
                        .setVibrate(VIBRATION_PATTERN)
                        .setSound(alarmSound)
                        .setContentIntent(resultPendingIntent)
                        .setAutoCancel(true);

        // Android SDK 26 and up requires a notification channel
        NotificationChannel notificationChannel =
                new NotificationChannel(
                        CHANNEL_ID,
                        CHANNEL_TITLE,
                        NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.enableVibration(true);
        notificationChannel.setVibrationPattern(VIBRATION_PATTERN);

        builder.setChannelId(CHANNEL_ID);

        // Send notification through channel
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(notificationChannel);
            Notification notification = builder.build();
            notificationManager.notify(notificationId, notification);
        }
    }

    public void setOneTimeAlarm(Context context,
                                int notificationId,
                                String date,
                                String time,
                                String title,
                                String message,
                                ArrayList<Integer> weekdays) {
        // Get AlarmManager instance
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Set the date and time
        String[] dateArray = date.split("-");
        String[] timeArray = time.split(":");

        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.YEAR, Integer.parseInt(dateArray[0]));
        calendar.set(Calendar.MONTH, Integer.parseInt(dateArray[1]) - 1);
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateArray[2]));
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(timeArray[1]));
        calendar.set(Calendar.SECOND, 0);

        // Build Intent
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setAction(ACTION_SET_ALARM);
        intent.putExtra(TAG_NOTIFICATION_ID, notificationId);
        intent.putExtra(TAG_TITLE, title);
        intent.putExtra(TAG_MESSAGE, message);
        intent.putExtra(TAG_ENABLED_WEEKDAYS, weekdays);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        // Set with system Alarm Service
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        Log.i(TAG, "setOneTimeAlarm: Notification ID=" + notificationId + " at " + time + " " + date);

    }

    public void cancelAlarm(Context context, int notificationId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setAction(ACTION_SET_ALARM);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_CANCEL_CURRENT);

        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
            Log.i(TAG, "cancelAlarm: Notification ID=" + notificationId);
        }
    }
}
