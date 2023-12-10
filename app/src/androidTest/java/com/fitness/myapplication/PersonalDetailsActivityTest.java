package com.fitness.myapplication;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;

import static org.hamcrest.Matchers.is;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.Matchers.allOf;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class PersonalDetailsActivityTest {

    @Rule
    public ActivityScenarioRule<PersonalDetails> activityRule = new ActivityScenarioRule<>(PersonalDetails.class);

    @Test
    public void testGenderSpinnerSelection() {
        Espresso.onView(withId(R.id.genderspinner)).perform(click());
        Espresso.onData(allOf(is(instanceOf(String.class)), is("Male"))).perform(click());
        Espresso.onView(withId(R.id.genderspinner)).check(matches(withSpinnerText(containsString("Male"))));
    }

    @Test
    public void testInvalidAgeInput() {
        Espresso.onView(withId(R.id.editTextNumber)).perform(typeText(""), closeSoftKeyboard());
        Espresso.onView(withId(R.id.NextButton)).perform(click());
        Espresso.onView(withId(R.id.editTextNumber)).check(matches(hasErrorText("Please enter a valid age")));
    }
    @Test
    public void testInvalidHeightInput() {
        Espresso.onView(withId(R.id.editTextHeight)).perform(typeText(""), closeSoftKeyboard());
        Espresso.onView(withId(R.id.NextButton)).perform(click());
        Espresso.onView(withId(R.id.editTextHeight)).check(matches(hasErrorText("Please enter a valid height")));
    }

    @Test
    public void testInvalidWeightInput() {
        Espresso.onView(withId(R.id.editTextWeight)).perform(typeText(""), closeSoftKeyboard());
        Espresso.onView(withId(R.id.NextButton)).perform(click());
        Espresso.onView(withId(R.id.editTextWeight)).check(matches(hasErrorText("Please enter a valid weight")));
    }

    @Test
    public void testInvalidDreamWeightInput() {
        Espresso.onView(withId(R.id.dreamweight)).perform(typeText(""), closeSoftKeyboard());
        Espresso.onView(withId(R.id.NextButton)).perform(click());
        Espresso.onView(withId(R.id.dreamweight)).check(matches(hasErrorText("Please enter a valid dream weight")));
    }



    @Test
    public void testBackButton() {
        Intents.init();

        Espresso.onView(withId(R.id.back_button)).perform(click());

        intended(hasComponent(Create_Account.class.getName()));

        Intents.release();
    }

}
