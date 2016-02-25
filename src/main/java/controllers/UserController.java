package controllers;

import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * Created by Santiago Martí Olbrich (samarti@uc.cl) on 2/25/16.
 * Resit SpA.
 * All rights reserved
 */
public class UserController {

    public static final String profilePictureField = "profilePictureUrl";

    public String getUserProfilePictureUrl(String userId){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(ApiController.USER_SERVICE_URL + "users/" + userId)
                .build();
        Response responses;
        try {
            responses = client.newCall(request).execute();
            String result = responses.body().string();
            if(result != null) {
                JsonParser parser = new JsonParser();
                return parser.parse(result).getAsJsonObject().get(profilePictureField).getAsString();
            } else
                return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
