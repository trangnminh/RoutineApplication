package com.example.routineapplication.service;

import android.content.Context;

import androidx.fragment.app.Fragment;

import com.google.android.material.chip.Chip;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlarmHandler {

    private final AlarmReceiver mAlarmReceiver;
    private int mYear, mMonth, mDay, mHour, mMinute;

    public AlarmHandler() {
        this.mAlarmReceiver = new AlarmReceiver();
        updateCalendar();
    }

    // Show a time picker and update UI
    public void setAlarmTime(Fragment fragment, Chip alarmTime) {
        updateCalendar();

        MaterialTimePicker timePicker =
                new MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_24H)
                        .setHour(mHour)
                        .setMinute(mMinute)
                        .setTitleText("Alarm Time")
                        .build();

        timePicker.addOnPositiveButtonClickListener(
                view -> alarmTime.setText(
                        String.format(Locale.getDefault(),
                                "%02d:%02d",
                                timePicker.getHour(),
                                timePicker.getMinute()
                        )
                ));
        timePicker.show(fragment.getParentFragmentManager(), "alarm_time_picker");
    }

    // Notification ID is unique Routine ID
    public void setAlarm(Context context, Chip alarmTime, int notificationId, String title, String message, ArrayList<Integer> weekdays) {
        updateCalendar();

        String currentDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", mYear, mMonth + 1, mDay);

        if (!alarmTime.getText().toString().isEmpty()) {
            mAlarmReceiver.setOneTimeAlarm(
                    context,
                    notificationId,
                    currentDate,
                    alarmTime.getText().toString(),
                    title,
                    message,
                    weekdays);
        }
    }

    public void cancelAlarm(Context context, int notificationId) {
        mAlarmReceiver.cancelAlarm(context, notificationId);
    }

    // Update current date time
    public void updateCalendar() {
        Calendar calendar = Calendar.getInstance();

        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
        mHour = calendar.get(Calendar.HOUR_OF_DAY);
        mMinute = calendar.get(Calendar.MINUTE);
    }
}
