package controllers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import model.Category;
import model.Meep;
import org.bson.Document;
import org.bson.types.ObjectId;

import javax.print.Doc;
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
                Document auxEntry = new Document();
                auxEntry.append("id", el.getAsJsonObject().get("id").getAsString());
                auxEntry.append("type", el.getAsJsonObject().get("type").getAsString());
                aux = col.findOneAndUpdate(aux, new Document(add ? "$push" : "$pull", new Document("registrees", auxEntry)), new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER));
            }
            return true;
        }
    }

    public static boolean updateMeep(MongoCollection<Document> col, String meepId, Meep meep){
        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(meepId));
        FindIterable<Document> dbObj = col.find(query);
        Document aux = dbObj.first();
        if (aux == null) {
            return false;
        } else {
            if(meep.pictureUrl != null)
                col.updateOne(aux, new Document("$set", new Document("pictureUrl", meep.pictureUrl)));
            if(meep.message != null)
                col.updateOne(aux, new Document("$set", new Document("message", meep.message)));
            return true;
        }
    }

    /**
     * Adds the sender profile picture, from the Users Service, to a meep or comment
     * document.
     * @param meep
     * @return
     */
    public static Document addSenderPictureToMeepDocument(Document meep){
        UserController controller = new UserController();
        if(meep.getInteger("categoryId") == Category.TWITTER.getId()){
            if(meep.get("twitterUserPicture") != null){
                String pictureUrl = meep.getString("twitterUserPicture");
                if(pictureUrl != null)
                    meep.put("senderPictureUrl", pictureUrl);
            }
        } else {
            String senderId = meep.getString("senderId");
            if (senderId != null) {
                String pictureUrl = controller.getUserProfilePictureUrl(senderId);
                if (pictureUrl != null)
                    meep.append("senderPictureUrl", pictureUrl);
            }
        }
        return meep;
    }

    /**
     * Adds the sender profile picture, from the Users Service, to a meep or comment
     * document.
     * @param meep
     * @return
     */
    public static JsonObject addSenderPictureToMeepJson(JsonObject meep){
        UserController controller = new UserController();
        String senderId = meep.get("senderId").getAsString();
        if(meep.get("categoryId") != null && meep.get("categoryId").getAsInt() == Category.TWITTER.getId() && meep.get("twitterUserPicture") != null){
            String pictureUrl = meep.get("twitterUserPicture").getAsString();
            if(pictureUrl != null)
                meep.addProperty("senderPictureUrl", pictureUrl);
        } else if(senderId != null) {
            String pictureUrl = controller.getUserProfilePictureUrl(senderId);
            if(pictureUrl != null)
                meep.addProperty("senderPictureUrl", pictureUrl);
        }
        return meep;
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
