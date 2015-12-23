package model;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * Created by santiagomarti on 12/23/15.
 */
public class Comment {
    public String sender, message, userId, username, objectId;
    public Timestamp created_at, updated_at;
}
