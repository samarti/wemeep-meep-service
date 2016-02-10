package utils;

import model.Comment;
import model.Meep;

/**
 * Created by santiagomarti on 12/23/15.
 */
public class Validator {

    public static boolean validateMeep(Meep m) {
        if(m.isPublic == null ||
                m.lat == 0.0 ||
                m.longi == 0.0 ||
                m.message == null ||
                m.senderName == null ||
                m.receipts == null ||
                m.senderId == null)
            return false;
        return true;
    }

    public static boolean validateComment(Comment c){
        if(c.type == null || (!c.type.equals("text") && !c.type.equals("picture")))
            return false;
        if(c.type.equals("text"))
            if(c.message == null || c.senderId == null || c.senderName == null)
                return false;
        if(c.type.equals("picture"))
            if(c.senderId == null || c.senderName == null || c.pictureUrl == null)
                return false;
        return true;
    }
}
