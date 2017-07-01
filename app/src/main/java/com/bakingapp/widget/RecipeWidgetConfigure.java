package com.bakingapp.widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.widget.RemoteViews;

import com.bakingapp.R;
import com.bakingapp.Recipe;
import com.bakingapp.data.RecipeIngredients;
import com.bakingapp.data.RecipeTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aditya on 6/30/17.
 */

public class RecipeWidgetConfigure extends Activity {

    private int mAppWidgetId = 0;
    public Context mContext;
    List<Recipe> widgetRecipes;
    List<RecipeIngredients> recipes;
    ArrayList<String> names;
    Intent returnIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget_config);

        mContext = getApplicationContext();

        returnIntent = new Intent(mContext, ListWidgetService.class);

        //read Recipes from DB
        getRecipes();

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        buildDialog();

    }

    public void buildDialog(){
        CharSequence[] seq = new CharSequence[names.size()];

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.choose_recipe)
                .setSingleChoiceItems(names.toArray(seq), 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int pos) {

                        //pass position in list
                        returnIntent.putExtra("pos",pos);

                    }
                })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);

                        RemoteViews views = new RemoteViews(mContext.getPackageName(),
                                R.layout.recipe_widget_provider);
                        views.setRemoteAdapter(R.id.lvWidget,returnIntent);

                        appWidgetManager.updateAppWidget(mAppWidgetId, views);

                        Intent resultValue = new Intent();
                        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                        setResult(RESULT_OK, resultValue);
                        finish();

                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {


                    Intent resultValue = new Intent();
                    resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                    setResult(RESULT_CANCELED, resultValue);
                    finish();
                }
        });
        builder.create().show();
    }

    public void getRecipes(){
        Cursor cursor=null;
        cursor = mContext.getContentResolver().query(RecipeTable.CONTENT_URI,null,null,null,null);
        recipes = RecipeTable.getRows(cursor,true);
        cursor.close();

        setupLists();
    }
    public void setupLists(){

        names = new ArrayList<String>();
        widgetRecipes = new ArrayList<Recipe>();
        for(RecipeIngredients ri : recipes){
            if(!names.contains(ri.recipeName)) {
                names.add(ri.recipeName);
                widgetRecipes.add(new Recipe(0, ri.recipeName, 0));
            }
            widgetRecipes.get(names.indexOf(ri.recipeName)).addIngredient(0,"",ri.recipeIngredient);
        }
    }

}
