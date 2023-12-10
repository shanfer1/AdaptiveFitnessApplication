package com.fitness.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fitness.database.ExerciseDatabase;
import com.fitness.myapplication.R;

import com.fitness.myapplication.activities.WorkoutTrackerActivity;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Home extends AppCompatActivity implements CallbackListener{
    private DrawerLayout drawerLayout;
    private String[] cols=new String[]{"email","fullname","age","height","weight","activity_level","goal","location_preference","workout_preferences","intensity_level","duration","BMI","dream_weight","gender","equipmentList"};
    private ProgressBar caloriesProgressBar;
    private TextView caloriesTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        HashMap<String,String> map=getUserDetails();

        TextView fullnamedit=findViewById(R.id.fullnameViewHome);
            fullnamedit.setText( map.get("fullname"));
            Button scheduleworkout=findViewById(R.id.getWorkout);
            float ageinfloat=0;
            float bmiinfloat=0;
            float durationinfloat=0;
        float intensityinfloat=0;
        float weightinfloat=0;
        float intensityduration=0;
        float dreamweighinfloat=0;
        int featuresset=0;
        String gender="gender";
        String equipment="";
        float heightinfloat=0;


        if(map.containsKey("age")){
            featuresset++;
            ageinfloat = Float.parseFloat(map.get("age"));
        }

        if(map.containsKey("height")){
            heightinfloat = Float.parseFloat(map.get("height"));
        }
        if(map.containsKey("BMI")){
            featuresset++;

            bmiinfloat = Float.parseFloat(map.get("BMI"));
        }
        if(map.containsKey("duration")){
            featuresset++;

            durationinfloat = Float.parseFloat(map.get("duration"));
        }
        if(map.containsKey("intensity_level")){
            featuresset++;

            intensityinfloat = Float.parseFloat(map.get("intensity_level"));
        }
        if(intensityinfloat!=0 && durationinfloat!=0){
            featuresset++;

            intensityduration=intensityinfloat*durationinfloat;

        }

        if(map.containsKey("weight")){
            featuresset++;

            weightinfloat = Float.parseFloat(map.get("weight"));
        }
        if(map.containsKey("dream_weight")){
            featuresset++;

            dreamweighinfloat = Float.parseFloat(map.get("dream_weight"));
        }
        if(map.containsKey("equipmentList")){
            featuresset++;

            equipment = map.get("equipmentList");

        }
        float[] genderencoded=new float[2];
        if(map.containsKey("gender")){
            featuresset++;
            gender = map.get("gender");
            if(gender.equals("Male")){
                genderencoded[0]=0.0f;
                genderencoded[1]=1.f;

            }else if(gender.equals("Female")){
                genderencoded[0]=1.0f;
                genderencoded[1]=0.0f;
            }else {
                genderencoded[0]=0.0f;
                genderencoded[1]=0.0f;
            }
        }



        float heartRateinfloat = 100.0f;
        TFLiteModelPredictor predictor = new TFLiteModelPredictor(this);

        float predictedCalories=400;

        try {
//                        float predictedCalories=0;
            if(featuresset>=7){
                predictedCalories = predictor.predict(ageinfloat, weightinfloat, durationinfloat, dreamweighinfloat, bmiinfloat, intensityduration,heartRateinfloat,intensityinfloat,genderencoded);
            }else{
                Toast.makeText(Home.this, "Not enough User Details to predict the Calories  Burn. Going with Default", Toast.LENGTH_SHORT).show();

            }
            Log.d("WorkoutPreference", "Predicted Calories: " + predictedCalories);
        } catch (Exception e) {
            Log.e("WorkoutPreference", "Error during prediction", e);
        }

        caloriesProgressBar = findViewById(R.id.caloriesProgressBar);
        caloriesTextView = findViewById(R.id.caloriesTextView);
        storeCaloriesToDB(predictedCalories);
//       updateCalories(predictedCalories*5,predictedCalories*5);
//        float height = 171;
        float weightTaken = 15;

        String[] itemsArray = equipment.split(", ");
        List<String> equipmentList = new ArrayList<>(Arrays.asList(itemsArray));
        String currentUserEmailid=getCurrentUserEmailid();
        DatabaseHelper databaseHelper=DatabaseHelperFactory.getInstance(this);

        float previousWeekTotalCaloriesBurned=databaseHelper.getLatestCalories(currentUserEmailid);
        float caloriestoBurnPredicted=databaseHelper.getCaloriesToBurn(currentUserEmailid);
        int workoutnumber=databaseHelper.getUserWorkoutNumber(getCurrentUserEmailid());
        if(workoutnumber==1){
            updateCalories(0,caloriestoBurnPredicted*5);

        }else{
            updateCalories(previousWeekTotalCaloriesBurned,caloriestoBurnPredicted*5);

        }
//        if(previousWeekTotalCaloriesBurned!=0){
//            caloriestoBurnPredicted=PersonaliseCalories(previousWeekTotalCaloriesBurned,caloriestoBurnPredicted);
//            databaseHelper.updateCaloriesToBurn(currentUserEmailid,caloriestoBurnPredicted);
//        }

        float finalPredictedCalories = predictedCalories;
        float finalWeightinfloat = weightinfloat;
        float finalIntensityinfloat = intensityinfloat;
        float finalHeightinfloat = heightinfloat;
        scheduleworkout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view){
                DatabaseHelper databaseHelper=DatabaseHelperFactory.getInstance(Home.this);
                int workoutnumber=databaseHelper.getUserWorkoutNumber(getCurrentUserEmailid());
                if(workoutnumber==1){
                    scheduleWorkout(equipmentList, finalPredictedCalories, finalWeightinfloat, finalHeightinfloat /100, weightTaken, finalIntensityinfloat);

                }else{

                    Intent intent = new Intent(Home.this, WorkoutTrackerActivity.class);
                    startActivity(intent);
                }

//                scheduleWorkout(equipmentList, finalPredictedCalories, finalWeightinfloat, height/100, weightTaken, finalIntensityinfloat);
            }
        });

