package com.fitness.database;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.fitness.database.dao.FitnessDao;
import com.fitness.database.schema.Equipment;
import com.fitness.database.schema.ExcersiseSchedulerConverter;
import com.fitness.database.schema.Exercise;
import com.fitness.database.schema.MuscleGroup;
import com.fitness.database.schema.Schedule;
import com.fitness.myapplication.ExerciseScheduler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Database(entities = {Exercise.class, MuscleGroup.class, Equipment.class, Schedule.class}, version = 10, exportSchema = false)
@TypeConverters(ExcersiseSchedulerConverter.class)
public abstract class ExerciseDatabase extends RoomDatabase{
    public abstract FitnessDao fitnessDao();

    private static ExerciseDatabase instance;

    public static synchronized ExerciseDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            ExerciseDatabase.class, "exercise")
                    .fallbackToDestructiveMigration()
                    .build();
            instance.checkIfDatabaseIsPopulated();
        }
        return instance;
    }

    /*
        Implementation for checking DB population only occur once while app startup
     */
    private static boolean isDatabasePopulated = false;

    private void checkIfDatabaseIsPopulated() {
        boolean isPopulated = isExerciseTablePopulated(); // Call the method to check if the table is populated
        setDatabasePopulated(isPopulated); // Set the flag accordingly
    }

    public static boolean isDatabasePopulated() {
        return isDatabasePopulated;
    }

    public static void setDatabasePopulated(boolean value) {
        isDatabasePopulated = value;
    }

    public boolean isExerciseTablePopulated() {
        FitnessDao dao = fitnessDao();
        int rowCount = dao.countRows();
        return rowCount > 0;
    }

    public List<List<ExerciseScheduler.Result>> getNextScheuleForUser(String emailId) {
        FitnessDao dao = fitnessDao();
        List<List<ExerciseScheduler.Result>> results = new ArrayList<>();
        if(dao.getEntriesInScheduleForUser(emailId) > 0) {
            List<Schedule> schedules = dao.getNextScheduleForUser(emailId);
            for(Schedule schedule: schedules){
                List<ExerciseScheduler.Result> res= schedule.getSchedule();
                for(ExerciseScheduler.Result result: res)
                {
                    Log.i("RESULT " , String.valueOf(result.getWeightTaken()));
                }
                results.add(schedule.getSchedule());
            }
        }
        return results;
    }

}
