package com.bakingapp;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Created by aditya on 6/29/17.
 */

public class WidgetUpdateService extends IntentService {

    public static final String ACTION_UPDATE_LISTVIEW = "com.bakingapp.updatelistview";

    public WidgetUpdateService(){
        super("WidgetUpdateService");
    }
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if(intent!=null){
            final String action = intent.getAction();
            if(ACTION_UPDATE_LISTVIEW.equals(action)){
                updateView();
            }
        }
    }

    public void updateView(){
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, RecipeWidgetProvider.class));

        Intent ingredientIntent = new Intent(this, ListWidgetService.class);
        ingredientIntent.putExtra("ingredients", true);
        //ingredientIntent.putExtra("pos", position);

        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.lvWidget);
    }
}
