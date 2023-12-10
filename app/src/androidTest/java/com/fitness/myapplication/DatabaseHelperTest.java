package com.fitness.myapplication;

import android.content.Context;
import androidx.test.core.app.ApplicationProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class DatabaseHelperTest {

    private DatabaseHelper db;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        db = new DatabaseHelper(context);
    }

    @After
    public void finish() {
        db.close();
    }

    @Test
    public void testInsertNewRecordAndRetrieve() {
        String userId = "testupdate@gmail.com";
        float caloriesBurnt = 150.0f;

        db.insertNewRecord(userId, caloriesBurnt);

        float retrievedCalories = db.getLatestCalories(userId);

        assertEquals(caloriesBurnt, retrievedCalories, 0.01);
    }

    @Test
    public void testUpdateWorkoutNumber() {
        String userId = "testupdate@gmail.com";
        int newWorkoutNumber = 5;

        db.updateWorkoutNumber(userId, newWorkoutNumber);

        int retrievedWorkoutNumber = db.getUserWorkoutNumber(userId);

        assertEquals(newWorkoutNumber, retrievedWorkoutNumber);
    }
}

