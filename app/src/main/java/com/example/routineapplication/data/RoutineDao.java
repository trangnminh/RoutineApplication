package com.example.routineapplication.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.routineapplication.model.Routine;

import java.util.List;

@Dao
public interface RoutineDao {

    @Insert
    long insert(Routine routine);

    @Update
    void update(Routine routine);

    @Delete
    void delete(Routine routine);

    @Query("SELECT * FROM Routine")
    LiveData<List<Routine>> getAll();

    @Query("DELETE FROM Routine")
    void deleteAll();

    @Query("SELECT * FROM Routine")
    List<Routine> getAllForService();
}
