package com.fitness.database.schema;

import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

public class DifficultyLevelConverter {
    @TypeConverter
    public static DifficultyLevel toDifficultyLevel(String value){
        return value == null ? null : DifficultyLevel.valueOf(value);
    }

    @TypeConverter
    public static String fromDifficultyLevel(DifficultyLevel level){
        return level == null ? null: level.name();
    }
}
