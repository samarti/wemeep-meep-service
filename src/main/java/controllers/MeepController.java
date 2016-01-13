package controllers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.LinkedList;

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
                aux = col.findOneAndUpdate(aux, new Document(add ? "$push" : "$pull", new Document("registrees", new Document("id", el.getAsJsonObject().get("id").getAsString()))), new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER));
            }
            return true;
        }
    }

    public static void insertMeepReceipts(MongoCollection<Document> col, String meepId, JsonArray receipts){
        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(meepId));
        FindIterable<Document> dbObj = col.find(query);
        Document aux = dbObj.first();
        if (aux != null) {
            for(JsonElement el : receipts){
                aux = col.findOneAndUpdate(aux, new Document("$push", new Document("receipts", new Document("id", el.getAsJsonObject().get("id").getAsString()))), new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER));
            }
        }
    }

    public static void insertMeepHashtags(MongoCollection<Document> col, String meepId, LinkedList<String> hashtags){
        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(meepId));
        FindIterable<Document> dbObj = col.find(query);
        Document aux = dbObj.first();
        if (aux != null) {
            for(String el : hashtags){
                aux = col.findOneAndUpdate(aux, new Document("$push", new Document("hashtags", el)), new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER));
            }
        }
    }
}
