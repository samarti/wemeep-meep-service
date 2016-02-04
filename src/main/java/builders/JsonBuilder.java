package builders;

import com.google.gson.JsonObject;
import model.Comment;
import model.Meep;
import org.bson.types.ObjectId;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by santiagomarti on 1/13/16.
 */
public class JsonBuilder {

    public static JsonObject buildJsonComment(Comment aux){
        JsonObject aux2 = new JsonObject();
        aux2.addProperty("message", aux.message);
        aux2.addProperty("senderName", aux.senderName);
        aux2.addProperty("senderId", aux.senderId);
        aux2.addProperty("createdAt", aux.createdAt);
        aux2.addProperty("updatedAt", aux.updatedAt);
        aux2.addProperty("id", aux.objectId);
        aux2.addProperty("type", aux.type);
        return aux2;
    }

    public static JsonObject buildJsonMeep(Meep aux, ObjectId id){
        JsonObject aux2 = new JsonObject();
        aux2.addProperty("message", aux.message);
        aux2.addProperty("senderName", aux.senderName);
        aux2.addProperty("objectId", id.toHexString());
        String createdAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(id.getTimestamp() * 1000L));
        aux2.addProperty("createdAt", createdAt);
        aux2.addProperty("updatedAt", createdAt);
        aux2.addProperty("isPublic", aux.isPublic);
        aux2.addProperty("latitude", aux.lat);
        aux2.addProperty("longitude", aux.longi);
        aux2.addProperty("senderId", aux.senderId);
        aux2.addProperty("likeCounter", aux.likeCounter);
        aux2.addProperty("viewCounter", aux.viewCounter);
        aux2.addProperty("commentCounter", aux.commentCounter);
        return aux2;
    }
}
