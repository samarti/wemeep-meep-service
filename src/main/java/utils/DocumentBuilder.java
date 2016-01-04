package utils;

import com.mongodb.BasicDBList;
import model.Comment;
import model.Meep;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.text.SimpleDateFormat;
import java.util.Date;

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
        meep.append("picture", arg.picture);

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
        meep.append("isPublic", arg.isPublic);
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
        comment.append("createdAt", createdAt);
        comment.append("updatedAt", createdAt);
        return comment;
    }
}


