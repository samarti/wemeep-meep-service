package service;

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
import db.DBHelper;
import model.Comment;
import model.Meep;
import org.bson.Document;
import org.bson.types.ObjectId;
import utils.DocumentBuilder;
import utils.Parser;
import utils.QueryBuilder;
import utils.Validator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import static spark.Spark.get;
import static spark.Spark.post;

import static com.mongodb.client.model.Projections.*;
/**
 * Created by santiagomarti on 12/11/15.
 */
public class Server {

    //static MongoClient client = new MongoClient("192.168.99.100", 27017);
    static MongoClient client = new MongoClient("dbmeep", 27017);
    static MongoDatabase database = client.getDatabase("local");
    static MongoCollection<Document> meepCol = database.getCollection("meeps");

    public static void main(String[] args) {

        DBHelper.init(meepCol);

        get("/", (request, response) -> "WeMeep Meep Service");

        get("/meeps/:id", (request, response) -> {
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
                response.body(Parser.cleanMeepJson(aux.toJson()));
            return response.body();
        });

        //Get a meep comments
        get("/meeps/:id/comments", (request, response) -> {
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
                return response.body();
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
                    Comment aux = Parser.parseComment(docAux.toJson());
                    JsonObject aux2 = new JsonObject();
                    aux2.addProperty("message", aux.message);
                    aux2.addProperty("senderName", aux.senderName);
                    aux2.addProperty("senderId", aux.senderId);
                    aux2.addProperty("createdAt", aux.createdAt);
                    aux2.addProperty("updatedAt", aux.updatedAt);
                    ret2.add(aux2.getAsJsonObject());
                }
                index++;
            }
            response.body(ret2.toString() + "\n");
            return response.body();
        });


        get("/meeps", (request1, response1) -> {
            int km;
            double lat, longi;
            try {
                Map<String, String> data = Parser.splitQuery(request1.queryString());
                km = Integer.parseInt(data.get("radius"));
                lat = Double.parseDouble(data.get("lat"));
                longi = Double.parseDouble(data.get("longi"));
                if(km <= 0)
                    throw new Exception();
            } catch (Exception e){
                JsonObject red = new JsonObject();
                red.addProperty("Error", "Bad arguments. Please provide radius, lat and longi");
                response1.body(red.toString() + "\n");
                return response1.body();
            }

            BasicDBObject jobj3 = QueryBuilder.getMeepOnRangeQuery(lat, longi, km);
            JsonArray ret = new JsonArray();
            try (MongoCursor<Document> cursor = meepCol.find(jobj3).iterator()) {
                while (cursor.hasNext()) {
                    Document docAux = cursor.next();
                    ObjectId id = (ObjectId) docAux.get("_id");
                    Meep aux = Parser.parseMeep(docAux.toJson(), true);
                    JsonObject aux2 = new JsonObject();
                    aux2.addProperty("message", aux.message);
                    aux2.addProperty("senderName", aux.senderName);
                    aux2.addProperty("objectId", id.toHexString());
                    String createdAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(id.getTimestamp() * 1000L));
                    aux2.addProperty("createdAt", createdAt);
                    aux2.addProperty("updatedAt", createdAt);
                    aux2.addProperty("public", aux.isPublic);
                    aux2.addProperty("latitude", aux.lat);
                    aux2.addProperty("longitude", aux.longi);
                    ret.add(aux2.getAsJsonObject());
                }
            }
            response1.body(ret.toString() + "\n");
            return response1.body();
        });

        //Create a meep
        post("/meeps", (request, response) -> {
            Meep obj = Parser.parseMeep(request.body(), false);
            JsonObject res = new JsonObject();
            if(!Validator.validateMeep(obj)){
                res.addProperty("Error", "Missing fields");
                response.body(res.toString() + "\n");
                return response.body();
            }
            Document meep = DocumentBuilder.meepDocumentBuilder(obj);
            meepCol.insertOne(meep);
            ObjectId id = (ObjectId) meep.get("_id");
            String createdAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(id.getTimestamp() * 1000L));
            res.addProperty("id", meep.getObjectId("_id").toString());
            res.addProperty("createdAt", createdAt);
            response.body(res.toString() + "\n");
            return response.body();
        });

        //Add a meep comment
        post("/meeps/:id/comments", (request, response) -> {
            String id = request.params(":id");
            JsonObject res = new JsonObject();
            Comment ob = Parser.parseComment(request.body());
            if (!Validator.validateComment(ob)) {
                res.addProperty("Error", "Missing fields");
                response.body(res.toString());
                return response.body();
            }
            Document comment = DocumentBuilder.commentDocumentBuilder(ob);
            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(id));
            FindIterable<Document> dbObj = meepCol.find(query);
            Document aux = dbObj.first();
            if (aux == null) {
                res.addProperty("Error", "Meep not found");
                response.body(res.toString());
            } else {
                meepCol.findOneAndUpdate(aux, new Document("$push", new Document("comments", comment)), new FindOneAndUpdateOptions());
                JsonObject res2 = new JsonObject();
                res2.addProperty("Success", true);
                response.body(res2.toString() + "\n");
            }
            return response.body();
        });
    }
}
