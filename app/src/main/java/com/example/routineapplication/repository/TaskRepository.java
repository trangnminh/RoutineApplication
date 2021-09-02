package com.example.routineapplication.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.routineapplication.data.RoutineDatabase;
import com.example.routineapplication.data.TaskDao;
import com.example.routineapplication.model.Task;

import java.util.List;

public class TaskRepository {

    private final TaskDao mTaskDao;

    public TaskRepository(Application application) {
        RoutineDatabase db = RoutineDatabase.getDatabase(application);
        mTaskDao = db.taskDao();
    }

    public void insert(Task task) {
        new insertAsyncTask(mTaskDao).execute(task);
    }

    public LiveData<List<Task>> getAllByRoutineId(int routineId) {
        return mTaskDao.getAllByRoutineId(routineId);
    }

    private static class insertAsyncTask extends AsyncTask<Task, Void, Void> {

        private final TaskDao mDao;

        insertAsyncTask(TaskDao dao) {
            this.mDao = dao;
        }

        @Override
        protected Void doInBackground(Task... tasks) {
            mDao.insert(tasks[0]);
            return null;
        }
    }
}
