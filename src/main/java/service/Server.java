package service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mongodb.BasicDBList;
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

import javax.print.Doc;
import java.util.Map;

import static spark.Spark.get;
import static spark.Spark.post;

/**
 * Created by santiagomarti on 12/11/15.
 */
public class Server {

    //static MongoClient client = new MongoClient("192.168.99.100", 27017);
    static MongoClient client = new MongoClient("dbmeeps", 27017);
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
            if(aux == null)
                response.body("Meep not found\n");
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
                response.body("Meep not found\n");
            else {
                response.body((aux.get("comments")).toString() + "\n");
            }
            return response.body();
        });


        get("/meeps", (request1, response1) -> {
            int km;
            double lat, longi;
            Map<String, String> data = Parser.splitQuery(request1.queryString());
            try {
                km = Integer.parseInt(data.get("radius"));
                lat = Double.parseDouble(data.get("lat"));
                longi = Double.parseDouble(data.get("long"));
                if(km <= 0)
                    throw new Exception();
            } catch (Exception e){
                response1.body("Bad arguments. Please provide radius, lat and long\n");
                return response1.body();
            }

            BasicDBObject jobj3 = QueryBuilder.getMeepOnRangeQuery(lat, longi, km);
            JsonArray ret = new JsonArray();
            try (MongoCursor<Document> cursor = meepCol.find(jobj3).iterator()) {
                while (cursor.hasNext()) {
                    ret.add(cursor.next().toJson());
                }
            }
            response1.body(ret.toString() + "\n");
            return response1.body();
        });

        //Create a meep
        post("/meeps", (request, response) -> {
            Meep obj = Parser.parseMeep(request.body());
            Document meep = DocumentBuilder.meepDocumentBuilder(obj, true);
            meepCol.insertOne(meep);
            JsonObject res = new JsonObject();
            res.addProperty("id", meep.getObjectId("_id").toString());
            response.body(res.toString() + "\n");
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
