package model;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

/**
 * Created by santiagomarti on 12/11/15.
 */
public class Meep {
    public String senderName, message, type, picture, objectId, senderId;
    public List<HashMap<String, String>> receipts, registrees;
    public double lat, longi;
    public Boolean isPublic, isRoot;
    public Timestamp created_at, updated_at;
}
