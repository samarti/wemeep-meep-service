package model;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by the mighty and powerful santiagomarti on 12/11/15.
 * All those who dare, in any way, copying or somehow reproducing
 * the God-inspired earth-shattering 8th Wonder
 * code below, shall be punished for 47 days straight on the realms
 * of the 9th Circle of Hell, while watching "Friday" sang live by
 * Rebecca Black
 */
public class Meep {
    public String senderName, message, type, pictureUrl, objectId, senderId, twitterUserPicture;
    public List<HashMap<String, String>> receipts, registrees, likes;
    public LinkedList<String> hashtags;
    public double lat, longi;
    public Boolean isPublic, isRoot;
    public int commentCounter, likeCounter, viewCounter;
    public Timestamp created_at, updated_at;
    public int categoryId = -1;
}
