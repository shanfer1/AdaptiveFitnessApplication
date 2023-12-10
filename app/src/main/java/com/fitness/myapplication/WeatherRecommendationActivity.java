package com.fitness.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import android.content.pm.PackageManager;

public class WeatherRecommendationActivity extends AppCompatActivity {
    private TextView textViewLocation, textViewWeatherInfo, textViewWorkoutSuggestion;
    private Button buttonGetWorkout;
    private double temperature;
    private double humidity;
    private double windSpeed;
    double precipitation;
    double snowVolume;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private static final String API_KEY = "a78f7708f7e1853bcf6c4c705f8207fa";
    private static final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather";

    @SuppressLint({"MissingInflatedId", "MissingPermission"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_recommendation);

        textViewLocation = findViewById(R.id.textViewLocation);
        textViewWeatherInfo = findViewById(R.id.textViewWeatherInfo);
        textViewWorkoutSuggestion = findViewById(R.id.textViewWorkoutSuggestion);
        buttonGetWorkout = findViewById(R.id.buttonGetWorkout);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener();


        buttonGetWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Your workout suggestion logic here
                getWorkoutRecommendation();

                showWorkoutSuggestionPopup();

            }
        });

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
    }
    private void showWorkoutSuggestionPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(WeatherRecommendationActivity.this);
        builder.setTitle("Workout Suggestion");

        // Use textViewWorkoutSuggestion's text for the dialog
        String workoutSuggestion = textViewWorkoutSuggestion.getText().toString();
        builder.setMessage(workoutSuggestion);

        // Add a button to close the dialog
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void getWorkoutRecommendation() {
        String workoutSuggestion = WorkoutSuggestion.suggestOutdoorWorkout(temperature, humidity, windSpeed, precipitation, snowVolume);
        textViewWorkoutSuggestion.setText(workoutSuggestion);
    }

    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            new FetchAddressTask().execute(location.getLatitude(), location.getLongitude());
            new FetchWeatherTask().execute(location.getLatitude(), location.getLongitude());
        }

        // Implement other abstract methods...
    }

    private class FetchAddressTask extends AsyncTask<Double, Void, String> {
        @Override
        protected String doInBackground(Double... params) {
            Geocoder geocoder = new Geocoder(WeatherRecommendationActivity.this, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(params[0], params[1], 1);
                if (addresses != null && !addresses.isEmpty()) {
                    return (""+ addresses.get(0).getAddressLine(0));
                }
            } catch (IOException e) {
                Log.e("Geocoder Error", "Unable to get address", e);
            }
            return "No address found";
        }

        @Override
        protected void onPostExecute(String address) {
            textViewLocation.setText(address);
        }
    }

    private class FetchWeatherTask extends AsyncTask<Double, Void, String> {
        @Override
        protected String doInBackground(Double... params) {
            try {
                URL url = new URL(WEATHER_URL + "?lat=" + params[0] + "&lon=" + params[1] + "&appid=" + API_KEY + "&units=metric");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder jsonData = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonData.append(line);
                }
                reader.close();
                return jsonData.toString();
            } catch (Exception e) {
                Log.e("WeatherTask Error", "Error fetching weather data", e);
                return null;
            }
        }
        @Override
        protected void onPostExecute(String weatherData) {
            if (weatherData != null) {
                try {
                    JSONObject jsonObject = new JSONObject(weatherData);

                    // Main weather data
                    JSONObject mainWeather = jsonObject.getJSONObject("main");
                    temperature = mainWeather.getDouble("temp");
                    humidity = mainWeather.getDouble("humidity");

                    // Wind data
                    JSONObject wind = jsonObject.getJSONObject("wind");
                    windSpeed = wind.getDouble("speed");

                    // Precipitation
                    precipitation = 0.0;
                    if (jsonObject.has("rain")) {
                        JSONObject rain = jsonObject.getJSONObject("rain");
                        precipitation = rain.optDouble("1h", 0.0); // "1h" key
                    }

                    // Clouds data
                    JSONObject clouds = jsonObject.getJSONObject("clouds");
                    int cloud = clouds.getInt("all");

                    // Weather condition
                    String weatherCondition = "";
                    if (jsonObject.has("weather")) {
                        JSONArray weatherArray = jsonObject.getJSONArray("weather");
                        if (weatherArray.length() > 0) {
                            JSONObject weather = weatherArray.getJSONObject(0);
                            String mainCondition = weather.getString("main");
                            String description = weather.getString("description");
                            weatherCondition = mainCondition + ": " + description;
                        }
                    }

                    // Formatting weather information
                    String weatherInfo = String.format(Locale.getDefault(),
                            "%s\nTemperature: %.1f°C\nHumidity: %.0f%%\nWind Speed: %.1f km/h\nPrecipitation: %.1f mm",
                            weatherCondition, temperature, humidity, windSpeed, precipitation, cloud);
                    textViewWeatherInfo.setText(weatherInfo);
                } catch (Exception e) {
                    Log.e("JSON Parsing Error", "Error parsing weather data", e);
                }
            }
        }

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
    }
}
