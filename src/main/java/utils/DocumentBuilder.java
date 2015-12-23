package utils;

import com.mongodb.BasicDBList;
import model.Meep;
import org.bson.Document;

/**
 * Created by santiagomarti on 12/14/15.
 */
public class DocumentBuilder {

    public static Document meepDocumentBuilder(Meep arg, boolean isRoot){
        Document meep = new Document();
        meep.append("sender", arg.sender);
        meep.append("message", arg.message);
        meep.append("type", arg.type);
        meep.append("facebookId", arg.facebookId);
        meep.append("twitterId", arg.twitterId);
        meep.append("isRoot", arg.isRoot);
        meep.append("picture", arg.picture);

        BasicDBList list = new BasicDBList();
        list.add(arg.longi);
        list.add(arg.lat);
        Document jobj = new Document();
        jobj.append("type", "Point");
        jobj.append("coordinates", list);

        meep.append("location", jobj);
        if(isRoot){
            BasicDBList comments = new BasicDBList();
            meep.append("comments", comments);
            meep.append("receipts", arg.receipts.toString());
            meep.append("isPublic", arg.isPublic);
        }
        return meep;
    }
}
