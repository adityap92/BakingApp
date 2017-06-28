package com.bakingapp;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.Assert.assertEquals;

/**
 * Created by aditya on 6/28/17.
 */

@RunWith(AndroidJUnit4.class)
public class StepDetailBasicTest {

    @Rule
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule
            = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        assertEquals("com.bakingapp", appContext.getPackageName());
    }

    @Test
    public void displayRecipeIngredients(){
        onView(withId(R.id.container)).check(matches(isDisplayed()));
    }

}
