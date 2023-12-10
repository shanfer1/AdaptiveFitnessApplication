package com.fitness.database.schema;

import android.os.Parcel;

import androidx.room.TypeConverter;

import com.fitness.myapplication.ExerciseScheduler.Result;

import java.util.ArrayList;
import java.util.List;

public class ExcersiseSchedulerConverter {
    @TypeConverter
    public static byte[] fromResult(List<Result> result) {
        Parcel parcel = Parcel.obtain();
        parcel.writeTypedList(result);
        byte[] bytes = parcel.marshall();
        parcel.recycle();
        return bytes;
    }

    @TypeConverter
    public static List<Result> toResult(byte[] bytes) {
        Parcel parcel = Parcel.obtain();
        parcel.unmarshall(bytes, 0, bytes.length);
        parcel.setDataPosition(0);
        List<Result> resultList = new ArrayList<>();
        parcel.readTypedList(resultList, Result.CREATOR);
        parcel.recycle();
        return resultList;
    }
}
