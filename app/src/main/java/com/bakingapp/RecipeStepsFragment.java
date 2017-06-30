package com.bakingapp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
 * Fragment to display RecipeSteps
 *
 * Created by aditya on 6/26/17.
 */

public class RecipeStepsFragment extends Fragment {

    @BindView(R.id.rvRecipeSteps)
    RecyclerView rvStepsView;
    @BindView(R.id.lvIngredients)
    ExpandableListView ingredientsList;
    RecyclerView.Adapter stepsAdapter;
    RecyclerView.LayoutManager stepsLayoutManager;
    private Context mContext;
    public static Recipe currRecipe;
    private boolean tablet;
    OnStepClickListener mCallback;

    public interface OnStepClickListener{
        void onStepSelected(int position);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mCallback = (OnStepClickListener) context;
        }catch(ClassCastException e){
            throw new ClassCastException(context.toString()
                    + "must implement interface");
        }
    }

    public RecipeStepsFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_recipe_steps, container, false);
        ButterKnife.bind(this, rootView);
        mContext = getActivity();
        tablet = getResources().getBoolean(R.bool.isTablet);

        Bundle bundle = this.getArguments();
        if(bundle!=null){
            currRecipe = (Recipe)bundle.getSerializable("steps");
        }

        //create steps RecyclerView
        stepsLayoutManager = new LinearLayoutManager(mContext);
        rvStepsView.setLayoutManager(stepsLayoutManager);
        stepsAdapter = new RecipeStepsAdapter();
        rvStepsView.setAdapter(stepsAdapter);

        if(!tablet){
            //setup fragment navigation
            ((AppCompatActivity) mContext).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setHasOptionsMenu(true);
        }
        ((AppCompatActivity) mContext).getSupportActionBar().setTitle(currRecipe.getName());

        IngredientsAdapter ingredientAdapter = new IngredientsAdapter(mContext,currRecipe.getIngredients());
        ingredientsList.setAdapter(ingredientAdapter);

        if(tablet){
            StepsDetailFragment frag = new StepsDetailFragment();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.detail_container, frag, "StepsDetailFragment")
                    .commit();
        }

        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id){
            case android.R.id.home:
                getActivity().onBackPressed();
                ((AppCompatActivity) mContext).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                ((AppCompatActivity) mContext).getSupportActionBar().setTitle(R.string.app_name);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public class RecipeStepsAdapter extends RecyclerView.Adapter<RecipeStepsAdapter.ViewHolder>{

        public class ViewHolder extends RecyclerView.ViewHolder{

            View view;
            @BindView(R.id.tvDesc)
            TextView tvDesc;

            public ViewHolder(View v){
                super(v);
                view = v;
                ButterKnife.bind(this,view);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recipe_steps_view, parent, false);

            return new ViewHolder(v);
        }

        //fill in step views
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final Recipe.Step step = currRecipe.getSteps().get(position);
            final int pos = position;
            holder.tvDesc.setText(step.getDesc());

            holder.tvDesc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCallback.onStepSelected(pos);

                    if(!tablet){
                        StepsDetailFragment frag = new StepsDetailFragment();
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.container, frag, "StepsDetailFragment")
                                .addToBackStack(null)
                                .commit();
                    }else{
                        StepsDetailFragment frag = new StepsDetailFragment();
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.detail_container, frag, "StepsDetailFragment")
                                .commit();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return currRecipe.getSteps().size();
        }

    }
}
