package com.fitness.myapplication;

public class WorkoutSuggestion {

    static String suggestOutdoorWorkout(double temperature, double humidity, double windSpeed, double precipitation, double snowVolume) {
        StringBuilder suggestion = new StringBuilder();

        // Check temperature
        if (temperature > 30.0) { // Above 30°C
            suggestion.append("It's quite hot outside. You can do: \n1. Swimming \n 2. An indoor gym session.\n");
        } else if (temperature < 5.0) { // Below 5°C
            suggestion.append("It's cold outside. Consider a brisk walk or a run to keep warm.\n");
        } else {
            suggestion.append("Temperature is ideal for outdoor activities. You can go for: \n1. Running \n2. Cycling \n3. Team sports.\n");
        }

        // Check humidity
        if (humidity > 80) {
            suggestion.append("High humidity can lead to dehydration. Stay hydrated and prefer light activities like walking.\n");
        } else if (humidity < 20) {
            suggestion.append("Low humidity can cause dryness. Keep hydrated and you can enjoy most outdoor activities.\n");
        }

        // Check wind speed
        if (windSpeed > 10) { // Speed more than 10 km/h
            suggestion.append("Windy conditions. Avoid cycling or boating. Consider indoor activities.\n");
        }

        // Check precipitation
        if (precipitation > 0) {
            suggestion.append("It's raining. Indoor exercises or water-proof gear for outdoor activities are recommended.\n");
        }

//        // Check cloudiness
//        if (cloud > 80) {
//            suggestion.append("It's quite cloudy. Outdoor activities are still good, but keep an eye on the weather for changes.\n");
//        }

        return suggestion.toString();
    }
    }
