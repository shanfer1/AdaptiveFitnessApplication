package com.fitness.myapplication;

import org.junit.Test;
import static org.junit.Assert.*;

public class WorkoutSuggestionTest {

    @Test
    public void suggestOutdoorWorkout_ReturnsSwimmingSuggestion_WhenHot() {
        String suggestion = WorkoutSuggestion.suggestOutdoorWorkout(31.0, 50.0, 5.0, 0.0, 0.0);
        assertTrue(suggestion.contains("Swimming"));
    }

    @Test
    public void suggestOutdoorWorkout_ReturnsBriskWalkSuggestion_WhenCold() {
        String suggestion = WorkoutSuggestion.suggestOutdoorWorkout(4.0, 50.0, 5.0, 0.0, 0.0);
        assertTrue(suggestion.contains("brisk walk"));
    }

    @Test
    public void suggestOutdoorWorkout_ReturnsRunningSuggestion_WhenIdealTemperature() {
        String suggestion = WorkoutSuggestion.suggestOutdoorWorkout(20.0, 50.0, 5.0, 0.0, 0.0);
        assertTrue(suggestion.contains("Running"));
    }

    @Test
    public void suggestOutdoorWorkout_WarnsAboutDehydration_WhenHighHumidity() {
        String suggestion = WorkoutSuggestion.suggestOutdoorWorkout(25.0, 85.0, 5.0, 0.0, 0.0);
        assertTrue(suggestion.contains("dehydration"));
    }

    @Test
    public void suggestOutdoorWorkout_RecommendsLightActivities_WhenLowHumidity() {
        String suggestion = WorkoutSuggestion.suggestOutdoorWorkout(25.0, 15.0, 5.0, 0.0, 0.0);
        assertTrue(suggestion.contains("enjoy most outdoor activities"));
    }

    @Test
    public void suggestOutdoorWorkout_SuggestsIndoorActivities_WhenWindy() {
        String suggestion = WorkoutSuggestion.suggestOutdoorWorkout(25.0, 50.0, 15.0, 0.0, 0.0);
        assertTrue(suggestion.contains("Consider indoor activities"));
    }

    @Test
    public void suggestOutdoorWorkout_RecommendsWaterProofGear_WhenRaining() {
        String suggestion = WorkoutSuggestion.suggestOutdoorWorkout(25.0, 50.0, 5.0, 5.0, 0.0);
        assertTrue(suggestion.contains("water-proof gear"));
    }
}

//Test Case Explanation
//Each test case checks for a specific condition:
//
//Hot Weather: Checks if the suggestion includes swimming for temperatures above 30°C.
//Cold Weather: Checks if the suggestion includes a brisk walk for temperatures below 5°C.
//Ideal Temperature: Checks for outdoor activities like running or cycling for moderate temperatures.
//High Humidity: Verifies the warning about dehydration and suggestion for light activities.
//Low Humidity: Ensures the suggestion encourages most outdoor activities.
//Windy Conditions: Checks if indoor activities are suggested.
//Rainy Weather: Confirms the recommendation for indoor exercises or appropriate gear for outdoor activities.