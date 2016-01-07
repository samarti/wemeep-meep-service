package utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import model.Circle;
import model.Comment;
import model.Meep;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    public static Comment parseComment(String json, boolean containsId){
        Comment ret = gson.fromJson(json, Comment.class);
        if(containsId){
            JsonParser parser = new JsonParser();
            JsonObject original = parser.parse(json).getAsJsonObject();
            ret.objectId = original.getAsJsonObject("_id").get("$oid").getAsString();
        }
        return ret;
    }

    public static String cleanMeepJson(Document doc){
        String json = doc.toJson();
        JsonParser parser = new JsonParser();
        JsonObject original = parser.parse(json).getAsJsonObject();
        original.add("objectId", original.getAsJsonObject("_id").get("$oid"));
        original.remove("_id");
        original.addProperty("latitude", original.getAsJsonObject("location").getAsJsonArray("coordinates").get(1).getAsDouble());
        original.addProperty("longitude", original.getAsJsonObject("location").getAsJsonArray("coordinates").get(0).getAsDouble());
        original.remove("location");
        original.remove("receipts");
        original.remove("comments");
        original.remove("registrees");
        ObjectId objId = (ObjectId) doc.get("_id");
        String createdAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(objId.getTimestamp() * 1000L));
        original.addProperty("createdAt", createdAt);
        return original.toString();
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
