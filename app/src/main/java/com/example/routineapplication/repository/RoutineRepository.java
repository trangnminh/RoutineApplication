package com.example.routineapplication.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.routineapplication.data.RoutineDao;
import com.example.routineapplication.data.RoutineDatabase;
import com.example.routineapplication.model.Routine;

import java.util.List;

public class RoutineRepository {

    private final RoutineDao mRoutineDao;

    private final LiveData<List<Routine>> mRoutines;

    public RoutineRepository(Application application) {
        RoutineDatabase db = RoutineDatabase.getDatabase(application);
        mRoutineDao = db.routineDao();
        mRoutines = mRoutineDao.getAll();
    }

    public void insert(Routine routine) {
        new insertAsyncTask(mRoutineDao).execute(routine);
    }

    public void update(Routine routine) {
        new updateAsyncTask(mRoutineDao).execute(routine);
    }

    public void delete(Routine routine) {
        new deleteAsyncTask(mRoutineDao).execute(routine);
    }

    public LiveData<Routine> getById(int id) {
        return mRoutineDao.getById(id);
    }

    public LiveData<List<Routine>> getAll() {
        return mRoutines;
    }

    public void deleteAll() {
        mRoutineDao.deleteAll();
    }

    // Insert routine
    private static class insertAsyncTask extends AsyncTask<Routine, Void, Void> {

        private final RoutineDao mDao;

        insertAsyncTask(RoutineDao dao) {
            this.mDao = dao;
        }

        @Override
        protected Void doInBackground(Routine... routines) {
            mDao.insert(routines[0]);
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