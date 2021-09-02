package com.example.routineapplication.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.routineapplication.data.RoutineDao;
import com.example.routineapplication.data.RoutineDatabase;
import com.example.routineapplication.model.Routine;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class RoutineRepository {

    // Return value of insert() method
    private static long id = -1;

    // Thread-safe latch to wait for async insert()
    private static CountDownLatch latch;

    private final RoutineDao mRoutineDao;

    private final LiveData<List<Routine>> mRoutines;

    public RoutineRepository(Application application) {
        RoutineDatabase db = RoutineDatabase.getDatabase(application);
        mRoutineDao = db.routineDao();
        mRoutines = mRoutineDao.getAll();
    }

    public long insert(Routine routine) {
        latch = new CountDownLatch(1);

        // Wait for insert() to finish and update the current id
        new insertAsyncTask(mRoutineDao).execute(routine);
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return id;
    }

    public void update(Routine routine) {
        new updateAsyncTask(mRoutineDao).execute(routine);
    }

    public void delete(Routine routine) {
        new deleteAsyncTask(mRoutineDao).execute(routine);
    }

    public LiveData<List<Routine>> getAll() {
        return mRoutines;
    }

    // Insert routine
    private static class insertAsyncTask extends AsyncTask<Routine, Void, Void> {

        private final RoutineDao mDao;

        insertAsyncTask(RoutineDao dao) {
            this.mDao = dao;
        }

        @Override
        protected Void doInBackground(Routine... routines) {
            id = mDao.insert(routines[0]);
            latch.countDown();
            return null;
        }
    }

    // Update routine
    private static class updateAsyncTask extends AsyncTask<Routine, Void, Void> {

        private final RoutineDao mDao;

        updateAsyncTask(RoutineDao dao) {
            this.mDao = dao;
        }

        @Override
        protected Void doInBackground(Routine... routines) {
            mDao.update(routines[0]);
            return null;
        }
    }

    // Delete routine
    private static class deleteAsyncTask extends AsyncTask<Routine, Void, Void> {

        private final RoutineDao mDao;

        deleteAsyncTask(RoutineDao dao) {
            this.mDao = dao;
        }

        @Override
        protected Void doInBackground(Routine... routines) {
            mDao.delete(routines[0]);
            return null;
        }
    }
}