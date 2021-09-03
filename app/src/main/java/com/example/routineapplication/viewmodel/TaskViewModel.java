package com.example.routineapplication.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.routineapplication.model.Task;
import com.example.routineapplication.repository.TaskRepository;

import java.util.List;

public class TaskViewModel extends AndroidViewModel {

    private final TaskRepository mRepository;

    public TaskViewModel(@NonNull Application application) {
        super(application);
        this.mRepository = new TaskRepository(application);
    }

    public void insert(Task task) {
        mRepository.insert(task);
    }

    public LiveData<List<Task>> getAllByRoutineId(int routineId) {
        return mRepository.getAllByRoutineId(routineId);
    }

    public List<Task> getAllByRoutineIdForClone(int routineId) {
        return mRepository.getAllByRoutineIdForClone(routineId);
    }
}
