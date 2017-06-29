package com.bakingapp.data;

import ckm.simple.sql_provider.annotation.SimpleSQLColumn;
import ckm.simple.sql_provider.annotation.SimpleSQLTable;

/**
 * Class for setting up DB using SimpleSQLProvider
 * Created by aditya on 6/29/17.
 */

@SimpleSQLTable(table = "recipe", provider = "RecipeProvider")
public class RecipeIngredients {

    @SimpleSQLColumn(value = "col_id", primary = true, autoincrement = true)
    public int id;
    @SimpleSQLColumn("col_name")
    public String recipeName;
    @SimpleSQLColumn("col_ingredient")
    public String recipeIngredient;

}
