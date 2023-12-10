package com.fitness.myapplication;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class CreateAccountActivityTest {

    private DatabaseHelper mockDbHelper;


    @Before
    public void setUp(){
        mockDbHelper =Mockito.mock(DatabaseHelper.class);

        DatabaseHelperFactory.setMockInstance(mockDbHelper);
        Mockito.when(mockDbHelper.isEmailExists("existingemail@example.com")).thenReturn(true);

    }

    @After
    public void tearDown(){
        DatabaseHelperFactory.setMockInstance(null);
    }


    @Rule
    public ActivityScenarioRule<Create_Account> activityRule =
            new ActivityScenarioRule<>(Create_Account.class);

    @Test
    public void testInputFields() {
        Espresso.onView(withId(R.id.editTextText))
                .perform(typeText("John Doe"), closeSoftKeyboard());
        Espresso.onView(withId(R.id.editTextTextEmailAddress))
                .perform(typeText("johndoe@example.com"), closeSoftKeyboard());
        Espresso.onView(withId(R.id.editTextTextPassword))
                .perform(typeText("Password123!"), closeSoftKeyboard());
        Espresso.onView(withId(R.id.editTextTextPassword2))
                .perform(typeText("Password123!"), closeSoftKeyboard());

        // Assert that the text was changed
        Espresso.onView(withId(R.id.editTextText))
                .check(matches(withText("John Doe")));
        Espresso.onView((withId(R.id.editTextTextEmailAddress))).check(matches(withText("johndoe@example.com")));
        Espresso.onView((withId(R.id.editTextTextPassword))).check(matches(withText("Password123!")));
        Espresso.onView((withId(R.id.editTextTextPassword2))).check(matches(withText("Password123!")));


    }

    @Test
    public void testFormValidation() {

        //Simulate typing an Invalid email.
        Espresso.onView(withId(R.id.editTextTextEmailAddress)).perform(typeText("invalidemail"),closeSoftKeyboard());


        //Simulate typing an invalid password
        Espresso.onView(withId(R.id.editTextTextPassword)).perform(typeText("123"),closeSoftKeyboard());
        Espresso.onView(withId(R.id.editTextTextPassword2)).perform(typeText("123"),closeSoftKeyboard());


        //simulate tying an invalid fullname
        Espresso.onView(withId(R.id.editTextText)).perform(typeText("Muham"),closeSoftKeyboard());
        Espresso.onView(withId(R.id.button)).perform(click());

        Espresso.onView(withId(R.id.editTextText)).check(matches(hasErrorText("Full Name must be at least 6 characters")));
        Espresso.onView(withId(R.id.editTextTextEmailAddress)).check(matches(hasErrorText("Invalid Email Address")));
        Espresso.onView(withId(R.id.editTextTextPassword)).check(matches(hasErrorText("Password must be at least 8 characters, include a number and a special character")));



    }



    @Test
    public void testEmailExistsInDatabase() {
        Espresso.onView(withId(R.id.editTextTextEmailAddress))
                .perform(typeText("existingemail@example.com"), closeSoftKeyboard());
        Espresso.onView(withId(R.id.button)).perform(click());
        Espresso.onView(withId(R.id.editTextTextEmailAddress))
                .check(matches(hasErrorText("Email already in use")));
    }


}
