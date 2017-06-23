package com.bakingapp;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class to hold recipe data
 *
 * Created by aditya on 6/20/17.
 */

public class Recipe implements Serializable{

    private int id;
    private String name;
    private ArrayList<Ingredient> ingredients;
    private ArrayList<Step> steps;
    private int servings;

    public Recipe(int id, String name, int servings){
        this.id = id;
        this.name = name;
        this.servings = servings;
        this.ingredients = new ArrayList<Ingredient>();
        this.steps = new ArrayList<Step>();
    }

    public String getServings() {
        return " " + String.valueOf(servings);
    }

    public String getName(){
        return name;
    }

    /**
     * add ingredients to Recipe
     * @param quant
     * @param meas
     * @param iName
     */
    public void addIngredient(int quant, String meas, String iName){
        this.ingredients.add(new Ingredient(quant,meas,iName));
    }

    /**
     * add steps to Recipe
     * @param id
     * @param sDesc
     * @param d
     * @param vUrl
     * @param thumb
     */
    public void addStep(int id, String sDesc, String d, String vUrl, String thumb){
        this.steps.add(new Step(id, sDesc, d, vUrl, thumb));
    }


    private class Ingredient implements Serializable{

        private int quantity;
        private String measure;
        private String ingredientName;

        public Ingredient(int quantity, String measure, String ingredientName){
            this.quantity = quantity;
            this.measure = measure;
            this.ingredientName = ingredientName;
        }

    }

    private class Step implements Serializable{
        private int stepId;
        private String shortDesc;
        private String desc;
        private String videoUrl;
        private String thumbnail;

        public Step(int stepId, String shortDesc,String desc, String videoUrl, String thumbnail){
            this.stepId = stepId;
            this.shortDesc = shortDesc;
            this.desc = desc;
            this.videoUrl = videoUrl;
            this.thumbnail = thumbnail;
        }
    }
}
