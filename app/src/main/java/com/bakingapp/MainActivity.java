package com.bakingapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements RecipeStepsFragment.OnStepClickListener{

    public ArrayList<Recipe> recipes;
    private Context mainContext;
    public static Recipe currentRecipe;
    public int currRecipeStep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //library to help with UI binding
        ButterKnife.bind(this);

        mainContext = getApplicationContext();
        recipes = new ArrayList<Recipe>();
        currRecipeStep = 0;

        //pull and parse Recipe JSON
        getRecipes();

        RecipesFragment fragRecipe = new RecipesFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("recipes", recipes);
        fragRecipe.setArguments(bundle);

        if(savedInstanceState==null){
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.container, fragRecipe)
                    .addToBackStack(null)
                    .commit();
        }
    }

    public void setCurrentRecipe(Recipe recipe){
        currentRecipe = recipe;
    }



    @Override
    public void onBackPressed() {

        FragmentManager fragmentManager = getSupportFragmentManager();

        int count = fragmentManager.getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
        } else {
            fragmentManager.popBackStack();
            fragmentManager.executePendingTransactions();
            StepsDetailFragment.releasePlayer();
        }
    }


    public void getRecipes(){

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(getString(R.string.recipe_url),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for(int i = 0; i < response.length(); i++){
                                JSONObject obj = response.getJSONObject(i);
                                recipes.add(new Recipe(obj.getInt("id"),
                                        obj.getString("name"),
                                        obj.getInt("servings")));

                                JSONArray ingredients = obj.getJSONArray("ingredients");
                                for(int j = 0 ; j < ingredients.length(); j++){
                                    JSONObject obj2 = ingredients.getJSONObject(j);
                                    recipes.get(i).addIngredient(obj2.getInt("quantity"),
                                            obj2.getString("measure"),
                                            obj2.getString("ingredient"));
                                }

                                JSONArray step = obj.getJSONArray("steps");
                                for(int z = 0; z < step.length(); z++){
                                    JSONObject obj3 = step.getJSONObject(z);
                                    recipes.get(i).addStep(obj3.getInt("id"),
                                            obj3.getString("shortDescription"),
                                            obj3.getString("description"),
                                            obj3.getString("videoURL"),
                                            obj3.getString("thumbnailURL"));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        // Access the RequestQueue through your singleton class.
        RemoteConnection.getInstance(mainContext).addToRequestQueue(jsonArrayRequest);
    }

    @Override
    public void onStepSelected(int position) {
        this.currRecipeStep = position;
    }

    public int getStepSelected(){
        return this.currRecipeStep;
    }
}
