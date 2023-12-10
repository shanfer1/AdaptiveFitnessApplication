package com.fitness.myapplication;

import android.content.Context;

public class DatabaseHelperFactory {
    private static DatabaseHelper instance = null;
    public static DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = DatabaseHelper.getInstance(context);
        }
        return instance;
    }
    public static void setMockInstance(DatabaseHelper mockInstance) {
        instance = mockInstance;
    }
}
