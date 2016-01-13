package service;

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
import controllers.HashtagController;
import controllers.MeepController;
import db.DBHelper;
import model.Comment;
import model.Meep;
import org.bson.Document;
import org.bson.types.ObjectId;
import twitter_feeder.Main;
import utils.Parser;
import utils.Validator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import static com.mongodb.client.model.Projections.include;
import static spark.Spark.*;

/**
 * Created by santiagomarti on 12/11/15.
 */
public class Server {

    //static MongoClient client = new MongoClient("54.94.252.8", 27017);
    static MongoClient client  = new MongoClient("dbmeep", 27017);
    static MongoDatabase database = client.getDatabase("local");
    static MongoCollection<Document> meepCol = database.getCollection("meeps");

    public static void main(String[] args) {

        DBHelper.init(meepCol);

        /***********************************/
        /**                               **/
        /**                               **/
        /**              GET              **/
        /**                               **/
        /**                               **/
        /***********************************/


        get("/", (request, response) -> "WeMeep Meep Service");

        /***********************************/
        /**              Meep             **/
        /***********************************/

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
                response.body(Parser.cleanMeepJson(aux));
            return response.body();
        });

        /***********************************/
        /**             Comments          **/
        /***********************************/

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
                    Comment aux = Parser.parseComment(docAux.toJson(), true);
                    ret2.add(JsonBuilder.buildJsonComment(aux));
                }
                index++;
            }
            response.body(ret2.toString() + "\n");
            return response.body();
        });

        /***********************************/
        /**              Meeps            **/
        /***********************************/

        get("/meeps", (request1, response1) -> {
            double km;
            double lat, longi;
            String idSt = null;
            boolean secret = false;
            try {
                Map<String, String> data = Parser.splitQuery(request1.queryString());
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
                response1.body(red.toString() + "\n");
                return response1.body();
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
            response1.body(ret.toString() + "\n");
            return response1.body();
        });


        /***********************************/
        /**           Registrees          **/
        /***********************************/


        get("/meeps/:id/registrees", (request, response) -> {
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
            return response.body();
        });

        /***********************************/
        /**           Receipts            **/
        /***********************************/

        get("/meeps/:id/receipts", (request, response) -> {
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
            return response.body();
        });

        /***********************************/
        /**            Search             **/
        /***********************************/

        get("/searchmeep", (request1, response1) -> {
            String query = null;
            double km;
            double lat, longi;
            try {
                Map<String, String> data = Parser.splitQuery(request1.queryString());
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
                response1.body(red.toString() + "\n");
                return response1.body();
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
            response1.body(ret.toString() + "\n");
            return response1.body();
        });

        /***********************************/
        /**             Seed              **/
        /***********************************/

        get("/seed", (request, response) -> {
            Runnable r = () -> {
                Main main = new Main();
                main.init(meepCol);
            };
            new Thread(r).start();
            response.body("Received");
            return response.body();
        });


        /***********************************/
        /**                               **/
        /**                               **/
        /**              POST             **/
        /**                               **/
        /**                               **/
        /***********************************/

        /***********************************/
        /**              Meep             **/
        /***********************************/

        //Create a meep
        post("/meeps", (request, response) -> {
            Meep obj = Parser.parseMeep(request.body(), false);
            JsonObject res = new JsonObject();
            if(!Validator.validateMeep(obj)){
                res.addProperty("Error", "Missing fields");
                response.body(res.toString() + "\n");
                return response.body();
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
            return response.body();
        });


        /***********************************/
        /**              Comments         **/
        /***********************************/

        //Add a meep comment
        post("/meeps/:id/comments", (request, response) -> {
            String id = request.params(":id");
            JsonObject res = new JsonObject();
            Comment ob = Parser.parseComment(request.body(), false);
            if (!Validator.validateComment(ob)) {
                res.addProperty("Error", "Missing fields");
                response.body(res.toString());
                return response.body();
            }
            HashtagController hashtagController = new HashtagController();
            ob = hashtagController.extractHashtags(ob);
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
                String createdAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
                JsonObject res2 = new JsonObject();
                res2.addProperty("Success", true);
                res2.addProperty("createdAt", createdAt);
                res2.addProperty("id", comment.get("_id").toString());
                response.body(res2.toString() + "\n");
            }
            return response.body();
        });

        /***********************************/
        /**                               **/
        /**                               **/
        /**              PUT              **/
        /**                               **/
        /**                               **/
        /***********************************/


        /***********************************/
        /**            Registrees         **/
        /***********************************/


        put("/meeps/:id/registrees", (request, response) -> {
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
            return response.body();
        });
    }
}
