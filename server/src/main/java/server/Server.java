package server;

import com.google.gson.Gson;

import dataaccess.DataAccessException;
import service.requests.*;
import service.results.*;
import spark.*;
import service.ClearService;
import service.UserService;
import model.UserData;

import javax.xml.crypto.Data;
import java.util.Map;
import java.util.Objects;

public class Server {

    private final ClearService clearService;
    private final UserService userService;

    public Server(ClearService clearService, UserService userService) {//

        this.clearService = clearService;
        this.userService = userService;

    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.delete("/db", this::deleteDatabase);
        Spark.post("/user", this::registerUser);
        Spark.post("/session", this::loginUser);
        Spark.delete("/session", this::logoutUser);
        Spark.get("/game", this::listGames);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object deleteDatabase(Request req, Response res) {
        clearService.clear();
        res.status(200);
        return "";
    }

    private Object registerUser(Request req, Response res) throws DataAccessException {
            RegisterRequest regRequest = new Gson().fromJson(req.body(), RegisterRequest.class);
            RegisterResult regResult = userService.registerUser(regRequest);

            res.status(200);
            return new Gson().toJson(regResult);

    }

    private Object loginUser(Request req, Response res) throws DataAccessException {
        LoginRequest loginRequest = new Gson().fromJson(req.body(), LoginRequest.class);
        LoginResult loginResult = userService.login(loginRequest);

        res.status(200);
        return new Gson().toJson(loginResult);
    }

    private Object logoutUser(Request req, Response res) throws DataAccessException {
        String authToken = req.headers("Authorization");
//        LogoutRequest logoutRequest = new Gson().fromJson(req.body(), LogoutRequest.class);
        userService.logout(authToken);
        res.status(200);

        return "{}";
    }

    private Objects listGames(Request req, Response res) throws DataAccessException {
        String authToken = req.headers("Authorization");

    }
}
