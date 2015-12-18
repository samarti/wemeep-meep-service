package utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mongodb.BasicDBList;
import model.Meep;
import org.bson.Document;

/**
 * Created by santiagomarti on 12/14/15.
 */
public class DocumentBuilder {

    public static Document meepDocumentBuilder(Meep arg, boolean isRoot){
        Document meep = new Document();
        meep.append("sender", arg.sender);
        meep.append("message", arg.message);
        meep.append("type", arg.type);
        meep.append("facebookId", arg.facebookId);
        meep.append("twitterId", arg.twitterId);
        meep.append("isRoot", arg.isRoot);
        meep.append("picture", arg.picture);
        meep.append("updatedAt", "");
        meep.append("createdAt", "");

        JsonArray jsonArray = new JsonArray();
        jsonArray.add(new JsonPrimitive(arg.longi));
        jsonArray.add(new JsonPrimitive(arg.lat));
        JsonObject jobj = new JsonObject();
        jobj.addProperty("type", "point");
        jobj.add("coordinates", jsonArray);
        JsonObject jsonObject_loc = new JsonObject();
        jsonObject_loc.add("loc", jobj);

        meep.append("location", jobj.toString());
        if(isRoot){
            BasicDBList comments = new BasicDBList();
            meep.append("comments", comments);
            meep.append("receipts", arg.receipts.toString());
            meep.append("isPublic", arg.isPublic);
        }
        return meep;
    }
}
