package service;

import static spark.Spark.*;

/**
 * Created by santiagomarti on 12/11/15.
 */
public class Server {

    public static void main(String[] args) {
        get("/hello", (request, response) -> "Hello World!");
    }
}
