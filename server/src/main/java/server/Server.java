package server;


import static spark.Spark.*;
import spark.*; // WHY WON'T THIS WORK?????
import service.ChessService;

public class Server {
    private final ChessService service;
//    private final WebSocketHandler webSocketHandler;

    public Server(ChessService service) {
        this.service = service;
//        webSocketHandler = new WebSocketHandler();
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        //This line initializes the server and can be removed once you have a functioning endpoint 
//        Spark.init();

        Spark.delete("/db", this::deleteDatabase);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object deleteDatabase(Request req, Response res) {
        service.clear();
        res.status(200);
        return "";
    }


}
