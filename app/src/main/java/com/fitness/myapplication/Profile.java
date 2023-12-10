package com.fitness.myapplication;

import androidx.appcompat.app.AppCompatActivity;
//import androidx.room.Database;
//import androidx.room.SharedSQLiteStatement;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.concurrent.Executors;

public class Profile extends AppCompatActivity {


    private String[] cols=new String[]{"email","fullname","age","height","weight","activity_level","goal","location_preference","workout_preferences","intensity_level","duration","BMI","dream_weight","gender"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        DatabaseHelper dbHelper= DatabaseHelperFactory.getInstance(this);



//        fethcing the user logged in email id form shared preferences
        SharedPreferences sharedPreferences=getSharedPreferences("MySharedPrefs",MODE_PRIVATE);
        String emailid=sharedPreferences.getString("LoggedInUser",null);
        HashMap<String,String> map=new HashMap<>();

        if(emailid!=null){
            Cursor cursor=dbHelper.getUserAndDetailsByEMail(emailid);
            if(cursor!=null && cursor.moveToFirst()){
                do{
                    for(int i=0;i<cols.length;i++){
                        map.put(cols[i],cursor.getString((cursor.getColumnIndexOrThrow(cols[i]))));
                    }
                }while(cursor.moveToNext());
                cursor.close();

            }
        }



        TextInputEditText editTextEmail = findViewById(R.id.editTextEmail);
        TextInputEditText editTextFullname=findViewById(R.id.editTextFulllName);
        TextInputEditText editTextAge = findViewById(R.id.editTextAge);
        TextInputEditText editTextHeight = findViewById(R.id.editTextHeight);
        TextInputEditText editTextWeight = findViewById(R.id.editTextweight);
        TextInputEditText editTextDreamWeight = findViewById(R.id.editTextDreamWeight);
        Button savebutton=findViewById(R.id.profile_savebutton);
        Button backbutton=findViewById(R.id.profile_backbutton);

        Spinner genderSpinner = findViewById(R.id.profile_gender_spinner);
        String genderValue = map.get("gender");

        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);

        Spinner goalSpinner = findViewById(R.id.goalSpinner_profile);
        String goalValue = map.get("goal");

        ArrayAdapter<CharSequence> goalAdapter = ArrayAdapter.createFromResource(this,
                R.array.goal_array, android.R.layout.simple_spinner_item);
        goalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        goalSpinner.setAdapter(goalAdapter);


        Spinner activitySpinner = findViewById(R.id.profile_activity_spinner);
        String activityValue = map.get("activity_level");

        ArrayAdapter<CharSequence> activityAdapter = ArrayAdapter.createFromResource(this,
                R.array.acitivity_array, android.R.layout.simple_spinner_item);
        activityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activitySpinner.setAdapter(activityAdapter);



        Spinner locationSpinner = findViewById(R.id.profile_location_spinnner);
        String locationValue = map.get("location_preference");

        ArrayAdapter<CharSequence> location_adapter = ArrayAdapter.createFromResource(this,
                R.array.location_array, android.R.layout.simple_spinner_item);
        location_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpinner.setAdapter(location_adapter);

        SeekBar durationSeekBar = findViewById(R.id.profile_seek_bar_duration);
        String durationValue = map.get("duration");


        SeekBar intensitySeekBar = findViewById(R.id.profile_seek_bar_intensity);
        String intensityValue = map.get("intensity_level");

        TextView durationvalue=findViewById(R.id.profile_duration_value);
        TextView intesityvalue=findViewById(R.id.profile_intensity_value);
        editTextEmail.setText(emailid);
        editTextFullname.setText(map.get("fullname"));
        editTextAge.setText(map.get("age"));


        editTextHeight.setText(map.get("height") + " cm");

        editTextWeight.setText(map.get("weight") + " kg");
        editTextDreamWeight.setText(map.get("dream_weight")+ " kg");


        // Find the position of the genderValue in the spinner
        if (genderValue != null) {
            int spinnerPosition = genderAdapter.getPosition(genderValue);
            genderSpinner.setSelection(spinnerPosition);
        }

        // Find the position of the goalValue in the spinner
        if (goalValue != null) {
            int spinnerPosition = goalAdapter.getPosition(goalValue);
            goalSpinner.setSelection(spinnerPosition);
        }
        // Find the position of the activityValue in the spinner
        if (activityValue != null) {
            int spinnerPosition = activityAdapter.getPosition(activityValue);
            activitySpinner.setSelection(spinnerPosition);
        }


        // Find the position of the location_value in the spinner
        if (locationValue != null) {
            int spinnerPosition = location_adapter.getPosition(locationValue);
            locationSpinner.setSelection(spinnerPosition);
        }

        if (durationValue != null && !durationValue.isEmpty()) {
            int duration = Integer.parseInt(durationValue);
            durationSeekBar.setProgress(duration);
        }

        if (intensityValue != null && !intensityValue.isEmpty()) {
            int intensity = Integer.parseInt(intensityValue);
            intensitySeekBar.setProgress(intensity);
        }



        durationvalue.setText(durationSeekBar.getProgress() + " minutes");
        intesityvalue.setText(intensitySeekBar.getProgress()+"/10");

        durationSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                durationvalue.setText(progress + " minutes");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // You can add any required behavior when the user starts dragging the seek bar.
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // You can add any required behavior when the user stops dragging the seek bar.
            }
        });


        intensitySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                intesityvalue.setText(progress + "/10");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // You can add any required behavior when the user starts dragging the seek bar.
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // You can add any required behavior when the user stops dragging the seek bar.
            }
        });



        savebutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                    if (formValidation(editTextFullname, editTextAge, editTextHeight, editTextWeight, editTextDreamWeight,genderSpinner,activitySpinner,goalSpinner,locationSpinner)) {
                        String fullname= editTextFullname.getText().toString().trim();
                        String age=editTextAge.getText().toString().trim();
                        String textHeight=editTextHeight.getText().toString().trim();
                        String height= String.valueOf(convertFeetInchesToFeet(textHeight));
                        String weight=editTextWeight.getText().toString().trim().replaceAll("kg","").trim();
                        String dreamweight=editTextDreamWeight.getText().toString().trim().replaceAll("kg","").trim();
                        String gender= genderSpinner.getSelectedItem().toString();
                        String activity=activitySpinner.getSelectedItem().toString();
                        String goal=goalSpinner.getSelectedItem().toString();
                        String location=locationSpinner.getSelectedItem().toString();
                        String duration=durationvalue.getText().toString().split(" ")[0];
                        String intensity=intesityvalue.getText().toString().split("/")[0];
                        Double bmi= calculateBMI(Double.parseDouble(height),Double.parseDouble(weight));
                        Executors.newSingleThreadExecutor().execute(new Runnable() {
                            @Override
                            public void run() {
                                DatabaseHelper dbHelper=DatabaseHelper.getInstance(Profile.this);
                                if(dbHelper.UpdatePersonalDetails(fullname,emailid,age,height,weight,activity,goal,location,duration,intensity,dreamweight,bmi,gender)){
//                                    Toast.makeText(Profile.this,"Data Updated Successfully",Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Profile.this, Home.class);
                                    startActivity(intent);
                                }else{
//                                    Toast.makeText(Profile.this,"Internal Server error. Contact Administrator",Toast.LENGTH_SHORT).show();
                                }
                                // Perform database operations here
                                // If you need to update UI based on this, make sure to run that part on the main thread
                            }
                        });
//
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                                        }
//                        }).start();



                    }
                }
        });
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(Profile.this,Home.class);
                startActivity(intent);
            }
        });
        };
    private double calculateBMI(Double heightInFeet,Double weightInPounds){
        double heightinMeters=heightInFeet*0.3048;
        double weightinKg=weightInPounds*0.453592;
        return weightinKg/(heightinMeters*heightinMeters);
    }

    private boolean formValidation(TextInputEditText fullName,TextInputEditText age,TextInputEditText height,TextInputEditText weight,TextInputEditText dreamweight,Spinner gender,Spinner activity,Spinner goal,Spinner location){
        boolean valid=true;
        if(fullName.getText().toString().trim().isEmpty() || fullName.getText().toString().trim().length()<6){
            fullName.setError("Full Name must be at least 6 characters");
            valid=false;
        }
        if(gender.getSelectedItem().toString().equals("Gender")){
            Toast.makeText(this, "Please select a valid gender.", Toast.LENGTH_SHORT).show();
            valid=false;
        }
        if(TextUtils.isEmpty(age.getText().toString()) || Integer.parseInt(age.getText().toString())==0){
            age.setError("Please enter a valid age");
            valid=false;
        }
        if((TextUtils.isEmpty(height.getText().toString()) || height.getText().toString().isEmpty())){
            height.setError("Please enter a valid height");
            valid=false;
        }
        if((TextUtils.isEmpty(weight.getText().toString()) || Double.parseDouble(weight.getText().toString().replaceAll("kg","").trim())==0)){
            weight.setError("Please enter a valid weight");
            valid=false;
        }
        if((TextUtils.isEmpty(dreamweight.getText().toString()) || Double.parseDouble(dreamweight.getText().toString().replaceAll("kg","").trim())==0)){
            dreamweight.setError("Please enter a valid dream weight");
            valid=false;
        }
        if (activity.getSelectedItem().toString().equals("Choose Your Daily Activity Level")) {
            Toast.makeText(this, "Please select a valid activity level.", Toast.LENGTH_SHORT).show();
            valid=false;
        }
        if (goal.getSelectedItem().toString().equals("Select Goal")) {
            Toast.makeText(this, "Please select a valid goal.", Toast.LENGTH_SHORT).show();
            valid=false;
        }
        if (location.getSelectedItem().toString().equals("Choose location Preference")) {
            Toast.makeText(this, "Please select a valid location preference.", Toast.LENGTH_SHORT).show();
            valid=false;
        }
        return valid;


    }

    private String convertFeetToFeetInches(double heightInFeet) {
        int feet = (int) heightInFeet;
        int inches = (int) ((heightInFeet - feet) * 12);
        return feet + " " + inches;
    }

    private double convertFeetInchesToFeet(String heightFeetInches) {
        String[] parts = heightFeetInches.split(" ");
        if (parts.length == 2) {
            return Double.parseDouble(parts[0]);
        }
        return 0; // Default return value or handle error
    }

    }
