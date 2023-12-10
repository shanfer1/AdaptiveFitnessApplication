package com.fitness.myapplication.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fitness.database.ExerciseDatabase;
import com.fitness.database.dao.FitnessDao;
import com.fitness.myapplication.ActivityStatsAPI;
import com.fitness.myapplication.CaloriesBurntPredictor;
import com.fitness.myapplication.DatabaseHelper;
import com.fitness.myapplication.DatabaseHelperFactory;
import com.fitness.myapplication.DisplayExercise;
import com.fitness.myapplication.ExerciseScheduler;
import com.fitness.myapplication.Home;
import com.fitness.myapplication.R;
import com.fitness.myapplication.WorkoutPreference;

import java.util.ArrayList;
import java.util.List;

public class WorkoutTrackerActivity extends AppCompatActivity {

    private float workoutDuration;
    private long startTime, endTime, startTimeinSeconds, endTimeinSeconds;
    private List<Pair<Integer, Integer>> workoutTimeStamps;
    private int workoutNumber; // Expected to go up to 5 and to be used to index workoutNames
    private String[] workoutNames;

    ArrayList<String[]> dailySchedule = new ArrayList<>();
    private String userEmailId, gender;

    private float heartRate, temperature, age, height, weight;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_tracker);

//        List<ExerciseScheduler.Result> flattenedList = getIntent().getParcelableArrayListExtra("FLATTENED_RESULT");
//        ArrayList<Integer> groupSizes = getIntent().getIntegerArrayListExtra("GROUP_SIZES");
//
//        List<List<ExerciseScheduler.Result>> reconstructedResult = DisplayExercise.reconstructNestedList(flattenedList, groupSizes);
////        int i = 1;
////        for(List<ExerciseScheduler.Result> resultList: reconstructedResult) {
////            Log.i("DISPLAY", "DAY " + i++ + "  exercises");
////            for (ExerciseScheduler.Result r : resultList) {
////                Log.i("DISPLAY", r.getExercise().name + " with weight " + r.getWeightTaken());
////            }
////        }
//
//        for(List<ExerciseScheduler.Result> dayResults : reconstructedResult) {
//            String[] dayExercises = new String[dayResults.size()];
//            int i = 0;
//            for (ExerciseScheduler.Result r : dayResults) {
//                dayExercises[i++] = r.getExercise().name + " with weight " + r.getWeightTaken();
//            }
//            dailySchedule.add(dayExercises);
//        }

