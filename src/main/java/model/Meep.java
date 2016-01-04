package model;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * Created by santiagomarti on 12/11/15.
 */
public class Meep {
    public String senderName, message, type, picture, objectId, senderId;
    public List<Map<String, String>> receipts, registrees;
    public double lat, longi;
    public Boolean isPublic, isRoot;
    public Timestamp created_at, updated_at;
}
