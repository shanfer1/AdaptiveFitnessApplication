package com.fitness.myapplication;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.fitness.database.ExerciseDatabase;
import com.fitness.database.FitnessStore;

public class AppInitializer {
    public static void initializeApp(Context context){
        checkandInitializeDB(context);
    }

    private static void checkandInitializeDB(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("DBPreferences", context.MODE_PRIVATE);
        boolean isDBInitialised = sharedPreferences.getBoolean("isDatabasePopulated", false);

        if(!isDBInitialised){
            initializeDB(context);
        }
    }

    private static void initializeDB(Context context) {
        new Thread(() -> {
            ExerciseDatabase exerciseDatabase = ExerciseDatabase.getInstance(context);
            
            if(!exerciseDatabase.isDatabasePopulated()){
                Log.i("AppInitializer", "Prepopulating Databse");
                FitnessStore dbStore = new FitnessStore(exerciseDatabase, context);
                dbStore.prePopulateFitnessStore();
                exerciseDatabase.setDatabasePopulated(true);
            }
        }).start();
    }

}
