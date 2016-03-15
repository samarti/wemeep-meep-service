package model;

import com.google.gson.JsonObject;

/**
 * Created by Santiago Martí Olbrich (samarti@uc.cl) on //.
 * Resit SpA.
 * All rights reserved
 */
public enum Category {

    ACTIVE_LIFE("Active Life", 1),
    ARTS_ENTER("Arts & Entertainment", 2),
    AUTOMOTIVE("Automotive",3),
    BEAUTY_SPA("Beauty & Spas",4),
    BICYCLES("Bicycles",5),
    EDUCATION("Education", 6),
    EVENT_PLANNING("Event Planning & Services", 7),
    FINANCIAL_SERVICES("Financial Services", 8),
    FOOD("Food", 9),
    HEALTH_MEDICAL("Health & Medical", 10),
    HOME_SERVICES("Home Services", 11),
    HOTELS("Hotels & Travel", 12),
    LOCAL_SERVICES("Local Services", 13),
    MASS_MEDIA("Mass Media", 14),
    NIGHTLIFE("Nightlife", 15),
    PETS("Pets", 16),
    PROFFS_SERVICES("Professional Services", 17),
    PUBLIC_SERVICES("Public Services & Government", 18),
    REAL_STATE("Real Estate", 19),
    RELIGIOUS_ORGANIZATIONS("Religious Organizations", 20),
    RESTAURANTS("Restaurants", 21),
    SHOPPING("Shopping", 22),
    TWITTER("Twitter", 23),
    OTHER("Other", 24);

    private final String name;

    private final int id;

    Category(String name, int id){
        this.name = name;
        this.id = id;
    }

    @Override
    public String toString() {
        return name;
    }

    public JsonObject toJsonObject(){
        JsonObject ret = new JsonObject();
        ret.addProperty("name", name);
        ret.addProperty("id", id);
        return ret;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }
}
