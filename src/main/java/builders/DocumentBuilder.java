package builders;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import model.Category;
import model.Comment;
import model.Meep;
import org.bson.Document;
import org.bson.types.ObjectId;

import javax.print.Doc;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by santiagomarti on 12/14/15.
 */
public class DocumentBuilder {

    public static Document meepDocumentBuilder(Meep arg){
        Document meep = new Document();
        meep.append("senderName", arg.senderName);
        meep.append("senderId", arg.senderId);
        meep.append("message", arg.message);
        meep.append("type", arg.type);
        meep.append("isRoot", arg.isRoot);
        meep.append("pictureUrl", arg.pictureUrl);
        meep.append("commentCounter", 0);
        meep.append("likeCounter", 0);
        meep.append("viewCounter", 0);

        BasicDBList list = new BasicDBList();
        list.add(arg.longi);
        list.add(arg.lat);
        Document jobj = new Document();
        jobj.append("type", "Point");
        jobj.append("coordinates", list);
        meep.append("location", jobj);

        BasicDBList comments = new BasicDBList();
        meep.append("comments", comments);
        BasicDBList receipts = new BasicDBList();
        meep.append("receipts", receipts);
        BasicDBList registrees = new BasicDBList();
        meep.append("registrees", registrees);
        BasicDBList likes = new BasicDBList();
        meep.append("likes", likes);
        BasicDBList hashtags = new BasicDBList();
        if(arg.hashtags != null)
            for(String s : arg.hashtags)
                hashtags.add(s);
        meep.append("hashtags", hashtags);
        meep.append("isPublic", arg.isPublic);

        if(arg.categoryId >= 0)
            meep.append("categoryId", arg.categoryId);
        else
            meep.append("categoryId", Category.OTHER.getId());

        String createdAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
        meep.append("createdAt", createdAt);
        meep.append("updatedAt", createdAt);
        return meep;
    }

    public static Document commentDocumentBuilder(Comment arg){
        Document comment = new Document();
        String createdAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
        ObjectId newId = new ObjectId();
        comment.append("_id", newId);
        comment.append("senderName", arg.senderName);
        comment.append("senderId", arg.senderId);
        comment.append("message", arg.message);
        comment.append("type", arg.type);
        comment.append("pictureUrl", arg.pictureUrl);

        BasicDBList hashtags = new BasicDBList();
        if(arg.hashtags != null)
            for(String s : arg.hashtags)
                hashtags.add(s);

        comment.append("hashtags", hashtags);
        comment.append("createdAt", createdAt);
        comment.append("updatedAt", createdAt);
        return comment;
    }
}


