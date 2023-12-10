package com.fitness.myapplication;

import static java.lang.Math.max;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import com.fitness.database.ExerciseDatabase;
import com.fitness.database.dao.FitnessDao;
import com.fitness.database.schema.DifficultyLevel;
import com.fitness.database.schema.Exercise;
import com.fitness.database.schema.Schedule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class ExerciseScheduler extends AsyncTask<ExerciseScheduler.Params, Void, List<List<ExerciseScheduler.Result>>> {
    public static final String EXERCISES_SELECTED_TAG = "EXERCISES SELECTED";
    public static final String EXERCISES_PLAN_TAG = "EXERCISES PLAN";
    private final CallbackListener callbackListener;
    private FitnessDao fitnessDao;
    private static final Random random = new Random();
    private String emailId;

    static class Params {
        private final String BODY_ONLY = "Body Only";
        List<String> equipments;
        double weight;
        double height;
        float calories;
        double weightTaken;
        int category;

        Params(List<String> equipments, double weight, double height, float calories, double weightTaken, int category) {
            this.equipments = equipments;
            this.equipments.add(BODY_ONLY);
            this.weight = weight;
            this.height = height;
            this.calories = calories;
            this.weightTaken = weightTaken / 10;
            this.category = category;
        }
    }

    public static class Result implements Parcelable {
        Exercise exercise;
        double weightTaken;

        Result(Exercise exercise, double weightTaken) {
            this.exercise = exercise;
            this.weightTaken = weightTaken * 10;
        }

        protected Result(Parcel in) {
            exercise = in.readParcelable(Exercise.class.getClassLoader());
            weightTaken = in.readDouble();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(@NonNull Parcel parcel, int i) {
            parcel.writeParcelable(exercise, i);
            parcel.writeDouble(weightTaken);
        }

        public static final Creator<Result> CREATOR = new Creator<Result>() {
            @Override
            public Result createFromParcel(Parcel in) {
                return new Result(in);
            }

            @Override
            public Result[] newArray(int size) {
                return new Result[size];
            }
        };

        public double getWeightTaken() {
            return weightTaken;
        }

        public Exercise getExercise() {
            return exercise;
        }
    }

    enum MusclesGroup {
        LEGS, PUSH, PULL, CORE, FULL_BODY
    }

    public ExerciseScheduler(FitnessDao fitnessDao, CallbackListener callbackListener, String emailId) {
        this.fitnessDao = fitnessDao;
        this.callbackListener = callbackListener;
        this.emailId = emailId;
    }

    @Override
    protected List<List<Result>> doInBackground(Params... paramsArray) {
        // Assuming the first element in params is the list of checked items
        Params params = paramsArray[0];
        double bmi = calculateBMI(params.weight, params.height);

        Map<MusclesGroup, List<String>> muscleGroups = getMusclesGroupListMap();
        List<List<Result>> weeklyExercise = generateWeeklySchedule(muscleGroups, params, bmi);

        return weeklyExercise;
    }

    private List<List<Result>> generateWeeklySchedule(Map<MusclesGroup, List<String>> muscleGroups, Params params, double bmi) {
        double calorieCoeff = 1.02; // TODO: Fetch from personalisation improvement during integration (feedback)
        List<MusclesGroup> mGroupList = new ArrayList<>(muscleGroups.keySet());
        Collections.shuffle(mGroupList);

        List<List<Result>> weeklySchedule = new ArrayList<>();
        for(MusclesGroup mGroup: mGroupList){
            WorkoutCategoryDistribution distribution = getWorkoutDistributionBasedOnBMI(bmi, params.category);

            List<Result> completeDayExercises = new ArrayList<>();
            List<String> muscleListForDay = muscleGroups.get(mGroup);

            for(Map.Entry<DifficultyLevel, Double> entry: distribution.getPercentageMap().entrySet()){
                DifficultyLevel difficultyLevel = entry.getKey();
                double percentage = entry.getValue();

                if(percentage <= 0)
                    continue;

                List<Result> plannedExercise = filterAndPlanExercises(params, muscleListForDay, bmi, difficultyLevel, percentage);
                completeDayExercises.addAll(plannedExercise);
            }
            weeklySchedule.add(completeDayExercises);
            params.calories *= calorieCoeff;

            this.fitnessDao.insertSchedule(new Schedule(this.emailId, completeDayExercises));
        }

        return weeklySchedule;
    }

    @NonNull
    private static Map<MusclesGroup, List<String>> getMusclesGroupListMap() {
        /*
            Mapping between MuscleGroup to muscles
         */
        Map<MusclesGroup, List<String>> muscleGroups = new HashMap<>();

        muscleGroups.put(MusclesGroup.FULL_BODY, new ArrayList<>());
        // Legs Group
        muscleGroups.put(MusclesGroup.LEGS, new ArrayList<>());
        List<String> legMuscles = Arrays.asList("Quadriceps","Hamstrings", "Calves", "Glutes", "Abductors", "Adductors");
        muscleGroups.get(MusclesGroup.LEGS).addAll(legMuscles);
        muscleGroups.get(MusclesGroup.FULL_BODY).addAll(legMuscles);

        // Push Group (Chest, Shoulders, Triceps)
        muscleGroups.put(MusclesGroup.PUSH, new ArrayList<>());
        List<String> pushMuscles = Arrays.asList("Chest", "Shoulders", "Triceps");
        muscleGroups.get(MusclesGroup.PUSH).addAll(pushMuscles);
        muscleGroups.get(MusclesGroup.FULL_BODY).addAll(pushMuscles);

        // Pull Group (Middle Back, Lats, Biceps, Forearms)
        muscleGroups.put(MusclesGroup.PULL, new ArrayList<>());
        List<String> pullMuscles = Arrays.asList("Lats", "Biceps", "Forearms", "Middle Back");
        muscleGroups.get(MusclesGroup.PULL).addAll(pullMuscles);
        muscleGroups.get(MusclesGroup.FULL_BODY).addAll(pullMuscles);

        // Core Group (Lower Back, Abdominals, Traps)
        muscleGroups.put(MusclesGroup.CORE, new ArrayList<>());
        List<String> coreMuscles = Arrays.asList("Lower Back", "Abdominals", "Traps");
        muscleGroups.get(MusclesGroup.CORE).addAll(coreMuscles);
        muscleGroups.get(MusclesGroup.FULL_BODY).addAll(coreMuscles);
        return muscleGroups;
    }

    @NonNull
    private List<Result> filterAndPlanExercises(Params params, List<String> muscleList, double bmi, DifficultyLevel difficultyLevel, double percentage) {
        List<Exercise> filteredExercises = filterExercises(muscleList, params.equipments, difficultyLevel);

        // Group exercises by muscle and equipment ID
        Map<String, List<Exercise>> groupedExercises = groupExercises(filteredExercises);

        // Select exercises and shuffle
        List<Exercise> selectedExercises = selectRoundRobinExercises(groupedExercises);
        List<String> ex_name = selectedExercises.stream().map(line->line.name).collect(Collectors.toList());
        Log.d(EXERCISES_SELECTED_TAG, difficultyLevel.toString() + "  " + ex_name.toString());

        double calories = params.calories * percentage / 100;
        List<Result> plannedExercise = calculateAndLimitCalories(selectedExercises, params.weightTaken, calories, bmi);
        ex_name = plannedExercise.stream().map(line->line.exercise.name).collect(Collectors.toList());

        Log.d(EXERCISES_PLAN_TAG, difficultyLevel.toString() + "  " + ex_name.toString());
        return shuffleAndGroupExercises(plannedExercise);
    }

    protected List<ExerciseScheduler.Result> shuffleAndGroupExercises(List<ExerciseScheduler.Result> exercises) {
        // Step 1: Group exercises by name
        Map<String, List<ExerciseScheduler.Result>> groupedExercises = new HashMap<>();
        for (ExerciseScheduler.Result exercise : exercises) {
            groupedExercises.computeIfAbsent(exercise.exercise.name, k -> new ArrayList<>()).add(exercise);
        }

        List<String> shuffledExerciseNames = new ArrayList<>(groupedExercises.keySet());
        Collections.shuffle(shuffledExerciseNames);


        // Step 3: Interleave groups
        List<ExerciseScheduler.Result> shuffledExercises = new ArrayList<>();
        for (String name : shuffledExerciseNames) {
            List<ExerciseScheduler.Result> group = groupedExercises.get(name);
            shuffledExercises.addAll(group);
        }

        // Step 4: Handle remaining exercises if any
        // In this case, all exercises should already be interleaved and added

        return shuffledExercises;
    }

    private WorkoutCategoryDistribution getWorkoutDistributionBasedOnBMI(double bmi, int category) {
        WorkoutCategoryDistribution distribution = new WorkoutCategoryDistribution();

        if(bmi>=30)
            category = max(0, category-2);
        else if (bmi >= 25)
            category = max(0, category-1);

        if (category == 0) { // Example BMI threshold for higher category adjustment
            distribution.setBeginnerPercentage(100); // More beginner workouts
        } else if (category == 1) { // Overweight
            distribution.setBeginnerPercentage(75);
            distribution.setIntermediatePercentage(25);
        } else if(category == 2){
            distribution.setBeginnerPercentage(50); // Lower BMI, more advanced workouts
            distribution.setIntermediatePercentage(50);
        } else if(category == 3){
            distribution.setBeginnerPercentage(25); // Lower BMI, more advanced workouts
            distribution.setIntermediatePercentage(50);
            distribution.setExpertPercentage(25);
        } else if(category == 4) {
            distribution.setBeginnerPercentage(0); // Lower BMI, more advanced workouts
            distribution.setIntermediatePercentage(75);
            distribution.setExpertPercentage(25);
        }
        else {
            distribution.setIntermediatePercentage(50);
            distribution.setExpertPercentage(50);
        }

        return distribution;
    }

    protected List<Result> calculateAndLimitCalories(List<Exercise> selectedExercises, double weightTaken, double calorieLimit, double bmi) {
        List<Result> finalPlan = new ArrayList<>();
        double totalCalories = 0;

        for (Exercise exercise : selectedExercises) {
            double caloriesForOneRep = (exercise.weightFactor > 1)
                    ? calculateCalories(exercise.getMetScore(), weightTaken, exercise.weightFactor, 2, bmi)
                    : exercise.getMetScore() * 2;

            double totalCaloriesForTwoReps = 2 * caloriesForOneRep; // First two reps
            double caloriesForThirdRep = calculateCalories(exercise.getMetScore(), weightTaken + 0.5, exercise.weightFactor, 3, bmi); // Increased weight for third rep

            // Check if adding three reps exceeds calorie limit
            if (totalCalories + totalCaloriesForTwoReps + caloriesForThirdRep <= calorieLimit) {
                totalCalories += totalCaloriesForTwoReps + caloriesForThirdRep;
                finalPlan.add(new Result(exercise, weightTaken)); // Add all three reps
                finalPlan.add(new Result(exercise, weightTaken));
                finalPlan.add(new Result(exercise, weightTaken + 0.5));
            } else if (totalCalories + totalCaloriesForTwoReps <= calorieLimit) {
                totalCalories += totalCaloriesForTwoReps;
                finalPlan.add(new Result(exercise, weightTaken)); // Add only two reps
                finalPlan.add(new Result(exercise, weightTaken));
            } else if (totalCalories + caloriesForOneRep <= calorieLimit) {
                totalCalories += caloriesForOneRep;
                finalPlan.add(new Result(exercise, weightTaken)); // Add only one rep
            }
            // If even one rep exceeds the calorie limit, skip the exercise
        }
        return finalPlan;
    }

    protected double calculateCalories(double metScore, double weightTaken, double weightFactor, int durationInMinutes, double bmi) {
        // Simple MET-based calorie calculation
        double adjustedMetScore = adjustMetScoreBasedOnBmi(metScore, bmi);

        return adjustedMetScore * weightTaken * weightFactor * durationInMinutes; // 3 minutes as an example duration
    }

    private double adjustMetScoreBasedOnBmi(double metScore, double bmi) {
        // Example adjustment logic (customize as needed)
        if (bmi >= 30) { // Considered obese
            return metScore * 0.8; // Reduce intensity for high BMI
        }
        return metScore; // No adjustment for normal BMI
    }

    protected List<Exercise> selectRoundRobinExercises(Map<String, List<Exercise>> groupedExercises) {
        // Round robin implementation
        List<Exercise> selectedExercises = new ArrayList<>();
        boolean exercisesRemaining = true;

        while (exercisesRemaining){
            exercisesRemaining = false;
            for (List<Exercise> group : groupedExercises.values()) {
                if(!group.isEmpty()){
                    // Sort by rating, descending
                    group.sort(Comparator.comparingDouble(Exercise::getRating).reversed());

                    // Select the highest-rated exercise, but occasionally choose a random one
                    Exercise selected = random.nextInt(10) < 4 && group.size() > 1 ? group.get(random.nextInt(group.size())) : group.get(0);
                    selectedExercises.add(selected);

                    group.remove(selected);
                    if(!group.isEmpty())
                        exercisesRemaining = true;
                }
            }

        }
        return selectedExercises;
    }

    public Map<String, List<Exercise>> groupExercises(List<Exercise> filteredExercises) {
        Map<String, List<Exercise>> groupedExercises = new HashMap<>();
        for (Exercise exercise : filteredExercises) {
            String key = exercise.getMuscleId() + "-" + exercise.getEquipmentID();
            groupedExercises.computeIfAbsent(key, k -> new ArrayList<>()).add(exercise);
        }
        return groupedExercises;
    }

    protected List<Exercise> filterExercises(List<String> muscles, List<String> equipments, DifficultyLevel difficultyLevel) {
        List<Exercise> filteredExercise = fitnessDao.getSortedFilteredExercise(equipments, muscles, difficultyLevel);

        return filteredExercise;
    }

    @Override
    protected void onPostExecute(List<List<Result>> result) {
        super.onPostExecute(result);
        if (callbackListener != null) {
            callbackListener.onCallback(result);
        }
        // This method runs on the UI thread
        // Handle the result here (update UI or pass the data to other components)
    }

    double calculateBMI(double weight, double height) {
        return weight / (height * height);
    }
}
