package com.fitness.myapplication;

import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

import static org.hamcrest.CoreMatchers.not;

import com.fitness.myapplication.activities.WorkoutTrackerActivity;

@RunWith(AndroidJUnit4.class)
public class WorkoutTrackerActivityTest {

    @Rule
    public ActivityScenarioRule<WorkoutTrackerActivity> activityRule =
            new ActivityScenarioRule<>(WorkoutTrackerActivity.class);

    @Test
    public void testWorkoutStartStop() {
        onView(withId(R.id.buttonStart)).perform(click());
        onView(withId(R.id.textViewWorkoutDuration)).check(matches(withText("Workout in progress...")));

        onView(withId(R.id.buttonStop)).perform(click());
        onView(withId(R.id.textViewWorkoutDuration)).check(matches(not(withText("Workout in progress..."))));
    }

    @Test
    public void testWorkoutSubmit() {
        onView(withId(R.id.buttonStart)).perform(click());
        onView(withId(R.id.buttonStop)).perform(click());

        onView(withId(R.id.buttonSubmit)).perform(click());
        onView(withId(R.id.textViewWorkoutDuration)).check(matches(withText("0")));

    }
}
