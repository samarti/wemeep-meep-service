package utils;

import com.google.gson.Gson;
import model.Meep;

/**
 * Created by santiagomarti on 12/14/15.
 */
public class Parser {

    private static Gson gson = new Gson();

    public static Meep parseMeep(String json){
        return gson.fromJson(json, Meep.class);
    }
}
