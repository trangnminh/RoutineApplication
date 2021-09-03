package com.example.routineapplication.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.routineapplication.model.Task;

import java.util.List;

@Dao
public interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Task task);

    @Query("SELECT * FROM Task WHERE routineId=:routineId")
    LiveData<List<Task>> getAllByRoutineId(int routineId);

    @Query("SELECT * FROM Task WHERE routineId=:routineId")
    List<Task> getAllByRoutineIdForClone(int routineId);

    @Query("DELETE FROM Task")
    void deleteAll();
}
