package com.fitness.myapplication;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "fitnessApp.db";
    private static final int DATABASE_VERSION = 8;
    private static DatabaseHelper instance = null;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//      Need to add a foreign key to the user_details table (INTEGRATION STEP)
        Log.d("DatabaseHelper", "Creating database tables");
        String CREATE_USERS_TABLE = "CREATE TABLE users (" +
                "email TEXT PRIMARY KEY, " +
                "fullname TEXT, " +
                "password TEXT)";

        String CREATE_USER_DETAILS_TABLE = "CREATE TABLE user_details (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "email TEXT, " +
                "age TEXT, " +
                "height TEXT, " +
                "weight TEXT, " +
                "activity_level TEXT, " +
                "goal TEXT, " +
                "location_preference TEXT, " +
                "equipmentList TEXT, "+
                "workout_preferences TEXT, " +
                "intensity_level TEXT, "+
                "duration TEXT, "+
                "BMI TEXT, "+
                "dream_weight TEXT, "+
                "gender TEXT, "+
                "caloriestoBurn REAL, "+
                "FOREIGN KEY(email) REFERENCES users(email))";

        String CREATE_USER_WORKOUT_TRACKER_TABLE = "CREATE TABLE user_workout_tracker (" +
                "user_id TEXT, " +
                "workout_number INTEGER, " +
                "PRIMARY KEY (user_id), " +
                "FOREIGN KEY(user_id) REFERENCES users(email))";

        String CREATE_WEEKLY_CALORIES_TABLE = "CREATE TABLE weekly_calories (" +
                "user_id TEXT, " +
                "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "calories_burnt REAL, " +
                "PRIMARY KEY (user_id, timestamp), " +
                "FOREIGN KEY(user_id) REFERENCES users(email))";

        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_USER_DETAILS_TABLE);
        db.execSQL(CREATE_USER_WORKOUT_TRACKER_TABLE);
        db.execSQL(CREATE_WEEKLY_CALORIES_TABLE);
    }


    public float getCaloriesToBurn(String email){
        SQLiteDatabase db = this.getReadableDatabase();
        String query="SELECT caloriestoBurn FROM user_details WHERE email=?";
        Cursor cursor=db.rawQuery(query,new String[]{email});
        float currentCalories = 0;
        if (cursor.moveToFirst()) {
            currentCalories = Float.parseFloat(cursor.getString(cursor.getColumnIndexOrThrow("caloriestoBurn")));
        }
        cursor.close();
        db.close();
        return currentCalories;

    }
    public boolean updateCaloriesToBurn(String email,float newCaloriesToBurn){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("caloriestoBurn", newCaloriesToBurn);
        int rowsAffected = db.update("user_details", values, "email = ?", new String[] { email });
        db.close();
        return rowsAffected>0;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database version upgrades here
        Log.d("DatabaseHelper", "Updating database tables");
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS user_details");
        db.execSQL("DROP TABLE IF EXISTS user_workout_tracker");
        db.execSQL("DROP TABLE IF EXISTS weekly_calories");
        onCreate(db);
    }

    public void deleteData(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("user_details", "email = ?", new String[]{email});
        db.delete("users", "email = ?", new String[]{email});
    }

    public Cursor getUserAndDetailsByEMail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT u.email, u.fullname, ud.* FROM users u " +
                "INNER JOIN user_details ud ON u.email = ud.email " +
                "WHERE u.email = ?";
        return db.rawQuery(query, new String[]{email});
    }

    public boolean getCredentials(String username,String password){
        SQLiteDatabase db=this.getReadableDatabase();
        String query="SELECT count(*) FROM users WHERE email=? AND password=?";
        Cursor cursor=db.rawQuery(query,new String[]{username,password});
        cursor.moveToFirst();
        int count=cursor.getInt(0);
        cursor.close();
        return count>0;
    }

    public boolean isEmailExists(String email){
        SQLiteDatabase db=this.getReadableDatabase();
        String query="SELECT count(*) FROM users WHERE email=?";
        Cursor cursor=db.rawQuery(query,new String[]{email});
        cursor.moveToFirst();
        int count= cursor.getInt(0);
        cursor.close();
        return count>0;
    }

    public boolean UpdatePersonalDetails(String fullname, String email, String age, String height, String weight,
                                         String activityLevel, String goal, String locationPreference,String duration,String intensity,String dreamweight,Double bmi,String gender){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues userValues=new ContentValues();
        userValues.put("fullname",fullname);
        int userUpdateCount=db.update("users",userValues,"email=?",new String[]{email});
        ContentValues userDetailsValues = new ContentValues();

        userDetailsValues.put("age", age);
        userDetailsValues.put("height", height);
        userDetailsValues.put("weight", weight);
        userDetailsValues.put("activity_level", activityLevel);
        userDetailsValues.put("goal", goal);
        userDetailsValues.put("location_preference", locationPreference);
        userDetailsValues.put("intensity_level",intensity);
        userDetailsValues.put("duration",duration);
        userDetailsValues.put("dream_weight",dreamweight);
        userDetailsValues.put("BMI",bmi);
        userDetailsValues.put("gender",gender);
        int userDetailsUpdateCount=db.update("user_details",userDetailsValues,"email=?",new String[]{email});

        return (userDetailsUpdateCount>0 && userUpdateCount>0);
    }

    public void insertSelectedItems(String email, List<String> selectedItems) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        String selectedItemsString = TextUtils.join(", ", selectedItems);
        values.put("equipmentList", selectedItemsString);

        // Update the user_details table
        db.update("user_details", values, "email = ?", new String[]{email});
    }

    // Method to insert data into tables
    public void insertData(String fullname, String email, String password, String age, Double height, Double weight,
                           String activityLevel, String goal, String locationPreference, String workoutPreferences,String duration,String intensity,Double dreamweight,Double bmi,String gender) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues userValues = new ContentValues();
        userValues.put("email", email);
        userValues.put("fullname", fullname);
        userValues.put("password", password);
        db.insert("users", null, userValues);

        ContentValues userDetailsValues = new ContentValues();
        userDetailsValues.put("email", email);
        userDetailsValues.put("age", age);
        userDetailsValues.put("height", height);
        userDetailsValues.put("weight", weight);
        userDetailsValues.put("activity_level", activityLevel);
        userDetailsValues.put("goal", goal);
        userDetailsValues.put("location_preference", locationPreference);
        userDetailsValues.put("workout_preferences", workoutPreferences);
        userDetailsValues.put("intensity_level",intensity);
        userDetailsValues.put("duration",duration);
        userDetailsValues.put("dream_weight",dreamweight);
        userDetailsValues.put("BMI",bmi);
        userDetailsValues.put("gender",gender);

        db.insert("user_details", null, userDetailsValues);
    }

    public void insertNewRecord(String userId, float caloriesBurnt) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("calories_burnt", caloriesBurnt);
        values.put("timestamp", getCurrentTimestamp()); // Automatically set the current timestamp

        db.insert("weekly_calories", null, values);
        db.close();
    }

    public float getLatestCalories(String userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("weekly_calories", new String[]{"calories_burnt"}, "user_id = ?", new String[]{String.valueOf(userId)}, null, null, "timestamp DESC", "1");

        float currentCalories = 0;
        if (cursor.moveToFirst()) {
            currentCalories = cursor.getFloat(cursor.getColumnIndexOrThrow("calories_burnt"));
        }
        cursor.close();
        db.close();
        return currentCalories;
    }

    public void updateTotalCalories(String userId, float newCalories) {
        float currentCalories = getLatestCalories(userId);
        float updatedCalories = currentCalories + newCalories;
        String currentTimestamp = getCurrentTimestamp();

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("calories_burnt", updatedCalories);
        values.put("timestamp", currentTimestamp); // Update timestamp

        // Update the latest record
        int rows = db.update("weekly_calories", values, "user_id = ? AND timestamp = (SELECT MAX(timestamp) FROM weekly_calories WHERE user_id = ?)", new String[] {String.valueOf(userId), String.valueOf(userId)});

        // If no existing record, insert a new one
        if (rows == 0) {
            values.put("user_id", userId);
            db.insert("weekly_calories", null, values);
        }
        db.close();
    }

    private String getCurrentTimestamp() {
        // Format the current timestamp as required for your database
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return dateFormat.format(new Date());
    }


    public int getUserWorkoutNumber(String userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("user_workout_tracker", new String[]{"workout_number"}, "user_id = ?", new String[]{userId}, null, null, null);

        int workoutNumber = 1;
        if (cursor.moveToFirst()) {
            workoutNumber = cursor.getInt(cursor.getColumnIndexOrThrow("workout_number"));
        }
        cursor.close();
        db.close();
        return workoutNumber;
    }

    public void updateWorkoutNumber(String userId, int newWorkoutNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("workout_number", newWorkoutNumber);

        int rowsAffected = db.update("user_workout_tracker", values, "user_id = ?", new String[] { userId });
        if (rowsAffected == 0) {
            values.put("user_id", userId);
            db.insert("user_workout_tracker", null, values);
        }

        db.close();
    }
}
