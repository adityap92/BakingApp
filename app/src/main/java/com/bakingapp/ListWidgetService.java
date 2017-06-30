package com.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.bakingapp.data.RecipeIngredients;
import com.bakingapp.data.RecipeTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aditya on 6/28/17.
 */

public class ListWidgetService extends RemoteViewsService {

    Context mContext;


    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        mContext = getApplicationContext();

        return (new ListRemoteViewsFactory(mContext,
                intent.getBooleanExtra("ingredients", false),
                intent.getIntExtra("pos",0)));
    }
}

class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    Context mContext;
    boolean displayIngredients;
    List<RecipeIngredients> recipes;
    List<Recipe> widgetRecipes;
    int pos;


        public ListRemoteViewsFactory(Context applicationContext, boolean ingredients, int position){
            mContext = applicationContext;
            displayIngredients = ingredients;
            pos = position;
        }

        @Override
        public void onCreate() {
            recipes = new ArrayList<RecipeIngredients>();
            widgetRecipes = new ArrayList<Recipe>();
        }

        //setup new list to access recipe and ingredient data easier
        public void setupLists(){

            ArrayList<String> names = new ArrayList<String>();
            for(RecipeIngredients ri : recipes){
                if(!names.contains(ri.recipeName)) {
                    names.add(ri.recipeName);
                    widgetRecipes.add(new Recipe(0, ri.recipeName, 0));
                }
                    widgetRecipes.get(names.indexOf(ri.recipeName)).addIngredient(0,"",ri.recipeIngredient);

            }
        }

        @Override
        public void onDataSetChanged() {

            if(widgetRecipes.size()==0){
                Cursor cursor=null;
                cursor = mContext.getContentResolver().query(RecipeTable.CONTENT_URI,null,null,null,null);
                recipes = RecipeTable.getRows(cursor,true);
                cursor.close();

                setupLists();
            }
        }

        @Override
        public void onDestroy() {
        }

        @Override
        public int getCount() {
            return displayIngredients?widgetRecipes.get(pos).getIngredients().size():
                    widgetRecipes.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {

            RemoteViews views = new RemoteViews(mContext.getPackageName(),
                    R.layout.widget_list_item);

            if(displayIngredients){
                views.setTextViewText(R.id.tvWidgetItem,
                        widgetRecipes.get(pos).getIngredients().get(position).getIngredientName());
                views.setViewVisibility(R.id.bWidgetPrev, View.VISIBLE);
            }else{
                views.setTextViewText(R.id.tvWidgetItem, widgetRecipes.get(position).getName());

                Intent ingredientIntent = new Intent();
                ingredientIntent.putExtra("ingredients", true);
                ingredientIntent.putExtra("pos", position);
                views.setOnClickFillInIntent(R.id.tvWidgetItem, ingredientIntent);
            }


            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }

