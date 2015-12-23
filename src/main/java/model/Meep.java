package model;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * Created by santiagomarti on 12/11/15.
 */
public class Meep {
    public String sender, message, type, facebookId, twitterId, isPublic, isRoot, picture;
    public List<Map<String, String>> receipts;
    public double lat, longi;
    public Timestamp created_at, updated_at;
}
