package com.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by aditya on 6/21/17.
 */

public class RecipeStepsActivity extends AppCompatActivity {

    Recipe currRecipe;
    @BindView(R.id.lvIngredients)
    ExpandableListView ingredientsList;
    @BindView(R.id.rvRecipeSteps)
    RecyclerView rvStepsView;
    RecyclerView.Adapter stepsAdapter;
    RecyclerView.LayoutManager stepsLayoutManager;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_steps);
        ButterKnife.bind(this);

        mContext = getApplicationContext();

        //retrieve Recipe object
        currRecipe =(Recipe) getIntent().getSerializableExtra("steps");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(currRecipe.getName());

        IngredientsAdapter ingredientAdapter = new IngredientsAdapter(getApplicationContext(),currRecipe.getIngredients());
        ingredientsList.setAdapter(ingredientAdapter);

        //create steps RecyclerView
        stepsLayoutManager = new LinearLayoutManager(this);
        rvStepsView.setLayoutManager(stepsLayoutManager);
        stepsAdapter = new RecipeStepsAdapter();
        rvStepsView.setAdapter(stepsAdapter);


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

    public class RecipeStepsAdapter extends RecyclerView.Adapter<RecipeStepsAdapter.ViewHolder>{

        public class ViewHolder extends RecyclerView.ViewHolder{

            View view;
            @BindView(R.id.tvDesc)
            TextView tvDesc;

            public ViewHolder(View v){
                super(v);
                view = v;
                ButterKnife.bind(this,v);
            }

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recipe_steps_view, parent, false);

            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final Recipe.Step step = currRecipe.getSteps().get(position);
            holder.tvDesc.setText(step.getDesc());

            holder.tvDesc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent stepsDetail = new Intent(mContext, StepsDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("detail",step);
                    stepsDetail.putExtras(bundle);
                    startActivity(stepsDetail);
                }
            });

        }

        @Override
        public int getItemCount() {
            return currRecipe.getSteps().size();
        }

    }
}
