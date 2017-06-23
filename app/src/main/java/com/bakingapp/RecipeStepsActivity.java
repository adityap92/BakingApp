package com.bakingapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

/**
 * Created by aditya on 6/21/17.
 */

public class RecipeStepsActivity extends AppCompatActivity {

    Recipe currRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_steps);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //retrieve Recipe object
        currRecipe =(Recipe) getIntent().getSerializableExtra("steps");


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
