package com.bakingapp.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.bakingapp.R;
import com.bakingapp.Recipe;
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
        int pos = intent.getIntExtra("pos",0);
        return (new ListRemoteViewsFactory(mContext,
                pos));
    }
}

class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    Context mContext;
    List<RecipeIngredients> recipes;
    List<Recipe> widgetRecipes;
    int pos;


        public ListRemoteViewsFactory(Context applicationContext, int position){
            mContext = applicationContext;
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
            return widgetRecipes.get(pos).getIngredients().size();
        }

        @Override
        public RemoteViews getViewAt(int position) {

            RemoteViews views = new RemoteViews(mContext.getPackageName(),
                    R.layout.widget_list_item);

                views.setTextViewText(R.id.tvWidgetItem,
                        widgetRecipes.get(pos).getIngredients().get(position).getIngredientName());
                views.setViewVisibility(R.id.bWidgetPrev, View.VISIBLE);


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

