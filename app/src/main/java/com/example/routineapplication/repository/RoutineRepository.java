package com.example.routineapplication.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.routineapplication.data.RoutineDao;
import com.example.routineapplication.data.RoutineDatabase;
import com.example.routineapplication.model.Routine;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RoutineRepository {

    // Return value of insert() method
    private static long id = -1;

    // Thread-safe latch to wait for async insert()
    private static CountDownLatch latch;

    private final RoutineDao mRoutineDao;
    private final ExecutorService mExecutorService;

    private final LiveData<List<Routine>> mRoutines;

    public RoutineRepository(Application application) {
        RoutineDatabase db = RoutineDatabase.getDatabase(application);
        mRoutineDao = db.routineDao();
        mExecutorService = Executors.newCachedThreadPool();
        mRoutines = mRoutineDao.getAll();
    }

    public long insert(Routine routine) {
        latch = new CountDownLatch(1);

        Runnable runnable = () -> {
            id = mRoutineDao.insert(routine);
            latch.countDown();
        };
        mExecutorService.execute(runnable);

        // Wait for insert() to finish and update the current id
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return id;
    }

    public void update(Routine routine) {
        Runnable runnable = () -> mRoutineDao.update(routine);
        mExecutorService.execute(runnable);
    }

    public void delete(Routine routine) {
        Runnable runnable = () -> mRoutineDao.delete(routine);
        mExecutorService.execute(runnable);
    }

    public LiveData<List<Routine>> getAll() {
        return mRoutines;
    }
}