package com.fitness.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class PersonalDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_details);
        Spinner genderSpinner=findViewById(R.id.genderspinner);
        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this,R.array.gender_array,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapter);

        EditText editTextAge=findViewById(R.id.editTextNumber);
        EditText editTextHeight=findViewById(R.id.editTextHeight);
        EditText editTextWeight=findViewById(R.id.editTextWeight);
        EditText editTextDreamWeight=findViewById(R.id.dreamweight);

        Button nextButton=findViewById(R.id.NextButton);
        Spinner spinnerWeightUnits=findViewById(R.id.spinnerWeightunits);
        Spinner spinnerHeightUnits=findViewById(R.id.spinnerHeightunits);
        Spinner spinnerdreamweightunits=findViewById(R.id.dreamweightspinnerunits);

        Button backbutton=findViewById(R.id.back_button);
        Bundle extras=getIntent().getExtras();



        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(PersonalDetails.this,Create_Account.class);
                startActivity(intent);
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateForm(genderSpinner,editTextAge,editTextHeight,editTextWeight,editTextDreamWeight)){
                    Intent intent=new Intent(PersonalDetails.this,WorkoutPreference.class);
                    if(extras!=null){
                        String fullName=extras.getString("fullname");
                        String email=extras.getString("email");
                        String password=extras.getString("password");
                        intent.putExtra("fullname", fullName);
                        intent.putExtra("email", email);
                        intent.putExtra("password", password);
                        String gender = genderSpinner.getSelectedItem().toString();
                        String age=editTextAge.getText().toString();
                        double weightInkg = convertWeightToKg(editTextWeight.getText().toString(), spinnerWeightUnits.getSelectedItem().toString());
                        double heightinCM = convertHeightTocm(editTextHeight.getText().toString(), spinnerHeightUnits.getSelectedItem().toString());
                        double dreamweightinkg=convertWeightToKg(editTextDreamWeight.getText().toString(),spinnerdreamweightunits.getSelectedItem().toString());
                        double bmi=calculateBMI(heightinCM,weightInkg);
                        intent.putExtra("gender",gender);
                        intent.putExtra("age",age);
                        intent.putExtra("height",heightinCM);
                        intent.putExtra("weight", weightInkg);
                        intent.putExtra("BMI",bmi);
                        intent.putExtra("DreamWeight",dreamweightinkg);
                        startActivity(intent);
                    }

                }
            }
        });

    }
    private double convertWeightToKg(String weightStr, String unit) {
        double weight = Double.parseDouble(weightStr);
        if (unit.equals("pounds")) {
            return weight * 0.453592;
        }
        return weight;
    }


    private double convertHeightTocm(String heightStr, String unit) {
        final double INCHES_PER_FOOT = 12;
        final double CM_PER_INCH = 2.54;

        if (unit.equals("cm")) {
            return Double.parseDouble(heightStr);
        } else if (unit.equals("feet")) {
            try {
                String[] parts = heightStr.trim().split("\\s+");
                if (parts.length == 2) {
                    double feet = Double.parseDouble(parts[0]);
                    double inches = Double.parseDouble(parts[1]);
                    double totalInches = (feet * INCHES_PER_FOOT) + inches;
                    return totalInches * CM_PER_INCH;
                } else {
                    double feet = Double.parseDouble(heightStr);
                    return feet * INCHES_PER_FOOT * CM_PER_INCH;
                }
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }



    private double calculateBMI(double heightInCm, double weightInKg){
        double heightInMeters = heightInCm / 100.0;
        return weightInKg / (heightInMeters * heightInMeters);
    }


    private boolean validateForm(Spinner genderSpinner,EditText age,EditText height,EditText weight,EditText dreamweight){
        boolean valid=true;
        if(genderSpinner.getSelectedItem().toString().equals("Gender")){
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
        if((TextUtils.isEmpty(weight.getText().toString()) || Double.parseDouble(weight.getText().toString())==0)){
            weight.setError("Please enter a valid weight");
            valid=false;
        }
        if((TextUtils.isEmpty(dreamweight.getText().toString()) || Double.parseDouble(dreamweight.getText().toString())==0)){
            dreamweight.setError("Please enter a valid dream weight");
           valid=false;
        }
        return valid;
    }
}