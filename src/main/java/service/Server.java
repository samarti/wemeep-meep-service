package service;

import controllers.ApiController;

import java.util.TimeZone;

import static spark.Spark.*;

/**
 * Created by santiagomarti on 12/11/15.
 */
public class Server {


    public static void main(String[] args) {

        TimeZone.setDefault(TimeZone.getTimeZone("America/Santiago"));

        ApiController.init();

        get("/", (request, response) -> "WeMeep Meep Service");

        get("/meeps/:id", (request, response) -> ApiController.getMeep(response, request).body());

        get("/meeps/:id/comments", (request, response) -> ApiController.getComments(response, request).body());

        get("/meeps", (request, response) -> ApiController.getMeeps(response, request).body());

        get("/meeps/:id/registrees", (request, response) -> ApiController.getRegistrees(response, request).body());

        get("/meeps/:id/receipts", (request, response) -> ApiController.getReceipts(response, request).body());

        get("/searchmeep", (request, response) -> ApiController.searchMeep(response, request).body());

        get("/seed", (request, response) -> ApiController.seed(response, request).body());

        get("/meeps/:id/likes", (request, response) -> ApiController.hasLiked(response, request).body());

        get("/usermeeps/:id", (request, response) -> ApiController.getUserMeeps(response, request).body());

        get("/categories", (request, response) -> ApiController.getCategories(response, request).body());

        post("/meeps", (request, response) -> ApiController.createMeep(response, request).body());

        post("/meeps/:id/comments", (request, response) -> ApiController.createComment(response, request).body());

        put("/meeps/:id/newview", (request, response) -> ApiController.newView(response, request).body());

        put("/meeps/:id/likes", (request, response) -> ApiController.changeLikeStatus(response, request).body());

        put("/meeps/:id/registrees", (request, response) -> ApiController.changeRegistryRelation(response, request).body());

        put("/meeps/:id", (request, response) -> ApiController.updateMeep(response, request).body());
    }
}
