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

public class MainActivity extends AppCompatActivity {

    public ArrayList<Recipe> recipes;
    private Context mainContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //library to help with UI binding
        ButterKnife.bind(this);

        mainContext = getApplicationContext();
        recipes = new ArrayList<Recipe>();

        //pull and parse Recipe JSON
        getRecipes();

        RecipesFragment fragRecipe = new RecipesFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("recipes", recipes);
        fragRecipe.setArguments(bundle);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.container, fragRecipe)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed() {

        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
            //additional code
        } else {
            getSupportFragmentManager().popBackStack();
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
}
