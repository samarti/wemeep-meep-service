package twitter_feeder;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.endpoint.Location;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.endpoint.StatusesSampleEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.BasicClient;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import controllers.HashtagController;
import model.Category;
import org.bson.Document;

import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by santiagomarti on 1/11/16.
 */
public class Main {

    private static final int MIN_MEEP_DISTANCE = 50;

    public void init(MongoCollection<Document> meepCol) {
        BlockingQueue<String> queue = new LinkedBlockingQueue<>(10000);
        StatusesSampleEndpoint endpoint = new StatusesSampleEndpoint();
        endpoint.stallWarnings(false);
        Authentication auth = new OAuth1("FKzFwHjE3o1wb2pFqH0mr6cRJ", "xJ1TsuXwcNmoy8badgqx56DrLFeozx5vwjvQ3kBPtP006cEJtU",
                "3792449836-BYKKiZTnemmCFtvZoo5kwOCrhbB5bKRLIEDtQYs", "m4eE9BUvxprUnc7NmfgdxdaYVExha0NmwzW4vGoy7REiM");

        StatusesFilterEndpoint endpoint3 = new StatusesFilterEndpoint();
        endpoint3.locations(Lists.newArrayList(new Location(new Location.Coordinate(-74, -36), new Location.Coordinate(-65, -28))));

        BasicClient client = new ClientBuilder()
                .name("sampleExampleClient")
                .hosts(Constants.STREAM_HOST)
                .endpoint(endpoint3)
                .authentication(auth)
                .processor(new StringDelimitedProcessor(queue))
                .build();
        client.connect();

        JsonParser parser = new JsonParser();
        int validParsedMeeps;
        for (validParsedMeeps = 0; validParsedMeeps < 100;) {
            if (client.isDone()) {
                System.out.println("Client connection closed unexpectedly: " + client.getExitEvent().getMessage());
                break;
            }

            String msg = null;
            try {
                msg = queue.poll(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (msg == null) {
                System.out.println("Did not receive a message in 10 seconds");
            } else {
                JsonObject aux = parser.parse(msg).getAsJsonObject();
                Document meep = meepDocumentBuilder(aux, meepCol);
                if(meep != null){
                    validParsedMeeps++;
                    meepCol.insertOne(meep);
                }
            }
        }
        client.stop();
        System.out.printf("The client read %d messages!\n", client.getStatsTracker().getNumMessages());
    }

    public static Document meepDocumentBuilder(JsonObject arg, MongoCollection<Document> meepCol){
        Document meep = new Document();
        if(arg.get("coordinates").isJsonNull())
            return null;
        double longi = arg.getAsJsonObject("coordinates").getAsJsonArray("coordinates").get(0).getAsDouble();
        double lat = arg.getAsJsonObject("coordinates").getAsJsonArray("coordinates").get(1).getAsDouble();
        BasicDBObject aux = getCloseMeepsQuery(lat, longi);
        FindIterable<Document> auxCol = meepCol.find(aux);
        if(auxCol.first() != null) {
            System.out.println("Received valid tweet but too close to another");
            return null;
        }
        System.out.println("Received valid tweet");
        meep.append("senderName", arg.getAsJsonObject("user").getAsJsonPrimitive("screen_name").getAsString());
        meep.append("senderId", arg.getAsJsonObject("user").getAsJsonPrimitive("id").getAsInt());
        meep.append("message", arg.getAsJsonPrimitive("text").getAsString());
        meep.append("type", "tweet");
        meep.append("isRoot", true);
        meep.append("commentCounter", 0);
        meep.append("likeCounter", 0);
        meep.append("viewCounter", 0);
        /*if(arg.getAsJsonObject("entities").has("media"))
            meep.append("picture", arg.getAsJsonObject("entities").getAsJsonObject("media").get("media_url").getAsString());
        else*/
        meep.append("picture", null);
        BasicDBList list = new BasicDBList();
        list.add(longi);
        list.add(lat);
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
        BasicDBList likes = new BasicDBList();
        meep.append("likes", likes);

        meep.append("categoryId", Category.TWITTER.getId());

        BasicDBList hashtags = new BasicDBList();
        HashtagController controller = new HashtagController();
        LinkedList<String> aux2 = controller.extractHashtags(arg.getAsJsonPrimitive("text").getAsString());
        for(String s : aux2)
            hashtags.add(s);
        meep.append("hashtags", hashtags);

        meep.append("isPublic", true);
        return meep;
    }

    public static BasicDBObject getCloseMeepsQuery(double lat, double longi){
        BasicDBList coords = new BasicDBList();
        coords.add(longi);
        coords.add(lat);

        BasicDBObject geo = new BasicDBObject();
        geo.append("type", "Point");
        geo.append("coordinates", coords);

        BasicDBObject near = new BasicDBObject();
        near.append("$near", geo);
        near.append("$maxDistance", MIN_MEEP_DISTANCE);

        BasicDBObject loc = new BasicDBObject();
        loc.append("location",near);
        return loc;
    }
}
