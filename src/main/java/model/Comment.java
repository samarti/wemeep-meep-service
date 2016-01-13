package model;

import java.sql.Timestamp;
import java.util.LinkedList;

/**
 * Created by santiagomarti on 12/23/15.
 */
public class Comment {
    public String senderName, message, senderId, objectId;
    public LinkedList<String> hashtags;
    public String createdAt, updatedAt;
}
