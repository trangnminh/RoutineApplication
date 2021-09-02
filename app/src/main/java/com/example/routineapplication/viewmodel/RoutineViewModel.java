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

    public RoutineViewModel(@NonNull Application application) {
        super(application);

        mRepository = new RoutineRepository(application);
    }

    public long insert(Routine routine) {
        return mRepository.insert(routine);
    }

    public void update(Routine routine) {
        mRepository.update(routine);
    }

    public void delete(Routine routine) {
        mRepository.delete(routine);
    }

    public LiveData<List<Routine>> getAll() {
        return mRepository.getAll();
    }
}
