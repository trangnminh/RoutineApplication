package com.example.routineapplication.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(tableName = "Task", foreignKeys =
        {@ForeignKey(entity = Routine.class,
                parentColumns = "id",
                childColumns = "routineId",
                onUpdate = ForeignKey.CASCADE,
                onDelete = ForeignKey.CASCADE)})
public class Task implements Parcelable {

    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int routineId;
    private String name;
    private int durationInMinutes;

    protected Task(Parcel in) {
        id = in.readInt();
        routineId = in.readInt();
        name = in.readString();
        durationInMinutes = in.readInt();
    }

    public Task(int routineId, String name, int durationInMinutes) {
        this.routineId = routineId;
        this.name = name;
        this.durationInMinutes = durationInMinutes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(routineId);
        parcel.writeString(name);
        parcel.writeInt(durationInMinutes);
    }
}
