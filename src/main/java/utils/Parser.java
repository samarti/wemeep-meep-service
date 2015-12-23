package utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import model.Circle;
import model.Meep;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by santiagomarti on 12/14/15.
 */
public class Parser {

    private static Gson gson = new Gson();

    public static Meep parseMeep(String json, boolean inserting){
        Meep ret = gson.fromJson(json, Meep.class);
        if(inserting){
            JsonParser parser = new JsonParser();
            JsonObject obj = parser.parse(json).getAsJsonObject();
            JsonArray obs = obj.getAsJsonObject("location").getAsJsonArray("coordinates");
            ret.lat = obs.get(1).getAsDouble();
            ret.longi = obs.get(0).getAsDouble();
        }
        return ret;
    }

    public static Circle parseCircle(String json){
        return gson.fromJson(json, Circle.class);
    }

    public static Map<String, String> splitQuery(String query) throws UnsupportedEncodingException {
        Map<String, String> query_pairs = new LinkedHashMap<>();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
        }
        return query_pairs;
    }
}
