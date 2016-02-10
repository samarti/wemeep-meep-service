package model;

import java.util.List;

/**
 * Created by santiagomarti on 12/23/15.
 */
public class Comment {
    public String senderName, message, senderId, objectId, type, pictureUrl;
    public List<String> hashtags;
    public String createdAt, updatedAt;
}
