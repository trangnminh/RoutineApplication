package com.example.routineapplication.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.routineapplication.data.RoutineDatabase;
import com.example.routineapplication.data.TaskDao;
import com.example.routineapplication.model.Task;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskRepository {

    private final TaskDao mTaskDao;
    private final ExecutorService mExecutorService;

    public TaskRepository(Application application) {
        RoutineDatabase db = RoutineDatabase.getDatabase(application);
        mTaskDao = db.taskDao();
        mExecutorService = Executors.newCachedThreadPool();
    }

    public void insert(Task task) {
        Runnable runnable = () -> mTaskDao.insert(task);
        mExecutorService.execute(runnable);
    }

    public void update(Task task) {
        Runnable runnable = () -> mTaskDao.update(task);
        mExecutorService.execute(runnable);
    }

    public void delete(Task task) {
        Runnable runnable = () -> mTaskDao.delete(task);
        mExecutorService.execute(runnable);
    }

    public LiveData<List<Task>> getAllByRoutineId(int routineId) {
        return mTaskDao.getAllByRoutineId(routineId);
    }

    public List<Task> getAllByRoutineIdForClone(int routineId) {
        return mTaskDao.getAllByRoutineIdForClone(routineId);
    }
}