//        TextView textViewWorkoutName = findViewById(R.id.textViewWorkoutName);
//        if (!dailySchedule.isEmpty()) {
//            String[] firstDayExercises = dailySchedule.get(0); // 0 for the first day
//            String workoutText = TextUtils.join("\n", firstDayExercises);
//            textViewWorkoutName.setText(workoutText);
//        }

        // Buttons
        Button buttonStartWorkout = findViewById(R.id.buttonStart);
        Button buttonStopWorkout = findViewById(R.id.buttonStop);
        Button buttonSubmit = findViewById(R.id.buttonSubmit);

        TextView textViewWorkoutName = findViewById(R.id.textViewWorkoutName);
        TextView textViewWorkoutDuration = findViewById(R.id.textViewWorkoutDuration);

        dbHelper = new DatabaseHelper(this);
        workoutDuration = 0;

        buttonStartWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTime = System.currentTimeMillis();
                startTimeinSeconds = startTime / 1000;
                startTimeinSeconds = (startTimeinSeconds + 1) / 2 * 2;
                textViewWorkoutDuration.setText("Workout in progress...");
            }
        });

        buttonStopWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (startTime != 0) {
                    endTime = System.currentTimeMillis();
                    endTimeinSeconds = endTime / 1000;
                    endTimeinSeconds = (endTimeinSeconds + 1) / 2 * 2;
                    workoutDuration = (float) ((endTimeinSeconds - startTimeinSeconds));
//                    workoutDuration = (float) ((endTime - startTime) / 1000 / 60.0); // Duration in min
//                    workoutTimeStamps = new long[]{startTime, endTime};
//                    workoutTimeStamps.add(new Pair<>((int)startTime, (int)endTime));
                    workoutTimeStamps = new ArrayList<>();
                    workoutTimeStamps.add(new Pair<>((int)startTimeinSeconds, (int)endTimeinSeconds));
                    textViewWorkoutDuration.setText(workoutDuration / 60 + "min");
                    startTime = 0;
                }
                else {
                    textViewWorkoutDuration.setText("Please start workout first!");
                }
            }
        });

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonStartWorkout.setEnabled(false);
                buttonStopWorkout.setEnabled(false);
                buttonSubmit.setEnabled(false);

                if (workoutDuration != 0) {
                    Toast.makeText(WorkoutTrackerActivity.this, "Please wait...", Toast.LENGTH_SHORT).show();
                    // Invoke method to calculate heart rate and temperature during workout
                   // TODO: Using dummy value for now. Update after testing - Soorya
                    heartRate = ActivityStatsAPI.getAvgHeartRateValues(getApplicationContext(), workoutTimeStamps).get(0);
                    temperature = ActivityStatsAPI.getAvgBodyTemperatures(getApplicationContext(), workoutTimeStamps).get(0);
//                    heartRate = 72.5f;
//                    temperature = 40.8F;

                    // Dummy user profile values
//                    String gender = "male"; // "male" or "female"
//                    float age = 68;
//                    float height = 190.0F;
//                    float weight = 94.0F;

                    CaloriesBurntPredictor tfLiteModel = new CaloriesBurntPredictor(getApplicationContext());

                    float predictedCaloriesBurnt = tfLiteModel.predictCalories(gender, age, height, weight,
                            workoutDuration / 60, heartRate, temperature);

                    // Update calories burnt in DB
//                    String weekStartDate = getWeekStartDate();
//                    dbHelper.getTotalCalories(userId, weekStartDate, predictedCaloriesBurnt);
                    if(workoutNumber == 1) {
                        dbHelper.insertNewRecord(userEmailId, predictedCaloriesBurnt);
                    }
                    else {
                        dbHelper.updateTotalCalories(userEmailId, predictedCaloriesBurnt);
                    }
                    tfLiteModel.close();
                }
                else {
                    textViewWorkoutDuration.setText("No workout completed to submit!");
                }
                startTime = 0 ;
                workoutDuration = 0;
                // get next workout
                setWorkout(userEmailId, textViewWorkoutName, false);
                buttonStartWorkout.setEnabled(true);
                buttonStopWorkout.setEnabled(true);
                buttonSubmit.setEnabled(true);
                textViewWorkoutDuration.setText("0");
            }
        });
        // Get user profile
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPrefs",MODE_PRIVATE);
        userEmailId = sharedPreferences.getString("LoggedInUser",null);

        DatabaseHelper dbHelper= DatabaseHelperFactory.getInstance(this);
        Cursor cursor = dbHelper.getUserAndDetailsByEMail(userEmailId);
        if(cursor!=null && cursor.moveToFirst()) {
            gender = cursor.getString(cursor.getColumnIndexOrThrow("gender"));
            String ageStr = cursor.getString(cursor.getColumnIndexOrThrow("age"));
            String heightStr = cursor.getString(cursor.getColumnIndexOrThrow("height"));
            String weightStr = cursor.getString(cursor.getColumnIndexOrThrow("weight"));
            age = ageStr.isEmpty() ? 0 : Float.parseFloat(ageStr);
            height = heightStr.isEmpty() ? 0 : Float.parseFloat(heightStr);
            weight = weightStr.isEmpty() ? 0 : Float.parseFloat(weightStr);
            cursor.close();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                ExerciseDatabase exerciseDatabase = ExerciseDatabase.getInstance(WorkoutTrackerActivity.this);

                List<List<ExerciseScheduler.Result>> workoutSchedule = exerciseDatabase.getNextScheuleForUser(userEmailId);
                List<String[]> tempSchedule = new ArrayList<>();
                for(List<ExerciseScheduler.Result> dayResults : workoutSchedule) {
                    String[] dayExercises = new String[dayResults.size()];
                    int i = 0;
                    for (ExerciseScheduler.Result r : dayResults) {
                        dayExercises[i++] = r.getExercise().name + " with weight " + r.getWeightTaken();
                    }
                    tempSchedule.add(dayExercises);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dailySchedule.clear();
                        dailySchedule.addAll(tempSchedule);
                        setWorkout(userEmailId, textViewWorkoutName, true);
                    }
                });
            }
        }).start();
    }

    private void setWorkout(String userId, TextView textViewWorkoutName, boolean first) {
        // Get latest workout schedule from DB
        workoutNumber = dbHelper.getUserWorkoutNumber(userId);
        if(!first) {
            if (workoutNumber == 5)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Alert");
                builder.setMessage("Workout schedule completed. Fetching new schedule!");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.show();
                workoutNumber = 1;
                deleteScheduleForUser(userId);
//                sendFeedback();

            }
            else {
                workoutNumber += 1;
            }
            dbHelper.updateWorkoutNumber(userId, workoutNumber);
            Intent intent=new Intent(WorkoutTrackerActivity.this, Home.class);
            startActivity(intent);

        }
//        textViewWorkoutName.setText("Sample workout " + workoutNumber);
//        TextView textViewWorkoutName = findViewById(R.id.textViewWorkoutName);
        if (!dailySchedule.isEmpty()) {
            String[] currentSchedule = dailySchedule.get(workoutNumber - 1); // 0 for the first day
            String workoutText = "Day " + workoutNumber + "\n" + TextUtils.join("\n", currentSchedule);
            textViewWorkoutName.setText(workoutText);
        }
    }

//    public void sendFeedback() {
//        // this method gets the week's total calories burnt and sends to recommendation engine
////        float totalWeeklyCalories = dbHelper.getWeeklyCalories(userId, getWeekStartDate());
//        float totalCalories = dbHelper.getLatestCalories(userEmailId);
//    }

    public String[] getNextSchedule() {
        // Get latest workout schedule
        return new String[0];
    }

    public void deleteScheduleForUser(String emailID) {
        // Obtain the ExerciseDatabase instance
        ExerciseDatabase db = ExerciseDatabase.getInstance(WorkoutTrackerActivity.this);
        FitnessDao fitnessDao = db.fitnessDao();
        new Thread(new Runnable() {
            @Override
            public void run() {
                fitnessDao.deleteCompletedScheduleForUser(emailID);
            }
        }).start();
    }
}