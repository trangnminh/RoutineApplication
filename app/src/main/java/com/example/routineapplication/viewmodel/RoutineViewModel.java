package com.example.routineapplication.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.routineapplication.model.Routine;
import com.example.routineapplication.repository.RoutineRepository;

import java.util.List;

public class RoutineViewModel extends AndroidViewModel {

    private final RoutineRepository mRepository;

    private final LiveData<List<Routine>> mRoutines;

    public RoutineViewModel(@NonNull Application application) {
        super(application);

        mRepository = new RoutineRepository(application);
        mRoutines = mRepository.getAll();
    }

    public void insert(Routine routine) {
        mRepository.insert(routine);
    }

    public void update(Routine routine) {
        mRepository.update(routine);
    }

    public void delete(Routine routine) {
        mRepository.delete(routine);
    }

    public LiveData<Routine> getById(int id) {
        return mRepository.getById(id);
    }

    public LiveData<List<Routine>> getAll() {
        return mRepository.getAll();
    }

    public void deleteAll() {
        mRepository.deleteAll();
    }
}
