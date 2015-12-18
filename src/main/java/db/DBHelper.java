package db;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.util.JSON;
import org.bson.Document;
import org.bson.conversions.Bson;


/**
 * Created by santiagomarti on 12/18/15.
 */
public class DBHelper {

    public static void init(MongoCollection<Document> col){
        DBObject dbObj = new BasicDBObject();
        dbObj.put("location", "2dsphere");
        String json = JSON.serialize(dbObj);
        Bson bson = (Bson) JSON.parse(json);
        col.createIndex(bson);
    }
}
