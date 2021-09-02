package com.example.routineapplication.data;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.routineapplication.model.Routine;
import com.example.routineapplication.model.Task;

import java.util.ArrayList;
import java.util.Calendar;

@Database(entities = {Routine.class, Task.class}, version = 9, exportSchema = false)
@TypeConverters({ListToStringConverter.class})
public abstract class RoutineDatabase extends RoomDatabase {

    private static RoutineDatabase INSTANCE;
    private static final RoomDatabase.Callback sCallback =
            new RoomDatabase.Callback() {
                @Override
                public void onOpen(@NonNull SupportSQLiteDatabase db) {
                    super.onOpen(db);
                    new populateDbAsync(INSTANCE).execute();
                }
            };

    public static RoutineDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (RoutineDatabase.class) {
                if (INSTANCE == null) {
                    // Create database here
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            RoutineDatabase.class, "routine_database")
                            .addCallback(sCallback)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract RoutineDao routineDao();

    public abstract TaskDao taskDao();

    private static class populateDbAsync extends AsyncTask<Void, Void, Void> {

        private final RoutineDao mRoutineDao;
        private final TaskDao mTaskDao;

        populateDbAsync(RoutineDatabase db) {
            mRoutineDao = db.routineDao();
            mTaskDao = db.taskDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Clean slate -ish (this does not remove the alarms!)
            mRoutineDao.deleteAll();

            // Populate routine with tasks
            ArrayList<Integer> weekdays = new ArrayList<>();
            weekdays.add(Calendar.MONDAY);

            int routineId = (int) mRoutineDao.insert(new Routine("Routine", "Sample", false, "08:00", weekdays));
            mTaskDao.insert(new Task(routineId, "Task 1", 1));
            mTaskDao.insert(new Task(routineId, "Task 2", 2));

            return null;
        }
    }
}