//        scheduleWorkout(equipmentList, predictedCalories, weightinfloat, height/100, weightTaken, intensityinfloat);



        drawerLayout = findViewById(R.id.drawer_layout);
        ImageView avatarImageView = findViewById(R.id.avatarImageView);
        NavigationView navigationView = findViewById(R.id.nav_view);

        avatarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the navigation drawer
                drawerLayout.openDrawer(GravityCompat.END);
            }
        });

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        int id = item.getItemId();

                        if (id == R.id.nav_home) {
                            drawerLayout.closeDrawer(GravityCompat.END);
                            return true;
                        } else if (id == R.id.nav_profile) {
                            navigateToProfile();
                            drawerLayout.closeDrawer(GravityCompat.END);
                            return true;
                        }
                        else if (id == R.id.nav_signout) {
                            logout();
                            drawerLayout.closeDrawer(GravityCompat.END);
                            return true;
                        }
                        else if (id == R.id.nav_gyms) {
                            nearbygmys();
                            drawerLayout.closeDrawer(GravityCompat.END);
                            return true;
                        }
                        else if (id == R.id.weather_rec) {
                            weatherRecommendations();
                            drawerLayout.closeDrawer(GravityCompat.END);
                            return true;
                        }

                        return false;
                    }
                });



    }

    private void storeCaloriesToDB(float caloriesToBurn){
        DatabaseHelper databaseHelper=DatabaseHelperFactory.getInstance(this);
        String emailid=getCurrentUserEmailid();
        databaseHelper.updateCaloriesToBurn(emailid,caloriesToBurn);

    }
    private String getCurrentUserEmailid(){
        SharedPreferences sharedPreferences=getSharedPreferences("MySharedPrefs",MODE_PRIVATE);
        String emailid=sharedPreferences.getString("LoggedInUser",null);
    return emailid;
    }
    public void getNewWorkout(){
        DatabaseHelper databaseHelper=DatabaseHelperFactory.getInstance(this);
        String currentUserEmailid=getCurrentUserEmailid();
        float previousWeekTotalCaloriesBurned=databaseHelper.getLatestCalories(currentUserEmailid);
        float caloriestoBurnPredicted=databaseHelper.getCaloriesToBurn(currentUserEmailid);
        float newCaloriestoBurn=PersonaliseCalories(previousWeekTotalCaloriesBurned,caloriestoBurnPredicted);

        databaseHelper.updateCaloriesToBurn(currentUserEmailid,newCaloriestoBurn);
        updateCalories(newCaloriestoBurn*5,newCaloriestoBurn*5);
        HashMap<String,String> map=getUserDetails();
        float intensityinfloat = Float.parseFloat(map.get("intensity_level"));
        float heightinfloat = Float.parseFloat(map.get("height"));

        String equipment = map.get("equipmentList");
        String[] itemsArray = equipment.split(", ");
        List<String> equipmentList = new ArrayList<>(Arrays.asList(itemsArray));
        float weightinfloat = Float.parseFloat(map.get("weight"));

        scheduleWorkout(equipmentList, newCaloriestoBurn, weightinfloat, heightinfloat/100, 15, intensityinfloat);

    }


    private HashMap getUserDetails(){
        DatabaseHelper dbHelper= DatabaseHelperFactory.getInstance(this);
        HashMap<String,String> map=new HashMap<>();
        String emailid= getCurrentUserEmailid();
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
        return map;
    }
    private void scheduleWorkout(List<String> equipmentList, float calories, float weight, float height, float weightTaken, float intensity) {
        // Placeholder for equipmentList

        // TODO: Fetch these values -> input --> integration
        int category = Math.round(intensity);

        Log.d("MAIN", "Calorie TO BURN : "+ String.valueOf(calories));
        Log.d("MAIN", "Height : "+ String.valueOf(height));
        Log.d("MAIN", "Weight : "+ String.valueOf(weight));
        Log.d("MAIN", "Avail Equipments :" + equipmentList.toString());
        Log.d("MAIN", "Category :" + category);

        ExerciseScheduler.Params params = new ExerciseScheduler.Params(equipmentList, weight, height,
                calories, weightTaken, category);

        ExerciseDatabase exerciseDatabase = ExerciseDatabase.getInstance(this);
        new ExerciseScheduler(exerciseDatabase.fitnessDao(), this, getCurrentUserEmailid()).execute(params);
        Toast.makeText(this, "Computing workout plan", Toast.LENGTH_LONG).show();
    }

    private void updateCalories(float burnedCalories, float totalCalories) {

        int progress = (int) (((double) burnedCalories / totalCalories) * 100);
        caloriesProgressBar.setProgress(progress);
        String calorieText =  String.format("%.0f / %.0f", burnedCalories, totalCalories);
        caloriesTextView.setText(calorieText);
    }

    private void logout(){
        Intent intent=new Intent(Home.this, Login.class);
        startActivity(intent);
    }

    private float PersonaliseCalories(float previousWeekCalores, float caloriestoburnpredicted ){
        previousWeekCalores=previousWeekCalores/5;
        float extraCalories=previousWeekCalores -caloriestoburnpredicted;
        return Float.parseFloat(String.format("%.2f", caloriestoburnpredicted+extraCalories/2));

    }

    private void nearbygmys(){
        Intent intent=new Intent(Home.this, GymActivity.class);
        startActivity(intent);
    }
    private void navigateToProfile() {
        Intent intent = new Intent(Home.this, Profile.class);
        startActivity(intent);
    }

    private void weatherRecommendations() {
        Intent intent = new Intent(Home.this, WeatherRecommendationActivity.class);
        startActivity(intent);
    }

    @Override
    public void onCallback(List<List<ExerciseScheduler.Result>> result) {
        Toast.makeText(this, "Planned Workout Strategy", Toast.LENGTH_LONG);
        ArrayList<ExerciseScheduler.Result> flattenedList = new ArrayList<>();
        ArrayList<Integer> groupSizes = new ArrayList<>();

        for (List<ExerciseScheduler.Result> group : result) {
            groupSizes.add(group.size());
            flattenedList.addAll(group);
        }

        // Call intent after workout recommendation calculation
        Intent intent = new Intent(this, WorkoutTrackerActivity.class);
//        intent.putParcelableArrayListExtra("FLATTENED_RESULT", flattenedList);
//        intent.putIntegerArrayListExtra("GROUP_SIZES", groupSizes);
        startActivity(intent);
    }
}
