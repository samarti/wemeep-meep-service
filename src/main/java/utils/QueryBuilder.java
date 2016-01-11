package utils;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * Created by santiagomarti on 12/18/15.
 */
public class QueryBuilder {

    public static BasicDBObject getMeepOnRangeQuery(double lat, double longi, int radius, boolean secret, String id){
        BasicDBList list1 = new BasicDBList();
        list1.add(longi);
        list1.add(lat);
        BasicDBList list2 = new BasicDBList();
        list2.add(list1);
        list2.add(radius / 6378.15);
        BasicDBObject jobj = new BasicDBObject();
        jobj.append("$centerSphere", list2);
        BasicDBObject jobj2 = new BasicDBObject();
        jobj2.append("$geoWithin", jobj);
        BasicDBObject jobj3 = new BasicDBObject();
        jobj3.append("location",jobj2);
        jobj3.append("isPublic", secret);
        if(secret){
            BasicDBObject jobj4 = new BasicDBObject();
            BasicDBObject[] list = new BasicDBObject[1];
            list[1] = new BasicDBObject();
            list[1].put("id", id);
            jobj4.append("$in", list);
            jobj3.append("receipts", jobj4);
        }
        return jobj3;
    }
}
