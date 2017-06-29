package com.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
        return (new ListRemoteViewsFactory(mContext));
    }
}

class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    Context mContext;
    Cursor mCursor;
    List<RecipeIngredients> recipes;
    List<Recipe> widgetRecipes;


        public ListRemoteViewsFactory(Context applicationContext){
            mContext = applicationContext;
        }

        @Override
        public void onCreate() {
            recipes = new ArrayList<RecipeIngredients>();
            widgetRecipes = new ArrayList<Recipe>();
        }

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
            Cursor cursor=null;
            cursor = mContext.getContentResolver().query(RecipeTable.CONTENT_URI,null,null,null,null);
            recipes = RecipeTable.getRows(cursor,true);
            cursor.close();

            if(widgetRecipes.size()==0)
                setupLists();
        }

        @Override
        public void onDestroy() {
        }

        @Override
        public int getCount() {
            return widgetRecipes.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {

            RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_item);

            views.setTextViewText(R.id.tvWidgetItem, widgetRecipes.get(position).getName());

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

