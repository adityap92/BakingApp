package com.bakingapp.data;

import ckm.simple.sql_provider.UpgradeScript;
import ckm.simple.sql_provider.annotation.ProviderConfig;
import ckm.simple.sql_provider.annotation.SimpleSQLConfig;

/**
 * Class for setting up DB using SimpleSQLProvider
 * Created by aditya on 6/29/17.
 */

@SimpleSQLConfig(
        name = "RecipeProvider",
        authority = "com.bakingapp.authority",
        database = "recipe.db",
        version =1
)

public class RecipeProviderConfig implements ProviderConfig {
    @Override
    public UpgradeScript[] getUpdateScripts() {
        return new UpgradeScript[0];
    }
}
