package com.fitness.database.schema;

import androidx.room.Entity;

@Entity(tableName = "exercise_muscle_group",
        primaryKeys = {"exerciseId", "muscleGroupId"})
public class ExerciseMuscleGroup {

    public int exerciseId;
    public int muscleGroupId;
}
