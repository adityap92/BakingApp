package com.bakingapp;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by aditya on 6/28/17.
 */

@RunWith(AndroidJUnit4.class)
public class StepDetailBasicTest {

    @Rule
    public ActivityTestRule<MainActivity> mainActivityTestRule
            = new ActivityTestRule<MainActivity>(MainActivity.class);

    private IdlingResource mIdlingResource;

    @Before
    public void registerIdlingResource(){
        mIdlingResource = mainActivityTestRule.getActivity().getIdlingResource();

        Espresso.registerIdlingResources(mIdlingResource);
    }

    @Test
    public void displayContainer(){
        onView(withId(R.id.container)).check(matches(isDisplayed()));
    }

    @Test
    public void displayRecipeIngredients(){
        onView(withId(R.id.rvRecipeMain)).perform(RecyclerViewActions.actionOnItemAtPosition(0,click()));
        onView(withId(R.id.lvIngredients)).check(matches(isDisplayed()));
    }

    @Test
    public void displayRecipeSteps(){
        onView(withId(R.id.rvRecipeMain)).perform(RecyclerViewActions.actionOnItemAtPosition(0,click()));
        onView(withId(R.id.rvRecipeSteps)).check(matches(isDisplayed()));
    }

    @After
    public void unregisterIdlingResource(){
        if(mIdlingResource != null){
            Espresso.unregisterIdlingResources(mIdlingResource);
        }
    }

}
