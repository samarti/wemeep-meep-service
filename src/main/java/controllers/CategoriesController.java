package controllers;

import com.google.gson.JsonArray;
import model.Category;

/**
 * Created by Santiago Martí Olbrich (samarti@uc.cl) on 3/15/16.
 * Resit SpA.
 * All rights reserved
 */
public class CategoriesController {

    /**
     * Tells if a category id is valid
     * @param id
     * @return
     */
    public boolean isCategoryValid(int id){
        return id <= Category.values().length && id != 0;
    }

    /**
     * Returns a category object, given a valid id
     * @param id
     * @return
     */
    public Category getCategory(int id){
        if(id >= 0 && id < Category.values().length)
            return Category.values()[id];
        else
            return null;
    }

    /**
     * Get all Categories wrapped in a {@link JsonArray}
     * @return
     */
    public JsonArray getAsJsonArray(){
        JsonArray ret = new JsonArray();
        for(Category cat : Category.values()){
            ret.add(cat.toJsonObject());
        }
        return ret;
    }
}
