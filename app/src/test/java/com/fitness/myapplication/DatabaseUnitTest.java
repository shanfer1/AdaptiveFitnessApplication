package com.fitness.myapplication;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import android.app.Application;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import com.fitness.database.ExerciseDatabase;
import com.fitness.database.dao.FitnessDao;
import com.fitness.database.schema.Equipment;
import com.fitness.database.schema.Exercise;
import com.fitness.database.schema.DifficultyLevel;
import com.fitness.database.schema.MuscleGroup;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

@RunWith(RobolectricTestRunner.class)
public class DatabaseUnitTest {
    private ExerciseDatabase exerciseDatabase;
    private FitnessDao fitnessDao;

    @Before
    public void setup() {
        // Initialize Robolectric and create an in-memory database
        exerciseDatabase = Room.inMemoryDatabaseBuilder(
                        RuntimeEnvironment.application, ExerciseDatabase.class)
                .allowMainThreadQueries()
                .build();

        fitnessDao = exerciseDatabase.fitnessDao();
    }

    @After
    public void tearDown() {
        // Close the database after each test
        exerciseDatabase.close();
    }

    @Test
    public void testIsExerciseTablePopulatedWithNoData() {
        // Check if the Exercise table is populated with no data
        boolean isPopulated = exerciseDatabase.isExerciseTablePopulated();
        assertFalse(isPopulated);
    }

    @Test
    public void testIsExerciseTablePopulatedWithData() {
        // Add test data to the Exercise table
        Equipment testEquipment = new Equipment("equipment");
        int eqId = (int)fitnessDao.insertEquipment(testEquipment);
        MuscleGroup testMuscleGroup = new MuscleGroup("muscleGr");
        int muscleID = (int)fitnessDao.insertMuscleGroup(testMuscleGroup);

        Exercise testExercise = new Exercise("Test Exercise", "Description",
                "Type", DifficultyLevel.BEGINEER, muscleID, eqId, 4.5, 2, 1);
        fitnessDao.insertExercise(testExercise);

        // Check if the Exercise table is populated with data
        boolean isPopulated = exerciseDatabase.isExerciseTablePopulated();
        assertTrue(isPopulated);
    }

    @Test
    public void testIsDatabasePopulatedInitiallyFalse() {
        // Check if the initial state of isDatabasePopulated is false
        boolean isInitiallyPopulated = exerciseDatabase.isDatabasePopulated();
        assertFalse(isInitiallyPopulated);
    }

    @Test
    public void testSetDatabasePopulated() {
        // Set isDatabasePopulated to true
        exerciseDatabase.setDatabasePopulated(true);

        // Check if isDatabasePopulated is true
        boolean isPopulated = exerciseDatabase.isDatabasePopulated();
        assertTrue(isPopulated);
    }

    @Test
    public void testGetInstance() {
        Application application = ApplicationProvider.getApplicationContext();
        // Get an instance of ExerciseDatabase
        ExerciseDatabase instance1 = Room.inMemoryDatabaseBuilder(application, ExerciseDatabase.class).build();
        assertNotNull(instance1);
    }
}
