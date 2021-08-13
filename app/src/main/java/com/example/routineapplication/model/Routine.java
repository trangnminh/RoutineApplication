package com.example.routineapplication.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(tableName = "routines")
public class Routine implements Parcelable {

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
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String description;
    private boolean isAlarm;


    public Routine(String name, String description) {
        this.name = name;
        this.description = description;
    }

    protected Routine(Parcel in) {
        id = in.readInt();
        name = in.readString();
        description = in.readString();
        isAlarm = in.readByte() != 0;
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
        parcel.writeByte((byte) (isAlarm ? 1 : 0));
    }
}
