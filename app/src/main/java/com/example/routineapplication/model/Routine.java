package com.example.routineapplication.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(tableName = "routines")
public class Routine implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String description;
    public static final Creator<Routine> CREATOR = new Creator<Routine>() {
        @Override
        public Routine createFromParcel(Parcel in) {
            return new Routine(in);
        }

        @Override
        public Routine[] newArray(int size) {
            return new Routine[size];
        }
    };
    private boolean isAlarmEnabled;
    private String enabledTime;
    private ArrayList<Integer> enabledWeekdays;

    public Routine(String name, String description, boolean isAlarmEnabled, String enabledTime, ArrayList<Integer> enabledWeekdays) {
        this.name = name;
        this.description = description;
        this.isAlarmEnabled = isAlarmEnabled;
        this.enabledTime = enabledTime;
        this.enabledWeekdays = enabledWeekdays;
    }

    protected Routine(Parcel in) {
        id = in.readInt();
        name = in.readString();
        description = in.readString();
        isAlarmEnabled = in.readByte() != 0;
        enabledTime = in.readString();

        in.readList(enabledWeekdays, Integer.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeString(description);
        parcel.writeByte((byte) (isAlarmEnabled ? 1 : 0));
        parcel.writeString(enabledTime);

        parcel.writeList(this.getEnabledWeekdays());
    }
}
