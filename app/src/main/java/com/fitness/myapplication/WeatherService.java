package com.fitness.myapplication;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherService {

    private static final String API_KEY = "a78f7708f7e1853bcf6c4c705f8207fa";  // Replace with your API key
    private static final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather";

    public static String getWeatherData(double latitude, double longitude) {
        try {
            URL url = new URL(WEATHER_URL + "?lat=" + latitude + "&lon=" + longitude + "&appid=" + API_KEY + "&units=metric");
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
            e.printStackTrace();
            return null;
        }
    }
}
