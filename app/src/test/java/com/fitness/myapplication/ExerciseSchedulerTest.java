package com.fitness.myapplication;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import android.content.Context;

import androidx.room.Room;

import com.fitness.database.ExerciseDatabase;
import com.fitness.database.dao.FitnessDao;
import com.fitness.database.schema.DifficultyLevel;
import com.fitness.database.schema.Equipment;
import com.fitness.database.schema.Exercise;
import com.fitness.database.schema.MuscleGroup;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Collections;


public class ExerciseSchedulerTest {

    @Mock
    private Context mockContext;
    @Mock
    private ExerciseDatabase exerciseDatabase;

    @Mock
    private FitnessDao mockfitnessDao;
    @Mock
    private CallbackListener mockCallbackListener;

    private ExerciseScheduler exerciseScheduler;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        // Initialize Robolectric and create an in-memory database
        exerciseScheduler = new ExerciseScheduler(mockfitnessDao, mockCallbackListener);
    }

    @Test
    public void testCalculateBMI() {
        double weight = 70; // kg
        double height = 1.75; // meters
        double expectedBMI = 22.86; // Expected BMI

        double resultBMI = exerciseScheduler.calculateBMI(weight, height);
        assertEquals(expectedBMI, resultBMI, 0.01); // Assert with a delta for floating point comparison
    }

    @Test
    public void testGroupExercises_EmptyList() {
        List<Exercise> exercises = Arrays.asList();
        Map<String, List<Exercise>> grouped = exerciseScheduler.groupExercises(exercises);
        assertTrue(grouped.isEmpty());
    }

    @Test
    public void testGroupExercises_SingleExercise() {
        Exercise exercise = new Exercise();
        exercise.setMuscleId(1);
        exercise.setEquipmentID(1);

        List<Exercise> exercises = Arrays.asList(exercise);
        Map<String, List<Exercise>> grouped = exerciseScheduler.groupExercises(exercises);

        assertEquals(1, grouped.size());
        assertTrue(grouped.containsKey("1-1"));
        assertEquals(1, grouped.get("1-1").size());
    }

    @Test
    public void testGroupExercises_DifferentGroups() {
        Exercise exercise1 = new Exercise();
        exercise1.setMuscleId(1);
        exercise1.setEquipmentID(1);

        Exercise exercise2 = new Exercise();
        exercise2.setMuscleId(2);
        exercise2.setEquipmentID(2);

        List<Exercise> exercises = Arrays.asList(exercise1, exercise2);
        Map<String, List<Exercise>> grouped = exerciseScheduler.groupExercises(exercises);

        assertEquals(2, grouped.size());
        assertTrue(grouped.containsKey("1-1"));
        assertTrue(grouped.containsKey("2-2"));
    }

    @Test
    public void testGroupExercises_SameGroup() {
        Exercise exercise1 = new Exercise();
        exercise1.setMuscleId(1);
        exercise1.setEquipmentID(1);

        Exercise exercise2 = new Exercise();
        exercise2.setMuscleId(1);
        exercise2.setEquipmentID(1);

        List<Exercise> exercises = Arrays.asList(exercise1, exercise2);
        Map<String, List<Exercise>> grouped = exerciseScheduler.groupExercises(exercises);

        assertEquals(1, grouped.size());
        assertTrue(grouped.containsKey("1-1"));
        assertEquals(2, grouped.get("1-1").size());
    }

    @Test
    public void testFilterExercises() {
        // Arrange
        List<String> mockMuscles = Arrays.asList("Muscle1", "Muscle2");
        List<String> mockEquipments = Arrays.asList("Equipment1", "Equipment2");
        DifficultyLevel mockDifficultyLevel = DifficultyLevel.BEGINEER;

        List<Exercise> expectedExercises = Arrays.asList(new Exercise(), new Exercise());
        when(mockfitnessDao.getSortedFilteredExercise(mockEquipments, mockMuscles, mockDifficultyLevel)).thenReturn(expectedExercises);

        // Act
        List<Exercise> resultExercises = exerciseScheduler.filterExercises(mockMuscles, mockEquipments, mockDifficultyLevel);

        // Assert
        assertEquals(expectedExercises, resultExercises);
        verify(mockfitnessDao).getSortedFilteredExercise(mockEquipments, mockMuscles, mockDifficultyLevel);
    }

    private Exercise createExerciseWithRating(double rating) {
        Exercise exercise = mock(Exercise.class);
        when(exercise.getRating()).thenReturn(rating);
        return exercise;
    }

    @Test
    public void testSelectRoundRobinExercises_EmptyMap() {
        Map<String, List<Exercise>> groupedExercises = new HashMap<>();
        List<Exercise> result = exerciseScheduler.selectRoundRobinExercises(groupedExercises);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testCalculateCalories_NormalBMI() {
        double metScore = 10;
        double weightTaken = 2;
        double weightFactor = 1.5;
        int duration = 3; // minutes
        double bmi = 25;

        double expectedCalories = metScore * weightTaken * weightFactor * duration;
        double calculatedCalories = exerciseScheduler.calculateCalories(metScore, weightTaken, weightFactor, duration, bmi);

        assertEquals(expectedCalories, calculatedCalories, 0.01);
    }

    @Test
    public void testCalculateCalories_HighBMI() {
        double metScore = 10;
        double weightTaken = 2;
        double weightFactor = 1.5;
        int duration = 3; // minutes
        double bmi = 31;

        double adjustedMetScore = metScore * 0.8; // Adjustment for high BMI
        double expectedCalories = adjustedMetScore * weightTaken * weightFactor * duration;
        double calculatedCalories = exerciseScheduler.calculateCalories(metScore, weightTaken, weightFactor, duration, bmi);

        assertEquals(expectedCalories, calculatedCalories, 0.01);
    }

    @Test
    public void testCalculateCalories_BoundaryBMI() {
        double metScore = 10;
        double weightTaken = 2;
        double weightFactor = 1.5;
        int duration = 3; // minutes
        double bmi = 30;

        double adjustedMetScore = metScore * 0.8; // Adjustment for high BMI
        double expectedCalories = adjustedMetScore * weightTaken * weightFactor * duration;
        double calculatedCalories = exerciseScheduler.calculateCalories(metScore, weightTaken, weightFactor, duration, bmi);

        assertEquals(expectedCalories, calculatedCalories, 0.01);
    }

    private Exercise createMockExercise(double metScore, double weightFactor) {
        Exercise exercise = mock(Exercise.class);
        when(exercise.getMetScore()).thenReturn(metScore);
        exercise.weightFactor = weightFactor;
        return exercise;
    }

    @Test
    public void testCalculateAndLimitCalories_NoLimitExceeded() {
        List<Exercise> exercises = new ArrayList<>();
        exercises.add(createMockExercise(5, 1));
        double weightTaken = 1;
        double calorieLimit = 300; // High enough to not exceed the limit
        double bmi = 25;

        List<ExerciseScheduler.Result> results = exerciseScheduler.calculateAndLimitCalories(exercises, weightTaken, calorieLimit, bmi);

        assertEquals("Expected 3 results for 3 reps", 3, results.size());
    }

    @Test
    public void testCalculateAndLimitCalories_LimitExceededOnThirdRep() {
        List<Exercise> exercises = new ArrayList<>();
        exercises.add(createMockExercise(5, 1));
        double weightTaken = 1;
        double calorieLimit = 25; // Limit exceeds after adding two reps
        double bmi = 25;

        List<ExerciseScheduler.Result> results = exerciseScheduler.calculateAndLimitCalories(exercises, weightTaken, calorieLimit, bmi);

        assertEquals("Expected 2 results as the 3rd rep exceeds limit", 2, results.size());
    }

    @Test
    public void testCalculateAndLimitCalories_LimitExceededOnSecondRep() {
        List<Exercise> exercises = new ArrayList<>();
        exercises.add(createMockExercise(5, 1));
        double weightTaken = 1;
        double calorieLimit = 15; // Limit exceeds after adding one rep
        double bmi = 25;

        List<ExerciseScheduler.Result> results = exerciseScheduler.calculateAndLimitCalories(exercises, weightTaken, calorieLimit, bmi);

        assertEquals("Expected 1 result as the 2nd rep exceeds limit", 1, results.size());
    }

    @Test
    public void testCalculateAndLimitCalories_LimitExceededOnFirstRep() {
        List<Exercise> exercises = new ArrayList<>();
        exercises.add(createMockExercise(5, 1));
        double weightTaken = 1;
        double calorieLimit = 8; // Limit exceeds right at the first rep
        double bmi = 25;

        List<ExerciseScheduler.Result> results = exerciseScheduler.calculateAndLimitCalories(exercises, weightTaken, calorieLimit, bmi);

        assertTrue("Expected no results as the 1st rep exceeds limit", results.isEmpty());
    }

    @Test
    public void testCalculateAndLimitCalories_EmptyExerciseList() {
        List<Exercise> exercises = new ArrayList<>();
        double weightTaken = 1;
        double calorieLimit = 300;
        double bmi = 25;

        List<ExerciseScheduler.Result> results = exerciseScheduler.calculateAndLimitCalories(exercises, weightTaken, calorieLimit, bmi);

        assertTrue("Expected no results with an empty exercise list", results.isEmpty());
    }

    private ExerciseScheduler.Result createMockResult(String exerciseName) {
        Exercise exercise = mock(Exercise.class);
        exercise.name = exerciseName;
        return new ExerciseScheduler.Result(exercise, 0);
    }

    @Test
    public void testShuffleAndGroupExercises_EmptyList() {
        List<ExerciseScheduler.Result> results = exerciseScheduler.shuffleAndGroupExercises(new ArrayList<>());
        assertTrue("Expected empty list", results.isEmpty());
    }

    @Test
    public void testShuffleAndGroupExercises_SingleExerciseGroup() {
        List<ExerciseScheduler.Result> input = Arrays.asList(createMockResult("Exercise1"), createMockResult("Exercise1"));
        List<ExerciseScheduler.Result> results = exerciseScheduler.shuffleAndGroupExercises(input);

        assertEquals("Expected 2 results", 2, results.size());
        assertEquals("All exercises should be 'Exercise1'", "Exercise1", results.get(0).exercise.name);
        assertEquals("All exercises should be 'Exercise1'", "Exercise1", results.get(1).exercise.name);
    }

    @Test
    public void testShuffleAndGroupExercises_MultipleExerciseGroups() {
        List<ExerciseScheduler.Result> input = Arrays.asList(createMockResult("Exercise1"), createMockResult("Exercise2"));
        List<ExerciseScheduler.Result> results = exerciseScheduler.shuffleAndGroupExercises(input);

        assertEquals("Expected 2 results", 2, results.size());
        assertTrue("Results should contain Exercise1", results.stream().anyMatch(r -> r.exercise.name.equals("Exercise1")));
        assertTrue("Results should contain Exercise2", results.stream().anyMatch(r -> r.exercise.name.equals("Exercise2")));
    }

    @Test
    public void testShuffleAndGroupExercises_ShufflingIntegrity() {
        List<ExerciseScheduler.Result> input = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            input.add(createMockResult("Exercise" + (i % 3)));
        }

        List<ExerciseScheduler.Result> results = exerciseScheduler.shuffleAndGroupExercises(input);
        assertEquals("Expected same number of results as input", input.size(), results.size());

        // Ensure all original exercises are present in the result
        for (ExerciseScheduler.Result original : input) {
            assertTrue("Results should contain all original exercises", results.stream().anyMatch(r -> r.exercise.name.equals(original.exercise.name)));
        }
    }

}
