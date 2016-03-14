package builders;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

import java.util.regex.Pattern;

/**
 * Created by santiagomarti on 12/18/15.
 */
public class QueryBuilder {

    public static BasicDBObject getMeepOnRangeQuery(double lat, double longi, double radius, boolean secret, String id){
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
        jobj3.append("isPublic", !secret);
        if(secret){
            BasicDBObject jobj4 = new BasicDBObject();
            BasicDBObject[] list = new BasicDBObject[1];
            list[0] = new BasicDBObject();
            list[0].put("id", id);
            jobj4.append("$in", list);
            BasicDBObject jobj6 = new BasicDBObject();
            jobj6.append("receipts", jobj4);
            BasicDBObject[] jobj5 = new BasicDBObject[2];
            jobj5[0] = jobj6;
            BasicDBObject jobj7 = new BasicDBObject();
            jobj7.append("senderId", id);
            jobj5[1] = jobj7;
            jobj3.append("$or", jobj5);
        }
        return jobj3;
    }

    /**
     * Queries Meeps messages based on a string.
     * @param query
     * @param km
     * @param lat
     * @param longi
     * @return
     */
    public static BasicDBObject getMeepsWithRegexQuery(String query, double km, double lat, double longi){
        Pattern pat = Pattern.compile("^.*" + query + ".*", Pattern.CASE_INSENSITIVE);
        BasicDBObject distanceQuery = getMeepOnRangeQuery(lat, longi, km, false, null);
        BasicDBList list1 = new BasicDBList();
        list1.add(pat);
        BasicDBObject in = new BasicDBObject();
        in.append("$in", list1);
        distanceQuery.append("message", in);
        return distanceQuery;
    }
}
