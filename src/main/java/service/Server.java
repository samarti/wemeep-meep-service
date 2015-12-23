package service;

import com.google.gson.JsonArray;
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
import model.Meep;
import org.bson.Document;
import org.bson.types.ObjectId;
import utils.DocumentBuilder;
import utils.Parser;
import utils.QueryBuilder;
import utils.Validator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import static spark.Spark.get;
import static spark.Spark.post;

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
                response.body(aux.toJson());
            return response.body();
        });

        //Get a meep comments
        get("/meeps/:id/comments", (request, response) -> {
            double limit, offset;
            try {
                Map<String, String> data = Parser.splitQuery(request.queryString());
                limit = Integer.parseInt(data.get("limit"));
                offset = Double.parseDouble(data.get("offset"));
                if(limit <= 0 || offset <= 0 || limit > 100)
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
            FindIterable<Document> dbObj = meepCol.find(query);
            Document aux = dbObj.first();
            JsonObject red = new JsonObject();
            red.addProperty("Error", "Meep not found");
            if(aux == null)
                response.body(red.toString() + "\n");
            else {
                JsonParser parser = new JsonParser();
                response.body((parser.parse(aux.get("comments").toString()).getAsJsonArray()) + "\n");
            }
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
                    aux2.addProperty("senderName", aux.sender);
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
            Document meep = DocumentBuilder.meepDocumentBuilder(obj, true);
            meepCol.insertOne(meep);
            res.addProperty("id", meep.getObjectId("_id").toString());
            response.body(res.toString() + "\n");
            return response.body();
        });

        //Add a meep comment
        post("/meeps/:id/comments", (request, response) -> {
            String id = request.params(":id");
            Meep obj = Parser.parseMeep(request.body(), false);
            Document meep = DocumentBuilder.meepDocumentBuilder(obj, false);
            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(id));
            FindIterable<Document> dbObj = meepCol.find(query);
            Document aux = dbObj.first();
            if (aux == null)
                response.body("Meep not found\n");
            else {
                meepCol.findOneAndUpdate(aux, new Document("$push", new Document("comments", meep)), new FindOneAndUpdateOptions());
                JsonObject res = new JsonObject();
                res.addProperty("success", true);
                response.body(res.toString() + "\n");
            }
            return response.body();
        });



    }
}
