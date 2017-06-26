package com.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    public ArrayList<Recipe> recipes;
    private Context mainContext;
    @BindView(R.id.rvRecipeMain)
    RecyclerView recipeView;
    private RecyclerView.Adapter recipeAdapter;
    private RecyclerView.LayoutManager recipeLayoutManager;

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

        //setup linear layout manager
        recipeLayoutManager = new LinearLayoutManager(this);
        recipeView.setLayoutManager(recipeLayoutManager);

        recipeAdapter = new MyAdapter(recipes);
        recipeView.setAdapter(recipeAdapter);

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

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{

        public ArrayList<Recipe> recip;


        public class ViewHolder extends RecyclerView.ViewHolder {

            View view;

            @BindView(R.id.tvRecipeMain) TextView tvRecipeName;
            @BindView(R.id.tvRecipeServing) TextView tvRecipeServing;
            public ViewHolder (View v){
                super(v);
                view = v;
                ButterKnife.bind(this,v);
            }
        }

        public MyAdapter(ArrayList<Recipe> r){
            recip = r;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.main_recipe_card, parent, false);

            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.tvRecipeName.setText(recip.get(position).getName());
            holder.tvRecipeServing.setText(getString(R.string.serves) + recip.get(position).getServings());

            final Recipe r = recipes.get(position);

            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //open recipe steps screen here and pass recipe object to RecipeStepsActivity
                    Intent stepsIntent = new Intent(mainContext, RecipeStepsActivity.class);
                    Bundle b = new Bundle();
                    b.putSerializable("steps", r);
                    stepsIntent.putExtras(b);
                    startActivity(stepsIntent);
                }
            });

        }

        @Override
        public int getItemCount() {
            return recip.size();
        }


    }


}
