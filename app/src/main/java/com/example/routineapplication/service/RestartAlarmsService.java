package com.example.routineapplication.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.routineapplication.data.RoutineDatabase;
import com.example.routineapplication.model.Routine;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RestartAlarmsService extends IntentService {

    private static final String TAG = "RestartAlarmsService";

    public RestartAlarmsService() {
        // Required empty constructor
        super("RestartAlarmsService");
    }

    public RestartAlarmsService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        Runnable runnableTask = () -> {
            try {
                // Get all routines from database
                RoutineDatabase db = RoutineDatabase.getDatabase(getApplicationContext());
                List<Routine> routines = db.routineDao().getAllForService();
                Log.i(TAG, "onReceive: Routines queried=" + (routines != null));

                if (routines != null) {
                    // Get an AlarmReceiver
                    AlarmReceiver alarmReceiver = new AlarmReceiver();

                    // Get current date and time
                    LocalDate alarmDate = LocalDate.now();
                    LocalTime alarmTime = LocalTime.now();
                    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                    // Loop through routines to reset each alarm
                    for (Routine routine : routines) {
                        if (routine.isAlarmEnabled()) {
                            LocalTime enabledTime = LocalTime.parse(routine.getEnabledTime());

                            // If enabled time has already passed
                            if (alarmTime.isAfter(enabledTime))
                                alarmDate = alarmDate.plusDays(1);

                            String alarmDateString = alarmDate.format(dateFormatter);

                            // Set alarm for tomorrow
                            alarmReceiver.setOneTimeAlarm(getApplicationContext(), routine.getId(), alarmDateString, routine.getEnabledTime(), routine.getName(), routine.getDescription(), routine.getEnabledWeekdays());
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        executorService.execute(runnableTask);
    }
}
