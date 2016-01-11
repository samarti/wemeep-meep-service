package utils;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * Created by santiagomarti on 12/18/15.
 */
public class QueryBuilder {

    public static BasicDBObject getMeepOnRangeQuery(double lat, double longi, int radius){
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
        jobj3.append("isPublic", true);
        return jobj3;
    }
}
