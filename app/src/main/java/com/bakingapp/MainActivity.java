package com.bakingapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.bakingapp.data.RecipeIngredients;
import com.bakingapp.data.RecipeTable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements RecipeStepsFragment.OnStepClickListener{

    public ArrayList<Recipe> recipes;
    private Context mainContext;
    public static Recipe currentRecipe;
    public int currRecipeStep;
    final String TAG = MainActivity.class.getSimpleName();
    public RecipesFragment fragRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //library to help with UI binding
        ButterKnife.bind(this);

        mainContext = getApplicationContext();

        if(savedInstanceState==null){
            recipes = new ArrayList<Recipe>();
            currRecipeStep = 0;
            //pull and parse Recipe JSON
            getRecipes();
        }else{
            currRecipeStep = savedInstanceState.getInt("pos");
            recipes = (ArrayList<Recipe>) savedInstanceState.getSerializable("recipes");
        }
    }

    public void deleteEntries(){
        getContentResolver().delete(RecipeTable.CONTENT_URI,null,null);
    }

    public void addDBentries(){

        if(recipes.size()>0&&getRows()==0){
            for(Recipe r : recipes){
                for(Recipe.Ingredient i : r.getIngredients()){
                    ContentValues cv = new ContentValues();
                    cv.put(RecipeTable.FIELD_COL_NAME, r.getName());
                    cv.put(RecipeTable.FIELD_COL_INGREDIENT, i.getIngredientName());
                    getContentResolver().insert(RecipeTable.CONTENT_URI, cv);
                }
            }

        }
    }

    public int getRows(){
        Cursor cursor=null;
        List<RecipeIngredients> testRows=null;
        try{
            cursor = getContentResolver().query(RecipeTable.CONTENT_URI,null,null,null,null);
            testRows = RecipeTable.getRows(cursor,true);
        }catch(Exception e){}

        return testRows==null?0:testRows.size();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("recipes", recipes);
        outState.putInt("pos",currRecipeStep);
    }

    public void openFragment(){
        fragRecipe = new RecipesFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("recipes", recipes);
        fragRecipe.setArguments(bundle);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.container, fragRecipe, "RecipeFragment")
                .addToBackStack(null)
                .commit();
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
                                    recipes.get(i).addIngredient(Float.parseFloat(obj2.getString("quantity")),
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
                            addDBentries();
                            openFragment();
                        } catch (Exception e) {
                            Log.e(TAG,e.getLocalizedMessage());
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
