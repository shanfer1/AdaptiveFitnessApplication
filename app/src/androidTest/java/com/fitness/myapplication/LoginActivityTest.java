package com.fitness.myapplication;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginActivityTest {

    private DatabaseHelper mockDbHelper;


    @Before
    public void setUp(){
        mockDbHelper =Mockito.mock(DatabaseHelper.class);

        DatabaseHelperFactory.setMockInstance(mockDbHelper);
        Mockito.when(mockDbHelper.getCredentials("test@example.com", "Password123!")).thenReturn(true);

    }

    @After
    public void tearDown(){
        DatabaseHelperFactory.setMockInstance(null);
    }

    @Rule
    public ActivityScenarioRule<Login> activityRule =
            new ActivityScenarioRule<>(Login.class);


    @Test
    public void testInputFields() {
        Espresso.onView(withId(R.id.usernameText))
                .perform(typeText("johndoe@example.com"), closeSoftKeyboard());
        Espresso.onView(withId(R.id.passwordText))
                .perform(typeText("Password123!"), closeSoftKeyboard());

        // Assert that the text was changed
        Espresso.onView(withId(R.id.usernameText))
                .check(matches(withText("johndoe@example.com")));
        Espresso.onView((withId(R.id.passwordText))).check(matches(withText("Password123!")));
    }


    @Test
    public void testAttemptLogin() {
        Espresso.onView(withId(R.id.usernameText))
                .perform(typeText(""), closeSoftKeyboard());
        Espresso.onView(withId(R.id.passwordText))
                .perform(typeText(""), closeSoftKeyboard());
        Espresso.onView(withId(R.id.loginbutton)).perform(click());

        // Assert that the text was changed
        Espresso.onView(withId(R.id.usernameText))
                .check(matches(hasErrorText("Username should not be empty")));
        Espresso.onView((withId(R.id.passwordText))).check(matches(hasErrorText("Password should not be empty")));
    }


    @Test
    public void testCheckCredentials() {
        Mockito.when(mockDbHelper.getCredentials("johndoe@example.com","Password123!")).thenReturn(false);
        Espresso.onView(withId(R.id.usernameText))
                .perform(typeText("johndoe@example.com"), closeSoftKeyboard());
        Espresso.onView(withId(R.id.passwordText))
                .perform(typeText("Password123!"), closeSoftKeyboard());


        Espresso.onView(withId(R.id.loginbutton)).perform(click());

        // Assert that the text was changed
        Espresso.onView(withId(R.id.usernameText))
                .check(matches(hasErrorText("Invalid Credentials")));
        Espresso.onView((withId(R.id.passwordText))).check(matches(hasErrorText("Invalid Credentials")));
    }




    @Test
    public void testLogCurrentUser() {
        Context context = ApplicationProvider.getApplicationContext();
        String testEmail = "test@example.com";

        Mockito.when(mockDbHelper.getCredentials(testEmail,"Password123!")).thenReturn(true);

        Espresso.onView(withId(R.id.usernameText))
                .perform(typeText(testEmail), closeSoftKeyboard());
        Espresso.onView(withId(R.id.passwordText))
                .perform(typeText("Password123!"), closeSoftKeyboard());


        Espresso.onView(withId(R.id.loginbutton)).perform(click());
        SharedPreferences sharedPreferences=context.getSharedPreferences("MySharedPrefs",Context.MODE_PRIVATE);


        String storedEmail = sharedPreferences.getString("LoggedInUser", null);

        assertEquals(testEmail, storedEmail);
    }
}
