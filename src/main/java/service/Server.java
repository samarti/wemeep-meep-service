package service;

import com.google.gson.JsonObject;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import model.Meep;
import org.bson.Document;
import org.bson.types.ObjectId;
import utils.DocumentBuilder;
import utils.Parser;

import static spark.Spark.get;
import static spark.Spark.post;

/**
 * Created by santiagomarti on 12/11/15.
 */
public class Server {

    static MongoClient client = new MongoClient("dbmeep", 27017);
    static MongoDatabase database = client.getDatabase("local");
    static MongoCollection<Document> meepCol = database.getCollection("meeps");

    public static void main(String[] args) {

        get("/", (request, response) -> "WeMeep Meep Service");

        get("/meeps/:id", (request, response) -> {
            String id = request.params(":id");
            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(id));
            FindIterable<Document> dbObj = meepCol.find(query);
            Document aux = dbObj.first();
            if(aux == null)
                response.body("Meep not found");
            else
                response.body(aux.toJson());
            return response.body();
        });

        //Get a meep comments
        get("/meeps/:id/comments", (request, response) -> {
            String id = request.params(":id");
            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(id));
            FindIterable<Document> dbObj = meepCol.find(query);
            Document aux = dbObj.first();
            if(aux == null)
                response.body("Meep not found");
            else {
                response.body((aux.get("comments")).toString());
            }
            return response.body();
        });

        //Create a meep
        post("/meeps", (request, response) -> {
            Meep obj = Parser.parseMeep(request.body());
            Document meep = DocumentBuilder.meepDocumentBuilder(obj, true);
            meepCol.insertOne(meep);
            JsonObject res = new JsonObject();
            res.addProperty("id", meep.getObjectId("_id").toString());
            response.body(res.toString());
            return response.body();
        });

        //Add a meep comment
        post("/meeps/:id/comments", (request, response) -> {
            String id = request.params(":id");
            Meep obj = Parser.parseMeep(request.body());
            Document meep = DocumentBuilder.meepDocumentBuilder(obj, false);
            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(id));
            FindIterable<Document> dbObj = meepCol.find(query);
            Document aux = dbObj.first();
            if(aux == null)
                response.body("Meep not found");
            else {
                meepCol.findOneAndUpdate(aux, new Document("$push", new Document("comments", meep)), new FindOneAndUpdateOptions());
                JsonObject res = new JsonObject();
                res.addProperty("success", true);
                response.body(res.toString());
            }
            return response.body();
        });



    }
}
