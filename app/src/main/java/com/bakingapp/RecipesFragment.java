package com.bakingapp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Fragment to hold CardView of all Recipes
 * Created by aditya on 6/26/17.
 */

public class RecipesFragment extends Fragment {

    private Context mainContext;
    @BindView(R.id.rvRecipeMain)
    RecyclerView recipeView;
    private RecyclerView.Adapter recipeAdapter;
    private RecyclerView.LayoutManager recipeLayoutManager;
    private ArrayList<Recipe> recipes;

    public RecipesFragment(){
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_recipes, container, false);
        ButterKnife.bind(this, rootView);

        Bundle bundle = this.getArguments();
        if(bundle!=null){
            recipes = (ArrayList<Recipe>)bundle.getSerializable("recipes");
        }
        mainContext = getActivity();

        boolean tablet = getResources().getBoolean(R.bool.isTablet);
        if(!tablet){
            //setup linear layout manager
            recipeLayoutManager = new LinearLayoutManager(mainContext);
            recipeView.setLayoutManager(recipeLayoutManager);
            recipeAdapter = new MyAdapter(recipes);
            recipeView.setAdapter(recipeAdapter);
        }else{
            //setup grid layout if using tablet
            recipeLayoutManager = new GridLayoutManager(mainContext,4);
            recipeView.setLayoutManager(recipeLayoutManager);
            recipeAdapter = new MyAdapter(recipes);
            recipeView.setAdapter(recipeAdapter);
        }

        return rootView;
    }

    //Adapter for Recipes that are displayed with cardview
    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{

        public ArrayList<Recipe> recip;


        public class ViewHolder extends RecyclerView.ViewHolder {

            View view;

            @BindView(R.id.tvRecipeMain)
            TextView tvRecipeName;
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
                    //open recipe steps screen here and pass recipe object to RecipeStepsFragment
                    RecipeStepsFragment fragStep = new RecipeStepsFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("steps", r);
                    fragStep.setArguments(bundle);

                    //set current recipe for StepsDetail
                    ((MainActivity) getActivity()).setCurrentRecipe(r);

                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction()
                            .add(R.id.container, fragStep, "RecipeStepsFragment")
                            .addToBackStack(null)
                            .commit();
                }
            });

        }

        @Override
        public int getItemCount() {
            return recip.size();
        }
    }
}
