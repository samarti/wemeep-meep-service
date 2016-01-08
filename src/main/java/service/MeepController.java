package service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import org.bson.Document;
import org.bson.types.ObjectId;

/**
 * Created by santiagomarti on 1/4/16.
 */
public class MeepController {

    public static boolean updateMeepRegistree(MongoCollection<Document> col, String meepId, JsonArray registrees, boolean add){
        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(meepId));
        FindIterable<Document> dbObj = col.find(query);
        Document aux = dbObj.first();
        if (aux == null) {
            return false;
        } else {
            for(JsonElement el : registrees){
                aux = col.findOneAndUpdate(aux, new Document(add ? "$push" : "$pull", new Document("registrees", new Document("id", el.getAsJsonObject().get("id").getAsString()))), new FindOneAndUpdateOptions());
            }
            return true;
        }
    }

    public static void addMeepsReceipts(MongoCollection<Document> col, String meepId, JsonArray receipts){
        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(meepId));
        FindIterable<Document> dbObj = col.find(query);
        Document aux = dbObj.first();
        if (aux != null) {
            for(JsonElement el : receipts){
                aux = col.findOneAndUpdate(aux, new Document("$push", new Document("receipts", new Document("id", el.getAsJsonObject().get("id").getAsString()))), new FindOneAndUpdateOptions());
            }
        }
    }
}
