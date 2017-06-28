package com.bakingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by aditya on 6/22/17.
 */

public class IngredientsAdapter extends BaseExpandableListAdapter {

    ArrayList<Recipe.Ingredient> ingredients;
    ArrayList<String> groupName;
    Context context;
    //@BindView(R.id.tvIngredientName)
    TextView tvIngredientName;
    //@BindView(R.id.tvIngredientMeasure)
    TextView tvIngredientMeasure;
    //@BindView(R.id.tvIngredientGroup)
    TextView tvIngredientGroup;

    public IngredientsAdapter(Context c, ArrayList<Recipe.Ingredient> i){
        this.ingredients = i;
        this.context = c;
        groupName = new ArrayList<String>();
        groupName.add("Ingredients: (Click to View)");

    }

    @Override
    public View getGroupView(int groupPos, boolean isExpanded, View view, ViewGroup parent) {
        if(view==null){
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.ingredients_group, null);
        }
        //ButterKnife.bind(context, view);

        tvIngredientGroup = (TextView) view.findViewById(R.id.tvIngredientGroup);

        tvIngredientGroup.setText(groupName.get(groupPos));


        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup parent) {

        if(view == null){
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.ingredients_view, null);
        }

        //ButterKnife.bind(context, view);

        tvIngredientName = (TextView) view.findViewById(R.id.tvIngredientName);
        tvIngredientMeasure = (TextView) view.findViewById(R.id.tvIngredientMeasure);

        tvIngredientName.setText(ingredients.get(childPosition).getIngredientName());
        tvIngredientMeasure.setText(ingredients.get(childPosition).getQuantity() + " "
                + ingredients.get(childPosition).getMeasure() + " ");

        return view;
    }

    @Override
    public int getGroupCount() {
        return groupName.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return ingredients.size();
    }

    @Override
    public Object getGroup(int i) {
        return groupName.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return ingredients.get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int childPos) {
        return childPos;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
}
