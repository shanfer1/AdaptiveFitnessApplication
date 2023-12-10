package com.fitness.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fitness.adapters.CheckableSpinnerAdapter;

import java.util.Arrays;
import java.util.List;

public class WorkoutPreference extends AppCompatActivity {


    private TextView durationvalue,intesityvalue;
    private SeekBar seekbarDuraiton,seekbarIntensity;
    private Spinner spinner;
    private CheckableSpinnerAdapter adapter;
    private static final String [] equipments= {"Cable", "Bands", "Barbell", "Dumbbell",
            "Medicine Ball", "Exercise Ball", "E-Z Curl Bar", "Kettlebells", "Machine"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_workout_preference);
        LinearLayout checkboxContainer = findViewById(R.id.checkboxContainer);
        Spinner activitySpinner=findViewById(R.id.acitivityspinner);
        Spinner goalSpinner=findViewById(R.id.goalspinner);
        Spinner locationSpinner=findViewById(R.id.locationspinner);
        Button saveButton=findViewById(R.id.savebutton);
        Button backButton=findViewById(R.id.backbutton);
        Spinner equipmentspinner = findViewById(R.id.checkable_spinner);

        durationvalue=findViewById(R.id.durationValue);
        intesityvalue=findViewById(R.id.intensityValue);
        seekbarDuraiton=findViewById(R.id.seekBarduration);
        seekbarIntensity=findViewById(R.id.seekBarintensity);
        configureEquipmentSpinner();

        String[] workoutPreferences = getResources().getStringArray(R.array.workout_preferences);
        for (String preference : workoutPreferences) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(preference);
            checkboxContainer.addView(checkBox);
        }


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(WorkoutPreference.this,PersonalDetails.class);
                startActivity(intent);
            }
        });



        durationvalue.setText(seekbarDuraiton.getProgress()+" minutes");
        intesityvalue.setText(seekbarIntensity.getProgress()+"/10");

        // SeekBar Change Listeners
        seekbarDuraiton.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                durationvalue.setText(progress + " minutes");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // You can leave this empty if you don't need to handle this event
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // You can leave this empty if you don't need to handle this event
            }
            // Implement other methods if needed
        });

        seekbarIntensity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                intesityvalue.setText(progress + "/10");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // You can leave this empty if you don't need to handle this event
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // You can leave this empty if you don't need to handle this event
            }
            // Implement other methods if needed
        });



        // Set onClickListener for the save button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform form validation
                if (activitySpinner.getSelectedItem().toString().equals("Choose Your Daily Activity Level")) {
                    Toast.makeText(WorkoutPreference.this, "Please select a valid activity level.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (goalSpinner.getSelectedItem().toString().equals("Select Goal")) {
                    Toast.makeText(WorkoutPreference.this, "Please select a valid goal.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (locationSpinner.getSelectedItem().toString().equals("Choose location Preference")) {
                    Toast.makeText(WorkoutPreference.this, "Please select a valid location preference.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Bundle extras = getIntent().getExtras();
                if (extras != null) {
                    String fullname = extras.getString("fullname");
                    String email = extras.getString("email");
                    String password = extras.getString("password");
                    String gender = extras.getString("gender");
                    String age = extras.getString("age");
                    double height = extras.getDouble("height");
                    double weight = extras.getDouble("weight");
                    double dreamweight=extras.getDouble("DreamWeight");
                    double bmi=extras.getDouble("BMI");

                    String activityLevel = activitySpinner.getSelectedItem().toString();
                    String goal = goalSpinner.getSelectedItem().toString();
                    String locationPreference = locationSpinner.getSelectedItem().toString();
                    StringBuilder workoutPrefBuilder = new StringBuilder();
                    int childCount = checkboxContainer.getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        CheckBox checkBox = (CheckBox) checkboxContainer.getChildAt(i);
                        if (checkBox.isChecked()) {
                            workoutPrefBuilder.append(checkBox.getText().toString()).append(";");
                        }
                    }
                    String duration=durationvalue.getText().toString().split(" ")[0];
                    String intensity=intesityvalue.getText().toString().split("/")[0];

                    List<String> equipmentList = adapter.getCheckedItems();
                    //TODO: Add this into DB --> integration

                    String workoutPreferences = workoutPrefBuilder.toString();
//                    AppDatabase db=AppDatabase.getDatabase(WorkoutPreference.this);
//                    User user=new User();
//                    user.email=email;
//                    user.fullname=fullname;
//                    user.password=password;
//                    UserDetails userdetails=new UserDetails();
//                    userdetails.email=email;
//                    userdetails.duration=duration;
//                    userdetails.workout_preferences=workoutPreferences;
//                    userdetails.intensity_level=intensity;
//                    userdetails.height=height;
//                    userdetails.weight=weight;
//                    userdetails.goal=goal;
//                    userdetails.location_preferences=locationPreference;
//                    userdetails.dream_weight=dreamweight;
//                    userdetails.bmi=bmi;
//                    userdetails.age=age;
//                    db.userDao().insertUser(user);
//                    db.userDao().insertUserDetails(userdetails);
                    // Database insert operations
//                    new AsyncTask<Void, Void, Void>() {
//                        @Override
//                        protected Void doInBackground(Void... voids) {
//                            AppDatabase db = AppDatabase.getDatabase(WorkoutPreference.this);
//                            db.userDao().insertUser(user);
//                            db.userDao().insertUserDetails(userdetails);
//                            return null;
//                        }
//
//                        @Override
//                        protected void onPostExecute(Void aVoid) {
//                            super.onPostExecute(aVoid);
//                            // Notify the user that data has been saved
//                            Toast.makeText(WorkoutPreference.this, "Data saved successfully", Toast.LENGTH_SHORT).show();
//
//                            // Rest of your logic, like prediction and starting new activity
//                            // ...
//
//                            Intent intent = new Intent(WorkoutPreference.this, Home.class);
//                            intent.putExtra("emailid", email);
//                            startActivity(intent);
//                        }
//                    }.execute();


                    DatabaseHelper dbHelper = DatabaseHelper.getInstance(WorkoutPreference.this);


                    dbHelper.insertData(fullname, email,password, age, height, weight, activityLevel, goal, locationPreference, workoutPreferences,duration,intensity,dreamweight,bmi,gender);
                    dbHelper.insertSelectedItems(email,equipmentList);
                    Toast.makeText(WorkoutPreference.this, "Data saved successfully", Toast.LENGTH_SHORT).show();


                    logCurrentUser(email);
                    Intent intent=new Intent(WorkoutPreference.this,Home.class);
                    intent.putExtra("emailid",email);
                    startActivity(intent);

                }

            }
        });
    }
    private void logCurrentUser(String emailID){
        SharedPreferences sharedPreferences=getSharedPreferences("MySharedPrefs",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("LoggedInUser",emailID);
        editor.apply();
    }

    private void configureEquipmentSpinner() {
        spinner = findViewById(R.id.checkable_spinner);
        List<String> spinnerItems = Arrays.asList(equipments); // Your items
        adapter = new CheckableSpinnerAdapter(this, R.layout.checkable_spinner_item, spinnerItems);
        spinner.setAdapter(adapter);
    }
}