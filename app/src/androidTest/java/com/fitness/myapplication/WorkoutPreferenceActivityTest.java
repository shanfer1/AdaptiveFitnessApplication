package com.fitness.myapplication;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;

import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.is;

import androidx.test.espresso.contrib.RecyclerViewActions;

@RunWith(AndroidJUnit4.class)
public class WorkoutPreferenceActivityTest {

    @Rule
    public ActivityScenarioRule<WorkoutPreference> activityRule =
            new ActivityScenarioRule<>(WorkoutPreference.class);

//    @Test
//    public void testBackButton() {
//
//        Intents.init();
//
//        onView(withId(R.id.backbutton)).perform(scrollTo(), click());
//
//        intended(hasComponent(PersonalDetails.class.getName()));
//
//        Intents.release();
//    }

    @Test
    public void testSeekBarFunctionality() {
        Espresso.onView(withId(R.id.seekBarduration))
                .perform(ViewActions.swipeRight());
        Espresso.onView(withId(R.id.durationValue))
                .check(matches(withText(containsString("minutes"))));

        Espresso.onView(withId(R.id.seekBarintensity))
                .perform(ViewActions.swipeRight());
        Espresso.onView(withId(R.id.intensityValue))
                .check(matches(withText(containsString("/10"))));
    }

    @Test
    public void testActivitySpinnerSelection() {


        Espresso.onView(withId(R.id.acitivityspinner)).perform(click());
        Espresso.onData(allOf(is(instanceOf(String.class)), is("Mild"))).perform(click());
        Espresso.onView(withId(R.id.acitivityspinner)).check(matches(withSpinnerText(containsString("Mild"))));

    }

    @Test
    public void testGoalSpinnerSelection() {


        Espresso.onView(withId(R.id.goalspinner)).perform(click());
        Espresso.onData(allOf(is(instanceOf(String.class)), is("Loose Weight"))).perform(click());
        Espresso.onView(withId(R.id.goalspinner)).check(matches(withSpinnerText(containsString("Loose Weight"))));

    }

    @Test
    public void testLocationSpinnerSelection() {

        Espresso.onView(withId(R.id.locationspinner)).perform(click());
        Espresso.onData(allOf(is(instanceOf(String.class)), is("Indoor"))).perform(click());
        Espresso.onView(withId(R.id.locationspinner)).check(matches(withSpinnerText(containsString("Indoor"))));
    }
    // Additional tests as needed...
}
