package com.bakingapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public ArrayList<Recipe> recipes;
    private Context mainContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainContext = getApplicationContext();
        recipes = new ArrayList<Recipe>();

        ((TextView) findViewById(R.id.tvTest)).setText("asdf");

        //getRecipes();

    }


    public void getRecipes(View v){

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
