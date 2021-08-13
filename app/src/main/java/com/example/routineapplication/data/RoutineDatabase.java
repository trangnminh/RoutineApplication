package com.example.routineapplication.data;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.routineapplication.model.Routine;

@Database(entities = {Routine.class}, version = 1, exportSchema = false)
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
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract RoutineDao routineDao();

    private static class populateDbAsync extends AsyncTask<Void, Void, Void> {

        private final RoutineDao mDao;

        populateDbAsync(RoutineDatabase db) {
            mDao = db.routineDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Clean slate
            mDao.deleteAll();

            // Populate routines
            for (int i = 1; i <= 5; i++) {
                mDao.insert(new Routine(String.valueOf(i), "Sample data"));
            }

            return null;
        }
    }
}
