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
                m.sender == null ||
                m.receipts == null ||
                m.senderId == null)
            return false;
        return true;
    }

    public static boolean validateComment(Comment c){
        if(c.message == null ||
                c.senderId == null ||
                c.sender == null)
            return false;
        return true;
    }
}
