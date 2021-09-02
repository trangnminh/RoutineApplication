package com.example.routineapplication.data;

import androidx.room.TypeConverter;

import java.util.ArrayList;

// Convert an ArrayList<Integer> to a String and backwards
public class ListToStringConverter {

    @TypeConverter
    public static ArrayList<Integer> fromString(String values) {
        ArrayList<Integer> list = new ArrayList<>();
        String[] array = values.split(",");

        for (String s : array)
            list.add(Integer.parseInt(s));
        return list;
    }

    @TypeConverter
    public static String fromArrayList(ArrayList<Integer> list) {
        StringBuilder values = new StringBuilder();

        for (Integer i : list) {
            values.append(i.intValue());
            // The last element in list does not have comma after
            if (list.indexOf(i) != list.size() - 1)
                values.append(",");
        }
        return values.toString();
    }
}
