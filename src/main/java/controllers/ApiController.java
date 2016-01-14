package controllers;

import builders.DocumentBuilder;
import builders.JsonBuilder;
import builders.QueryBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import db.DBHelper;
import model.Comment;
import model.Meep;
import org.bson.Document;
import org.bson.types.ObjectId;
import spark.Request;
import spark.Response;
import twitter_feeder.Main;
import utils.Parser;
import utils.Validator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import static com.mongodb.client.model.Projections.include;

/**
 * Created by santiagomarti on 1/14/16.
 */
public class ApiController {

    //static MongoClient client = new MongoClient("54.94.252.8", 27017);
    static MongoClient client  = new MongoClient("dbmeep", 27017);
    static MongoDatabase database = client.getDatabase("local");
    static MongoCollection<Document> meepCol = database.getCollection("meeps");

    public static void init(){
        DBHelper.init(meepCol);
    }
    /***********************************/
    /**                               **/
    /**              GET              **/
    /**                               **/
    /***********************************/

    public static Response getMeep(Response response, Request request){
        String id = request.params(":id");
        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(id));
        FindIterable<Document> dbObj = meepCol.find(query);
        Document aux = dbObj.first();
        JsonObject red = new JsonObject();
        red.addProperty("Error", "Meep not found");
        if(aux == null)
            response.body(red.toString() + "\n");
        else
            response.body(Parser.cleanMeepJson(aux));
        return response;
    }

    public static Response getComments(Response response, Request request){
        int limit, offset;
        try {
            Map<String, String> data = Parser.splitQuery(request.queryString());
            limit = Integer.parseInt(data.get("limit"));
            offset = Integer.parseInt(data.get("offset"));
            if(limit <= 0 || offset < 0 || limit > 100)
                throw new Exception();
        } catch (Exception e){
            JsonObject red = new JsonObject();
            red.addProperty("Error", "Bad arguments. Please provide limit and offset");
            response.body(red.toString() + "\n");
            return response;
        }

        String id = request.params(":id");
        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(id));
        BasicDBObject slicer = new BasicDBObject();
        slicer.append("$slice", new int[]{0, 5});
        BasicDBObject comments = new BasicDBObject();
        comments.put("comments", slicer);

        FindIterable<Document> dbObj = meepCol.find(query).projection(include("comments"));
        Document meepAux = dbObj.first();
        JsonParser parser = new JsonParser();
        JsonArray ret = parser.parse(meepAux.toJson()).getAsJsonObject().getAsJsonArray("comments");
        JsonArray ret2 = new JsonArray();
        Iterator<JsonElement> it = ret.iterator();
        //TODO Muy mejorable (hacer query solo de los comentarios pedidos, no pedirlos todos)
        int index = 0;
        while(it.hasNext()){
            JsonElement com = it.next();
            if(index >= offset && index < offset + limit){
                Document docAux = Document.parse(com.toString());
                Comment aux = Parser.parseComment(docAux.toJson(), true);
                ret2.add(JsonBuilder.buildJsonComment(aux));
            }
            index++;
        }
        response.body(ret2.toString() + "\n");
        return response;
    }

    public static Response getMeeps(Response response, Request request){
        double km;
        double lat, longi;
        String idSt = null;
        boolean secret;
        try {
            Map<String, String> data = Parser.splitQuery(request.queryString());
            km = Double.parseDouble(data.get("radius"));
            lat = Double.parseDouble(data.get("lat"));
            longi = Double.parseDouble(data.get("longi"));
            secret = Boolean.parseBoolean(data.get("secret"));
            if(secret) {
                if(!data.containsKey("id"))
                    throw new Exception();
                idSt = data.get("id");
            }
            if(km <= 0)
                throw new Exception();
        } catch (Exception e){
            JsonObject red = new JsonObject();
            red.addProperty("Error", "Bad arguments. Please provide radius, lat, longi and \"secret\":true|false. For secret meeps add id also.");
            response.body(red.toString() + "\n");
            return response;
        }

        BasicDBObject jobj3 = QueryBuilder.getMeepOnRangeQuery(lat, longi, km, secret, idSt);
        JsonArray ret = new JsonArray();
        try (MongoCursor<Document> cursor = meepCol.find(jobj3).iterator()) {
            while (cursor.hasNext()) {
                Document docAux = cursor.next();
                ObjectId id = (ObjectId) docAux.get("_id");
                Meep aux = Parser.parseMeep(docAux.toJson(), true);
                ret.add(JsonBuilder.buildJsonMeep(aux, id));
            }
        }
        response.body(ret.toString() + "\n");
        return response;
    }

    public static Response getRegistrees(Response response, Request request){
        String id = request.params(":id");
        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(id));
        FindIterable<Document> dbObj = meepCol.find(query);
        Document aux = dbObj.first();
        JsonObject red = new JsonObject();
        red.addProperty("Error", "Meep not found");
        if(aux == null)
            response.body(red.toString() + "\n");
        else {
            JsonParser parser = new JsonParser();
            JsonArray ret = parser.parse(aux.toJson()).getAsJsonObject().getAsJsonArray("registrees");
            response.body(ret.toString());
        }
        return response;
    }

    public static Response getReceipts(Response response, Request request){
        String id = request.params(":id");
        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(id));
        FindIterable<Document> dbObj = meepCol.find(query);
        Document aux = dbObj.first();
        JsonObject red = new JsonObject();
        red.addProperty("Error", "Meep not found");
        if (aux == null)
            response.body(red.toString() + "\n");
        else {
            JsonParser parser = new JsonParser();
            JsonArray ret = parser.parse(aux.toJson()).getAsJsonObject().getAsJsonArray("receipts");
            response.body(ret.toString());
        }
        return response;
    }

    public static Response searchMeep(Response response, Request request){
        String query = null;
        double km;
        double lat, longi;
        try {
            Map<String, String> data = Parser.splitQuery(request.queryString());
            if(!data.containsKey("query") || !data.containsKey("radius"))
                throw new Exception();
            query = data.get("query");
            km = Double.parseDouble(data.get("radius"));
            lat = Double.parseDouble(data.get("lat"));
            longi = Double.parseDouble(data.get("longi"));
            if(km < 0 || km > 50)
                throw new Exception();
        } catch (Exception e){
            JsonObject red = new JsonObject();
            red.addProperty("Error", "Bad arguments. Please provide query, lat , longi and radius <= 50 && > 0 in km");
            response.body(red.toString() + "\n");
            return response;
        }

        BasicDBObject jobj3 = QueryBuilder.getMeepsWithHashtagQuery(query, km, lat, longi);
        JsonArray ret = new JsonArray();
        try (MongoCursor<Document> cursor = meepCol.find(jobj3).iterator()) {
            while (cursor.hasNext()) {
                Document docAux = cursor.next();
                JsonObject jsAux = Parser.hardMeepClean(docAux);
                ret.add(jsAux);
            }
        }
        response.body(ret.toString() + "\n");
        return response;
    }

    public static Response seed(Response response, Request request){
        Runnable r = () -> {
            Main main = new Main();
            main.init(meepCol);
        };
        new Thread(r).start();
        response.body("Received");
        return response;
    }
    /***********************************/
    /**                               **/
    /**              POST             **/
    /**                               **/
    /***********************************/

    public static Response createMeep(Response response, Request request){
        Meep obj = Parser.parseMeep(request.body(), false);
        JsonObject res = new JsonObject();
        if(!Validator.validateMeep(obj)){
            res.addProperty("Error", "Missing fields");
            response.body(res.toString() + "\n");
            return response;
        }
        HashtagController hashtagController = new HashtagController();
        obj = hashtagController.extractHashtags(obj);
        Document meep = DocumentBuilder.meepDocumentBuilder(obj);
        meepCol.insertOne(meep);
        ObjectId id = (ObjectId) meep.get("_id");
        JsonParser parser = new JsonParser();
        JsonObject meepAux = parser.parse(request.body()).getAsJsonObject();
        if(meepAux.has("receipts")){
            MeepController.insertMeepReceipts(meepCol, id.toHexString(), meepAux.getAsJsonArray("receipts"));
        }
        String createdAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(id.getTimestamp() * 1000L));
        res.addProperty("id", meep.getObjectId("_id").toString());
        res.addProperty("createdAt", createdAt);
        response.body(res.toString() + "\n");
        return response;
    }

    public static Response createComment(Response response, Request request){
        String id = request.params(":id");
        JsonObject res = new JsonObject();
        Comment ob = Parser.parseComment(request.body(), false);
        if (!Validator.validateComment(ob)) {
            res.addProperty("Error", "Missing fields");
            response.body(res.toString());
            return response;
        }
        HashtagController hashtagController = new HashtagController();
        ob = hashtagController.extractHashtags(ob);
        Document comment = DocumentBuilder.commentDocumentBuilder(ob);
        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(id));
        FindIterable<Document> dbObj = meepCol.find(query);
        Document aux = dbObj.first();
        JsonParser parser = new JsonParser();
        if (aux == null) {
            res.addProperty("Error", "Meep not found");
            response.body(res.toString());
        } else {
            aux = meepCol.findOneAndUpdate(aux, new Document("$push", new Document("comments", comment)), new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER));
            if(ob.hashtags != null) {
                //TODO Si bien puede que sea rápido por el uso de hash set (O(1) en contains()), quizás se pueda mejorar
                JsonArray auxOb = parser.parse(aux.toJson()).getAsJsonObject().get("hashtags").getAsJsonArray();
                HashSet<String> htags = new HashSet<>();
                for(JsonElement e : auxOb)
                    htags.add(e.getAsString());
                for (String s : ob.hashtags) {
                    if(!htags.contains(s))
                        aux = meepCol.findOneAndUpdate(aux, new Document("$push", new Document("hashtags", s)), new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER));
                }
            }
            String createdAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
            JsonObject res2 = new JsonObject();
            res2.addProperty("Success", true);
            res2.addProperty("createdAt", createdAt);
            res2.addProperty("id", comment.get("_id").toString());
            response.body(res2.toString() + "\n");
        }
        return response;
    }

    /***********************************/
    /**                               **/
    /**              PUT              **/
    /**                               **/
    /***********************************/

    public static Response changeRegistryRelation(Response response, Request request){
        JsonParser parser = new JsonParser();
        JsonObject data = parser.parse(request.body()).getAsJsonObject();
        JsonObject red = new JsonObject();
        String id = request.params(":id");
        Boolean decider = false;
        if(data.get("ids") == null)
            red.addProperty("Error", "Ids missing");
        else
            switch (data.get("type").getAsString()){
                case "add":
                    decider = true;
                    break;
                case "remove":
                    decider = false;
                    break;
                default:
                    decider = null;
                    red.addProperty("Error", "Type not defined");
                    break;
            }
        if(decider != null) {
            boolean res = MeepController.updateMeepRegistree(meepCol, id, data.get("ids").getAsJsonArray(), decider);
            if (!res)
                red.addProperty("Error", "Meep not found");
            else
                red.addProperty("Success", "Registrees updated");
        }
        response.body(red.toString());
        return response;
    }

}
